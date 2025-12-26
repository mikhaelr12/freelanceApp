package com.freelance.app.web.rest;

import com.freelance.app.domain.FavoriteOffer;
import com.freelance.app.domain.criteria.FavoriteOfferCriteria;
import com.freelance.app.service.FavoriteOfferService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
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

    public FavoriteOfferResource(FavoriteOfferService favoriteOfferService) {
        this.favoriteOfferService = favoriteOfferService;
    }

    /**
     * {@code POST  /favorite-offers} : Create a new favoriteOffer.
     *
     * @param offerId id of the offer to be added in favorites
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new favoriteOffer, or with status {@code 400 (Bad Request)} if the favoriteOffer has already an ID.
     */
    @PostMapping("/{offerId}")
    public Mono<ResponseEntity<FavoriteOffer>> createFavoriteOffer(@PathVariable Long offerId) {
        LOG.debug("REST request to create FavoriteOffer for Offer with id: {}", offerId);
        return favoriteOfferService.createFavoriteOffer(offerId).map(ResponseEntity::ok);
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
     * {@code GET  /favorite-offers/:id} : get the "id" favoriteOffer.
     *
     * @param id the id of the favoriteOffer to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the favoriteOffer, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<FavoriteOffer>> getFavoriteOffer(@PathVariable Long id) {
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
    public Mono<ResponseEntity<Void>> deleteFavoriteOffer(@PathVariable Long id) {
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
