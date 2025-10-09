package com.freelance.app.web.rest;

import com.freelance.app.domain.criteria.OfferReviewCriteria;
import com.freelance.app.repository.OfferReviewRepository;
import com.freelance.app.service.OfferReviewService;
import com.freelance.app.service.dto.OfferReviewDTO;
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
 * REST controller for managing {@link com.freelance.app.domain.OfferReview}.
 */
@RestController
@RequestMapping("/api/offer-reviews")
public class OfferReviewResource {

    private static final Logger LOG = LoggerFactory.getLogger(OfferReviewResource.class);

    private static final String ENTITY_NAME = "offerReview";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final OfferReviewService offerReviewService;

    private final OfferReviewRepository offerReviewRepository;

    public OfferReviewResource(OfferReviewService offerReviewService, OfferReviewRepository offerReviewRepository) {
        this.offerReviewService = offerReviewService;
        this.offerReviewRepository = offerReviewRepository;
    }

    /**
     * {@code POST  /offer-reviews} : Create a new offerReview.
     *
     * @param offerReviewDTO the offerReviewDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new offerReviewDTO, or with status {@code 400 (Bad Request)} if the offerReview has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<OfferReviewDTO>> createOfferReview(@Valid @RequestBody OfferReviewDTO offerReviewDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save OfferReview : {}", offerReviewDTO);
        if (offerReviewDTO.getId() != null) {
            throw new BadRequestAlertException("A new offerReview cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return offerReviewService
            .save(offerReviewDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/offer-reviews/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /offer-reviews/:id} : Updates an existing offerReview.
     *
     * @param id the id of the offerReviewDTO to save.
     * @param offerReviewDTO the offerReviewDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated offerReviewDTO,
     * or with status {@code 400 (Bad Request)} if the offerReviewDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the offerReviewDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<OfferReviewDTO>> updateOfferReview(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody OfferReviewDTO offerReviewDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update OfferReview : {}, {}", id, offerReviewDTO);
        if (offerReviewDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, offerReviewDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return offerReviewRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return offerReviewService
                    .update(offerReviewDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /offer-reviews/:id} : Partial updates given fields of an existing offerReview, field will ignore if it is null
     *
     * @param id the id of the offerReviewDTO to save.
     * @param offerReviewDTO the offerReviewDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated offerReviewDTO,
     * or with status {@code 400 (Bad Request)} if the offerReviewDTO is not valid,
     * or with status {@code 404 (Not Found)} if the offerReviewDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the offerReviewDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<OfferReviewDTO>> partialUpdateOfferReview(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody OfferReviewDTO offerReviewDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update OfferReview partially : {}, {}", id, offerReviewDTO);
        if (offerReviewDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, offerReviewDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return offerReviewRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<OfferReviewDTO> result = offerReviewService.partialUpdate(offerReviewDTO);

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
     * {@code GET  /offer-reviews} : get all the offerReviews.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of offerReviews in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<OfferReviewDTO>>> getAllOfferReviews(
        OfferReviewCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get OfferReviews by criteria: {}", criteria);
        return offerReviewService
            .countByCriteria(criteria)
            .zipWith(offerReviewService.findByCriteria(criteria, pageable).collectList())
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
     * {@code GET  /offer-reviews/count} : count all the offerReviews.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countOfferReviews(OfferReviewCriteria criteria) {
        LOG.debug("REST request to count OfferReviews by criteria: {}", criteria);
        return offerReviewService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /offer-reviews/:id} : get the "id" offerReview.
     *
     * @param id the id of the offerReviewDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the offerReviewDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<OfferReviewDTO>> getOfferReview(@PathVariable("id") Long id) {
        LOG.debug("REST request to get OfferReview : {}", id);
        Mono<OfferReviewDTO> offerReviewDTO = offerReviewService.findOne(id);
        return ResponseUtil.wrapOrNotFound(offerReviewDTO);
    }

    /**
     * {@code DELETE  /offer-reviews/:id} : delete the "id" offerReview.
     *
     * @param id the id of the offerReviewDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteOfferReview(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete OfferReview : {}", id);
        return offerReviewService
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
