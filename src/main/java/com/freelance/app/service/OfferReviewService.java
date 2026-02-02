package com.freelance.app.service;

import com.freelance.app.domain.OfferReview;
import com.freelance.app.repository.OfferRepository;
import com.freelance.app.repository.OfferReviewRepository;
import com.freelance.app.service.dto.ReviewCreateDTO;
import com.freelance.app.service.dto.ReviewShortDTO;
import com.freelance.app.util.ProfileHelper;
import com.freelance.app.web.rest.errors.BadRequestAlertException;
import com.freelance.app.web.rest.errors.NotFoundAlertException;
import java.util.List;
import java.util.Objects;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.freelance.app.domain.OfferReview}.
 */
@Service
@Transactional
public class OfferReviewService {

    private static final String ENTITY_NAME = "offerReview";

    private final OfferReviewRepository offerReviewRepository;
    private final ProfileHelper profileHelper;
    private final OfferRepository offerRepository;

    public OfferReviewService(OfferReviewRepository offerReviewRepository, ProfileHelper profileHelper, OfferRepository offerRepository) {
        this.offerReviewRepository = offerReviewRepository;
        this.profileHelper = profileHelper;
        this.offerRepository = offerRepository;
    }

    /**
     * Create new offer review.
     *
     * @param offerId id of the offer.
     * @param dto     the dto of the new review.
     * @return the persistent entity.
     */
    public Mono<OfferReview> createOfferReview(Long offerId, ReviewCreateDTO dto) {
        return profileHelper
            .getCurrentProfile()
            .flatMap(profile ->
                offerRepository
                    .findById(offerId)
                    .switchIfEmpty(
                        Mono.error(new NotFoundAlertException("Offer not found with id: " + offerId, ENTITY_NAME, "offerNotFound"))
                    )
                    .flatMap(offer ->
                        offerReviewRepository
                            .existsByReviewerId(profile.getId(), offerId)
                            .flatMap(exists -> {
                                if (exists) {
                                    return Mono.error(
                                        new BadRequestAlertException(
                                            "User already has a review for this offer",
                                            ENTITY_NAME,
                                            "userAlreadyHasAReviewForThisOffer"
                                        )
                                    );
                                }

                                OfferReview review = new OfferReview()
                                    .reviewer(profile)
                                    .text(dto.text() != null ? dto.text() : null)
                                    .rating(dto.rating())
                                    .offer(offer)
                                    .createdBy(profile.getUser().getLogin());

                                return offerReviewRepository
                                    .save(review)
                                    .flatMap(saved ->
                                        offerReviewRepository
                                            .getAverageRatingOffer(offerId)
                                            .defaultIfEmpty(0.0)
                                            .flatMap(avg -> {
                                                offer.setRating(avg);
                                                return offerRepository.save(offer).thenReturn(saved);
                                            })
                                    );
                            })
                    )
            );
    }

    /**
     * Get all offer reviews.
     *
     * @param offerId  id of the offer.
     * @param pageable the pagination information.
     * @return tje list of reviews.
     */
    public Mono<List<ReviewShortDTO>> getAllOfferReviews(Long offerId, Pageable pageable) {
        return offerReviewRepository
            .findByOfferPaged(offerId, pageable.getPageSize(), ((long) pageable.getPageNumber() * pageable.getPageSize()))
            .collectList();
    }

    /**
     * Get the current user's review for a specific offer.
     *
     * @param offerId the id of the offer.
     * @return the review created by the current user for the given offer.
     */
    public Mono<ReviewShortDTO> getMyOfferReview(Long offerId) {
        return profileHelper.getCurrentProfile().flatMap(profile -> offerReviewRepository.findMyOfferReview(offerId, profile.getId()));
    }

    /**
     * Delete an offer review by its id.
     *
     * @param reviewId the id of the review to delete.
     * @return an empty Mono indicating completion.
     */
    public Mono<Void> deleteOfferReview(Long reviewId) {
        return offerReviewRepository
            .findById(reviewId)
            .flatMap(review ->
                offerReviewRepository
                    .delete(review)
                    .doOnNext(_ ->
                        offerReviewRepository
                            .getAverageRatingOffer(review.getOfferId())
                            .defaultIfEmpty(0.0)
                            .zipWith(offerRepository.findById(review.getOfferId()))
                            .flatMap(tuple -> {
                                tuple.getT2().setRating(tuple.getT1());
                                return offerRepository.save(tuple.getT2()).then();
                            })
                    )
            );
    }

    /**
     * Delete the current user's review.
     *
     * <p>
     * This method verifies that the review belongs to the currently authenticated user
     * before performing the deletion.
     * </p>
     *
     * @param reviewId the id of the review to delete.
     * @return an empty Mono indicating completion.
     * @throws NotFoundAlertException if the review does not belong to the current user.
     */
    public Mono<Void> deleteMyReview(Long reviewId) {
        return profileHelper
            .getCurrentProfile()
            .zipWith(
                offerReviewRepository
                    .findById(reviewId)
                    .switchIfEmpty(
                        Mono.error(new NotFoundAlertException("Review not found with id: " + reviewId, ENTITY_NAME, "reviewNotFound"))
                    )
            )
            .flatMap(tuple -> {
                if (!Objects.equals(tuple.getT1().getId(), tuple.getT2().getReviewerId())) {
                    return Mono.error(
                        new NotFoundAlertException("Review does not belong to current user", ENTITY_NAME, "reviewDoesNotBelongToThisUser")
                    );
                }
                return offerReviewRepository
                    .deleteById(reviewId)
                    .doOnNext(_ ->
                        offerReviewRepository
                            .getAverageRatingOffer(tuple.getT2().getOfferId())
                            .defaultIfEmpty(0.0)
                            .zipWith(offerRepository.findById(tuple.getT2().getOfferId()))
                            .flatMap(tuple2 -> {
                                tuple2.getT2().setRating(tuple2.getT1());
                                return offerRepository.save(tuple2.getT2()).then();
                            })
                    );
            });
    }
}
