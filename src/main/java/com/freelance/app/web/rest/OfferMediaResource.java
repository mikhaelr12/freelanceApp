package com.freelance.app.web.rest;

import com.freelance.app.domain.criteria.OfferMediaCriteria;
import com.freelance.app.repository.OfferMediaRepository;
import com.freelance.app.service.OfferMediaService;
import com.freelance.app.service.dto.OfferMediaDTO;
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
     * @param offerMediaDTO the offerMediaDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new offerMediaDTO, or with status {@code 400 (Bad Request)} if the offerMedia has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<OfferMediaDTO>> createOfferMedia(@Valid @RequestBody OfferMediaDTO offerMediaDTO) throws URISyntaxException {
        LOG.debug("REST request to save OfferMedia : {}", offerMediaDTO);
        if (offerMediaDTO.getId() != null) {
            throw new BadRequestAlertException("A new offerMedia cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return offerMediaService
            .save(offerMediaDTO)
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
     * @param id the id of the offerMediaDTO to save.
     * @param offerMediaDTO the offerMediaDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated offerMediaDTO,
     * or with status {@code 400 (Bad Request)} if the offerMediaDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the offerMediaDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<OfferMediaDTO>> updateOfferMedia(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody OfferMediaDTO offerMediaDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update OfferMedia : {}, {}", id, offerMediaDTO);
        if (offerMediaDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, offerMediaDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return offerMediaRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return offerMediaService
                    .update(offerMediaDTO)
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
     * @param id the id of the offerMediaDTO to save.
     * @param offerMediaDTO the offerMediaDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated offerMediaDTO,
     * or with status {@code 400 (Bad Request)} if the offerMediaDTO is not valid,
     * or with status {@code 404 (Not Found)} if the offerMediaDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the offerMediaDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<OfferMediaDTO>> partialUpdateOfferMedia(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody OfferMediaDTO offerMediaDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update OfferMedia partially : {}, {}", id, offerMediaDTO);
        if (offerMediaDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, offerMediaDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return offerMediaRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<OfferMediaDTO> result = offerMediaService.partialUpdate(offerMediaDTO);

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
    public Mono<ResponseEntity<List<OfferMediaDTO>>> getAllOfferMedias(
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
     * @param id the id of the offerMediaDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the offerMediaDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<OfferMediaDTO>> getOfferMedia(@PathVariable("id") Long id) {
        LOG.debug("REST request to get OfferMedia : {}", id);
        Mono<OfferMediaDTO> offerMediaDTO = offerMediaService.findOne(id);
        return ResponseUtil.wrapOrNotFound(offerMediaDTO);
    }

    /**
     * {@code DELETE  /offer-medias/:id} : delete the "id" offerMedia.
     *
     * @param id the id of the offerMediaDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteOfferMedia(@PathVariable("id") Long id) {
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
