package com.freelance.app.web.rest;

import com.freelance.app.domain.OfferMedia;
import com.freelance.app.domain.criteria.OfferMediaCriteria;
import com.freelance.app.repository.OfferMediaRepository;
import com.freelance.app.service.OfferMediaService;
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
 * REST controller for managing {@link com.freelance.app.domain.OfferMedia}.
 */
@RestController
@RequestMapping("/api/offer-medias")
public class OfferMediaResource {

    private static final Logger LOG = LoggerFactory.getLogger(OfferMediaResource.class);

    private static final String ENTITY_NAME = "offerMedia";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final OfferMediaService offerMediaService;

    private final OfferMediaRepository offerMediaRepository;

    public OfferMediaResource(OfferMediaService offerMediaService, OfferMediaRepository offerMediaRepository) {
        this.offerMediaService = offerMediaService;
        this.offerMediaRepository = offerMediaRepository;
    }

    /**
     * {@code POST  /offer-medias} : Create a new offerMedia.
     *
     * @param offerMedia the offerMedia to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new offerMedia, or with status {@code 400 (Bad Request)} if the offerMedia has already an ID.
     */
    @PostMapping("")
    public Mono<ResponseEntity<OfferMedia>> createOfferMedia(@Valid @RequestBody OfferMedia offerMedia) {
        LOG.debug("REST request to save OfferMedia : {}", offerMedia);
        if (offerMedia.getId() != null) {
            throw new BadRequestAlertException("A new offerMedia cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return offerMediaService
            .save(offerMedia)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/offer-medias/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /offer-medias/:id} : Updates an existing offerMedia.
     *
     * @param id the id of the offerMedia to save.
     * @param offerMedia the offerMedia to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated offerMedia,
     * or with status {@code 400 (Bad Request)} if the offerMedia is not valid,
     * or with status {@code 500 (Internal Server Error)} if the offerMedia couldn't be updated.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<OfferMedia>> updateOfferMedia(
        @PathVariable(required = false) final Long id,
        @Valid @RequestBody OfferMedia offerMedia
    ) {
        LOG.debug("REST request to update OfferMedia : {}, {}", id, offerMedia);
        if (offerMedia.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, offerMedia.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return offerMediaRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return offerMediaService
                    .update(offerMedia)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /offer-medias/:id} : Partial updates given fields of an existing offerMedia, field will ignore if it is null
     *
     * @param id the id of the offerMedia to save.
     * @param offerMedia the offerMedia to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated offerMedia,
     * or with status {@code 400 (Bad Request)} if the offerMedia is not valid,
     * or with status {@code 404 (Not Found)} if the offerMedia is not found,
     * or with status {@code 500 (Internal Server Error)} if the offerMedia couldn't be updated.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<OfferMedia>> partialUpdateOfferMedia(
        @PathVariable(required = false) final Long id,
        @NotNull @RequestBody OfferMedia offerMedia
    ) {
        LOG.debug("REST request to partial update OfferMedia partially : {}, {}", id, offerMedia);
        if (offerMedia.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, offerMedia.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return offerMediaRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<OfferMedia> result = offerMediaService.partialUpdate(offerMedia);

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
     * {@code GET  /offer-medias} : get all the offerMedias.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of offerMedias in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<OfferMedia>>> getAllOfferMedias(
        OfferMediaCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get OfferMedias by criteria: {}", criteria);
        return offerMediaService
            .countByCriteria(criteria)
            .zipWith(offerMediaService.findByCriteria(criteria, pageable).collectList())
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
     * {@code GET  /offer-medias/count} : count all the offerMedias.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countOfferMedias(OfferMediaCriteria criteria) {
        LOG.debug("REST request to count OfferMedias by criteria: {}", criteria);
        return offerMediaService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /offer-medias/:id} : get the "id" offerMedia.
     *
     * @param id the id of the offerMedia to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the offerMedia, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<OfferMedia>> getOfferMedia(@PathVariable Long id) {
        LOG.debug("REST request to get OfferMedia : {}", id);
        Mono<OfferMedia> offerMedia = offerMediaService.findOne(id);
        return ResponseUtil.wrapOrNotFound(offerMedia);
    }

    /**
     * {@code DELETE  /offer-medias/:id} : delete the "id" offerMedia.
     *
     * @param id the id of the offerMedia to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteOfferMedia(@PathVariable Long id) {
        LOG.debug("REST request to delete OfferMedia : {}", id);
        return offerMediaService
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
