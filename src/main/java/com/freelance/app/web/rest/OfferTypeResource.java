package com.freelance.app.web.rest;

import com.freelance.app.domain.OfferType;
import com.freelance.app.domain.criteria.OfferTypeCriteria;
import com.freelance.app.repository.OfferTypeRepository;
import com.freelance.app.service.OfferTypeService;
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
 * REST controller for managing {@link com.freelance.app.domain.OfferType}.
 */
@RestController
@RequestMapping("/api/offer-types")
public class OfferTypeResource {

    private static final Logger LOG = LoggerFactory.getLogger(OfferTypeResource.class);

    private static final String ENTITY_NAME = "offerType";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final OfferTypeService offerTypeService;

    private final OfferTypeRepository offerTypeRepository;

    public OfferTypeResource(OfferTypeService offerTypeService, OfferTypeRepository offerTypeRepository) {
        this.offerTypeService = offerTypeService;
        this.offerTypeRepository = offerTypeRepository;
    }

    /**
     * {@code POST  /offer-types} : Create a new offerType.
     *
     * @param offerType the offerType to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new offerType, or with status {@code 400 (Bad Request)} if the offerType has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<OfferType>> createOfferType(@Valid @RequestBody OfferType offerType) throws URISyntaxException {
        LOG.debug("REST request to save OfferType : {}", offerType);
        if (offerType.getId() != null) {
            throw new BadRequestAlertException("A new offerType cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return offerTypeService
            .save(offerType)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/offer-types/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /offer-types/:id} : Updates an existing offerType.
     *
     * @param id the id of the offerType to save.
     * @param offerType the offerType to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated offerType,
     * or with status {@code 400 (Bad Request)} if the offerType is not valid,
     * or with status {@code 500 (Internal Server Error)} if the offerType couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<OfferType>> updateOfferType(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody OfferType offerType
    ) throws URISyntaxException {
        LOG.debug("REST request to update OfferType : {}, {}", id, offerType);
        if (offerType.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, offerType.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return offerTypeRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return offerTypeService
                    .update(offerType)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /offer-types/:id} : Partial updates given fields of an existing offerType, field will ignore if it is null
     *
     * @param id the id of the offerType to save.
     * @param offerType the offerType to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated offerType,
     * or with status {@code 400 (Bad Request)} if the offerType is not valid,
     * or with status {@code 404 (Not Found)} if the offerType is not found,
     * or with status {@code 500 (Internal Server Error)} if the offerType couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<OfferType>> partialUpdateOfferType(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody OfferType offerType
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update OfferType partially : {}, {}", id, offerType);
        if (offerType.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, offerType.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return offerTypeRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<OfferType> result = offerTypeService.partialUpdate(offerType);

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
     * {@code GET  /offer-types} : get all the offerTypes.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of offerTypes in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<OfferType>>> getAllOfferTypes(
        OfferTypeCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get OfferTypes by criteria: {}", criteria);
        return offerTypeService
            .countByCriteria(criteria)
            .zipWith(offerTypeService.findByCriteria(criteria, pageable).collectList())
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
     * {@code GET  /offer-types/count} : count all the offerTypes.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countOfferTypes(OfferTypeCriteria criteria) {
        LOG.debug("REST request to count OfferTypes by criteria: {}", criteria);
        return offerTypeService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /offer-types/:id} : get the "id" offerType.
     *
     * @param id the id of the offerType to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the offerType, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<OfferType>> getOfferType(@PathVariable("id") Long id) {
        LOG.debug("REST request to get OfferType : {}", id);
        Mono<OfferType> offerType = offerTypeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(offerType);
    }

    /**
     * {@code DELETE  /offer-types/:id} : delete the "id" offerType.
     *
     * @param id the id of the offerType to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteOfferType(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete OfferType : {}", id);
        return offerTypeService
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
