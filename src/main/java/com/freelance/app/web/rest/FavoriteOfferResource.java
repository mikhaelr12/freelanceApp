package com.freelance.app.web.rest;

import com.freelance.app.domain.FavoriteOffer;
import com.freelance.app.service.FavoriteOfferService;
import com.freelance.app.service.dto.FavoriteOfferDTO;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * REST controller for managing {@link com.freelance.app.domain.FavoriteOffer}.
 */
@RestController
@RequestMapping("/api/favorite-offers")
public class FavoriteOfferResource {

    private static final Logger LOG = LoggerFactory.getLogger(FavoriteOfferResource.class);

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

    @GetMapping("")
    public Mono<ResponseEntity<List<FavoriteOfferDTO>>> getAllFavoriteOffers() {
        LOG.debug("REST request to get all FavoriteOffers");
        return favoriteOfferService.getAllFavoriteOffers().map(ResponseEntity::ok);
    }

    @DeleteMapping("/remove/{favoriteOfferId}")
    public Mono<ResponseEntity<Void>> deleteFavoriteOffer(@PathVariable Long favoriteOfferId) {
        LOG.debug("REST request to delete FavoriteOffer for Offer with id: {}", favoriteOfferId);
        return favoriteOfferService.deleteFavoriteOffer(favoriteOfferId).thenReturn(ResponseEntity.ok().build());
    }
}
