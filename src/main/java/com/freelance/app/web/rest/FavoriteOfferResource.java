package com.freelance.app.web.rest;

import com.freelance.app.domain.FavoriteOffer;
import com.freelance.app.domain.criteria.FavoriteOfferCriteria;
import com.freelance.app.repository.FavoriteOfferRepository;
import com.freelance.app.service.FavoriteOfferService;
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
 * REST controller for managing {@link com.freelance.app.domain.FavoriteOffer}.
 */
@RestController
@RequestMapping("/api/favorite-offers")
public class FavoriteOfferResource {

    private static final Logger LOG = LoggerFactory.getLogger(FavoriteOfferResource.class);

    private static final String ENTITY_NAME = "favoriteOffer";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FavoriteOfferService favoriteOfferService;

    private final FavoriteOfferRepository favoriteOfferRepository;

    public FavoriteOfferResource(FavoriteOfferService favoriteOfferService, FavoriteOfferRepository favoriteOfferRepository) {
        this.favoriteOfferService = favoriteOfferService;
        this.favoriteOfferRepository = favoriteOfferRepository;
    }

    /**
     * {@code POST  /favorite-offers} : Create a new favoriteOffer.
     *
     * @param favoriteOffer the favoriteOffer to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new favoriteOffer, or with status {@code 400 (Bad Request)} if the favoriteOffer has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<FavoriteOffer>> createFavoriteOffer(@Valid @RequestBody FavoriteOffer favoriteOffer)
        throws URISyntaxException {
        LOG.debug("REST request to save FavoriteOffer : {}", favoriteOffer);
        if (favoriteOffer.getId() != null) {
            throw new BadRequestAlertException("A new favoriteOffer cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return favoriteOfferService
            .save(favoriteOffer)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/favorite-offers/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /favorite-offers/:id} : Updates an existing favoriteOffer.
     *
     * @param id the id of the favoriteOffer to save.
     * @param favoriteOffer the favoriteOffer to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated favoriteOffer,
     * or with status {@code 400 (Bad Request)} if the favoriteOffer is not valid,
     * or with status {@code 500 (Internal Server Error)} if the favoriteOffer couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<FavoriteOffer>> updateFavoriteOffer(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody FavoriteOffer favoriteOffer
    ) throws URISyntaxException {
        LOG.debug("REST request to update FavoriteOffer : {}, {}", id, favoriteOffer);
        if (favoriteOffer.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, favoriteOffer.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return favoriteOfferRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return favoriteOfferService
                    .update(favoriteOffer)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /favorite-offers/:id} : Partial updates given fields of an existing favoriteOffer, field will ignore if it is null
     *
     * @param id the id of the favoriteOffer to save.
     * @param favoriteOffer the favoriteOffer to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated favoriteOffer,
     * or with status {@code 400 (Bad Request)} if the favoriteOffer is not valid,
     * or with status {@code 404 (Not Found)} if the favoriteOffer is not found,
     * or with status {@code 500 (Internal Server Error)} if the favoriteOffer couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<FavoriteOffer>> partialUpdateFavoriteOffer(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody FavoriteOffer favoriteOffer
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update FavoriteOffer partially : {}, {}", id, favoriteOffer);
        if (favoriteOffer.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, favoriteOffer.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return favoriteOfferRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<FavoriteOffer> result = favoriteOfferService.partialUpdate(favoriteOffer);

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
     * {@code GET  /favorite-offers} : get all the favoriteOffers.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of favoriteOffers in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<FavoriteOffer>>> getAllFavoriteOffers(
        FavoriteOfferCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get FavoriteOffers by criteria: {}", criteria);
        return favoriteOfferService
            .countByCriteria(criteria)
            .zipWith(favoriteOfferService.findByCriteria(criteria, pageable).collectList())
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
     * {@code GET  /favorite-offers/count} : count all the favoriteOffers.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countFavoriteOffers(FavoriteOfferCriteria criteria) {
        LOG.debug("REST request to count FavoriteOffers by criteria: {}", criteria);
        return favoriteOfferService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /favorite-offers/:id} : get the "id" favoriteOffer.
     *
     * @param id the id of the favoriteOffer to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the favoriteOffer, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<FavoriteOffer>> getFavoriteOffer(@PathVariable("id") Long id) {
        LOG.debug("REST request to get FavoriteOffer : {}", id);
        Mono<FavoriteOffer> favoriteOffer = favoriteOfferService.findOne(id);
        return ResponseUtil.wrapOrNotFound(favoriteOffer);
    }

    /**
     * {@code DELETE  /favorite-offers/:id} : delete the "id" favoriteOffer.
     *
     * @param id the id of the favoriteOffer to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteFavoriteOffer(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete FavoriteOffer : {}", id);
        return favoriteOfferService
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
