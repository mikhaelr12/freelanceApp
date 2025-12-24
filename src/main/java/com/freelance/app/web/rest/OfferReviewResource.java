package com.freelance.app.web.rest;

import com.freelance.app.domain.OfferReview;
import com.freelance.app.security.AuthoritiesConstants;
import com.freelance.app.service.OfferReviewService;
import com.freelance.app.service.dto.OfferReviewCreateDTO;
import com.freelance.app.service.dto.OfferReviewShortDTO;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * REST controller for managing {@link com.freelance.app.domain.OfferReview}.
 */
@RestController
@RequestMapping("/api/offer-reviews")
public class OfferReviewResource {

    private static final Logger LOG = LoggerFactory.getLogger(OfferReviewResource.class);

    private final OfferReviewService offerReviewService;

    public OfferReviewResource(OfferReviewService offerReviewService) {
        this.offerReviewService = offerReviewService;
    }

    /**
     * {@code POST /offer-reviews/:offerId} : Create a review for an offer.
     *
     * @param reviewDTO the body of the review containing text if present and rating.
     * @param offerId the id of the offer reviewed.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body of the new offer review.
     * */
    @PostMapping("/{offerId}")
    @Operation(summary = "Create a review for an offer")
    public Mono<ResponseEntity<OfferReview>> createOfferReview(
        @Valid @RequestBody OfferReviewCreateDTO reviewDTO,
        @PathVariable Long offerId
    ) {
        LOG.debug("REST request to create offer review : {}", reviewDTO);
        return offerReviewService.createOfferReview(offerId, reviewDTO).map(ResponseEntity::ok);
    }

    /**
     * {@code GET /offer-reviews/:offerId} : Get all reviews of an offer.
     *
     * @param offerId the id of the offer.
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of reviews.
     * */
    @GetMapping("/{offerId}")
    @Operation(summary = "Get all reviews of an offer")
    public Mono<ResponseEntity<List<OfferReviewShortDTO>>> getAllOfferReviews(
        @PathVariable Long offerId,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get all offer reviews for offerId, limit: {} {}", offerId, pageable);
        return offerReviewService.getAllOfferReviews(offerId, pageable).map(ResponseEntity::ok);
    }

    /**
     * {@code GET /offer-reviews/my/:offerId} : Get the user's review of an offer.
     *
     * @param offerId the id of the offer.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the review.
     * */
    @GetMapping("/my/{offerId}")
    @Operation(summary = "Get the user's review of an offer")
    public Mono<ResponseEntity<OfferReviewShortDTO>> getMyOfferReview(@PathVariable Long offerId) {
        LOG.debug("REST request to get offer review : {}", offerId);
        return offerReviewService.getMyOfferReview(offerId).map(ResponseEntity::ok);
    }

    /**
     * {@code DELETE /offer-reviews/:reviewId} : Delete a review.
     *
     * @param reviewId the id of the review to be deleted.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)}.
     * */
    @DeleteMapping("/{reviewId}")
    @PreAuthorize("hasAuthority('" + AuthoritiesConstants.ADMIN + "')")
    @Operation(summary = "Delete a review (only permitted for an admin)")
    public Mono<ResponseEntity<Void>> deleteReview(@PathVariable Long reviewId) {
        LOG.debug("REST request to delete offer review : {}", reviewId);
        return offerReviewService.deleteOfferReview(reviewId).map(ResponseEntity::ok);
    }

    /**
     * {@code DELETE /offer-reviews/my/:reviewId} : Delete a personal offer review
     *
     * @param reviewId the id of the review to be deleted.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)}.
     * */
    @DeleteMapping("/my/{reviewId}")
    @Operation(summary = "Delete a personal review of an offer")
    public Mono<ResponseEntity<Void>> deleteMyReview(@PathVariable Long reviewId) {
        LOG.debug("REST request to delete my offer review : {}", reviewId);
        return offerReviewService.deleteMyReview(reviewId).map(ResponseEntity::ok);
    }
}
