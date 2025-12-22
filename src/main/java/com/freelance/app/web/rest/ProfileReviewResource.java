package com.freelance.app.web.rest;

import com.freelance.app.domain.ProfileReview;
import com.freelance.app.domain.criteria.ProfileReviewCriteria;
import com.freelance.app.repository.ProfileReviewRepository;
import com.freelance.app.service.ProfileReviewService;
import com.freelance.app.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.ForwardedHeaderUtils;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.freelance.app.domain.ProfileReview}.
 */
@RestController
@RequestMapping("/api/profile-reviews")
public class ProfileReviewResource {

    private static final Logger LOG = LoggerFactory.getLogger(ProfileReviewResource.class);

    private static final String ENTITY_NAME = "profileReview";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProfileReviewService profileReviewService;

    private final ProfileReviewRepository profileReviewRepository;

    public ProfileReviewResource(ProfileReviewService profileReviewService, ProfileReviewRepository profileReviewRepository) {
        this.profileReviewService = profileReviewService;
        this.profileReviewRepository = profileReviewRepository;
    }

    /**
     * {@code POST  /profile-reviews} : Create a new profileReview.
     *
     * @param profileReview the profileReview to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new profileReview, or with status {@code 400 (Bad Request)} if the profileReview has already an ID.
     */
    @PostMapping("")
    public Mono<ResponseEntity<ProfileReview>> createProfileReview(@Valid @RequestBody ProfileReview profileReview) {
        LOG.debug("REST request to save ProfileReview : {}", profileReview);
        if (profileReview.getId() != null) {
            throw new BadRequestAlertException("A new profileReview cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return profileReviewService
            .save(profileReview)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/profile-reviews/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /profile-reviews/:id} : Updates an existing profileReview.
     *
     * @param id the id of the profileReview to save.
     * @param profileReview the profileReview to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated profileReview,
     * or with status {@code 400 (Bad Request)} if the profileReview is not valid,
     * or with status {@code 500 (Internal Server Error)} if the profileReview couldn't be updated.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<ProfileReview>> updateProfileReview(
        @PathVariable(required = false) final Long id,
        @Valid @RequestBody ProfileReview profileReview
    ) {
        LOG.debug("REST request to update ProfileReview : {}, {}", id, profileReview);
        if (profileReview.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, profileReview.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return profileReviewRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return profileReviewService
                    .update(profileReview)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /profile-reviews/:id} : Partial updates given fields of an existing profileReview, field will ignore if it is null
     *
     * @param id the id of the profileReview to save.
     * @param profileReview the profileReview to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated profileReview,
     * or with status {@code 400 (Bad Request)} if the profileReview is not valid,
     * or with status {@code 404 (Not Found)} if the profileReview is not found,
     * or with status {@code 500 (Internal Server Error)} if the profileReview couldn't be updated.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<ProfileReview>> partialUpdateProfileReview(
        @PathVariable(required = false) final Long id,
        @NotNull @RequestBody ProfileReview profileReview
    ) {
        LOG.debug("REST request to partial update ProfileReview partially : {}, {}", id, profileReview);
        if (profileReview.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, profileReview.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return profileReviewRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<ProfileReview> result = profileReviewService.partialUpdate(profileReview);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /profile-reviews} : get all the profileReviews.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of profileReviews in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<ProfileReview>>> getAllProfileReviews(
        ProfileReviewCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get ProfileReviews by criteria: {}", criteria);
        return profileReviewService
            .countByCriteria(criteria)
            .zipWith(profileReviewService.findByCriteria(criteria, pageable).collectList())
            .map(countWithEntities ->
                ResponseEntity.ok()
                    .headers(
                        PaginationUtil.generatePaginationHttpHeaders(
                            ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                            new PageImpl<>(countWithEntities.getT2(), pageable, countWithEntities.getT1())
                        )
                    )
                    .body(countWithEntities.getT2())
            );
    }

    /**
     * {@code GET  /profile-reviews/count} : count all the profileReviews.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countProfileReviews(ProfileReviewCriteria criteria) {
        LOG.debug("REST request to count ProfileReviews by criteria: {}", criteria);
        return profileReviewService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /profile-reviews/:id} : get the "id" profileReview.
     *
     * @param id the id of the profileReview to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the profileReview, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<ProfileReview>> getProfileReview(@PathVariable Long id) {
        LOG.debug("REST request to get ProfileReview : {}", id);
        Mono<ProfileReview> profileReview = profileReviewService.findOne(id);
        return ResponseUtil.wrapOrNotFound(profileReview);
    }

    /**
     * {@code DELETE  /profile-reviews/:id} : delete the "id" profileReview.
     *
     * @param id the id of the profileReview to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteProfileReview(@PathVariable Long id) {
        LOG.debug("REST request to delete ProfileReview : {}", id);
        return profileReviewService
            .delete(id)
            .then(
                Mono.just(
                    ResponseEntity.noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }
}
