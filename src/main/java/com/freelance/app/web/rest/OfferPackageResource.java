package com.freelance.app.web.rest;

import com.freelance.app.domain.OfferPackage;
import com.freelance.app.domain.criteria.OfferPackageCriteria;
import com.freelance.app.repository.OfferPackageRepository;
import com.freelance.app.service.OfferPackageService;
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
 * REST controller for managing {@link com.freelance.app.domain.OfferPackage}.
 */
@RestController
@RequestMapping("/api/offer-packages")
public class OfferPackageResource {

    private static final Logger LOG = LoggerFactory.getLogger(OfferPackageResource.class);

    private static final String ENTITY_NAME = "offerPackage";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final OfferPackageService offerPackageService;

    private final OfferPackageRepository offerPackageRepository;

    public OfferPackageResource(OfferPackageService offerPackageService, OfferPackageRepository offerPackageRepository) {
        this.offerPackageService = offerPackageService;
        this.offerPackageRepository = offerPackageRepository;
    }

    /**
     * {@code POST  /offer-packages} : Create a new offerPackage.
     *
     * @param offerPackage the offerPackage to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new offerPackage, or with status {@code 400 (Bad Request)} if the offerPackage has already an ID.
     */
    @PostMapping("")
    public Mono<ResponseEntity<OfferPackage>> createOfferPackage(@Valid @RequestBody OfferPackage offerPackage) {
        LOG.debug("REST request to save OfferPackage : {}", offerPackage);
        if (offerPackage.getId() != null) {
            throw new BadRequestAlertException("A new offerPackage cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return offerPackageService
            .save(offerPackage)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/offer-packages/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /offer-packages/:id} : Updates an existing offerPackage.
     *
     * @param id the id of the offerPackage to save.
     * @param offerPackage the offerPackage to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated offerPackage,
     * or with status {@code 400 (Bad Request)} if the offerPackage is not valid,
     * or with status {@code 500 (Internal Server Error)} if the offerPackage couldn't be updated.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<OfferPackage>> updateOfferPackage(
        @PathVariable(required = false) final Long id,
        @Valid @RequestBody OfferPackage offerPackage
    ) {
        LOG.debug("REST request to update OfferPackage : {}, {}", id, offerPackage);
        if (offerPackage.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, offerPackage.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return offerPackageRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return offerPackageService
                    .update(offerPackage)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /offer-packages/:id} : Partial updates given fields of an existing offerPackage, field will ignore if it is null
     *
     * @param id the id of the offerPackage to save.
     * @param offerPackage the offerPackage to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated offerPackage,
     * or with status {@code 400 (Bad Request)} if the offerPackage is not valid,
     * or with status {@code 404 (Not Found)} if the offerPackage is not found,
     * or with status {@code 500 (Internal Server Error)} if the offerPackage couldn't be updated.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<OfferPackage>> partialUpdateOfferPackage(
        @PathVariable(required = false) final Long id,
        @NotNull @RequestBody OfferPackage offerPackage
    ) {
        LOG.debug("REST request to partial update OfferPackage partially : {}, {}", id, offerPackage);
        if (offerPackage.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, offerPackage.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return offerPackageRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<OfferPackage> result = offerPackageService.partialUpdate(offerPackage);

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
     * {@code GET  /offer-packages} : get all the offerPackages.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of offerPackages in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<OfferPackage>>> getAllOfferPackages(
        OfferPackageCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get OfferPackages by criteria: {}", criteria);
        return offerPackageService
            .countByCriteria(criteria)
            .zipWith(offerPackageService.findByCriteria(criteria, pageable).collectList())
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
     * {@code GET  /offer-packages/count} : count all the offerPackages.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countOfferPackages(OfferPackageCriteria criteria) {
        LOG.debug("REST request to count OfferPackages by criteria: {}", criteria);
        return offerPackageService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /offer-packages/:id} : get the "id" offerPackage.
     *
     * @param id the id of the offerPackage to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the offerPackage, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<OfferPackage>> getOfferPackage(@PathVariable Long id) {
        LOG.debug("REST request to get OfferPackage : {}", id);
        Mono<OfferPackage> offerPackage = offerPackageService.findOne(id);
        return ResponseUtil.wrapOrNotFound(offerPackage);
    }

    /**
     * {@code DELETE  /offer-packages/:id} : delete the "id" offerPackage.
     *
     * @param id the id of the offerPackage to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteOfferPackage(@PathVariable Long id) {
        LOG.debug("REST request to delete OfferPackage : {}", id);
        return offerPackageService
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
