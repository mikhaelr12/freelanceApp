package com.freelance.app.service;

import com.freelance.app.domain.ProfileReview;
import com.freelance.app.repository.ProfileRepository;
import com.freelance.app.repository.ProfileReviewRepository;
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
 * Service Implementation for managing {@link com.freelance.app.domain.ProfileReview}.
 */
@Service
@Transactional
public class ProfileReviewService {

    private static final String ENTITY_NAME = "profileReview";

    private final ProfileReviewRepository profileReviewRepository;
    private final ProfileHelper profileHelper;
    private final ProfileRepository profileRepository;

    public ProfileReviewService(
        ProfileReviewRepository profileReviewRepository,
        ProfileHelper profileHelper,
        ProfileRepository profileRepository
    ) {
        this.profileReviewRepository = profileReviewRepository;
        this.profileHelper = profileHelper;
        this.profileRepository = profileRepository;
    }

    /**
     * Create new profile review.
     *
     * @param revieweeId id of the profile reviewed.
     * @param dto the dto of the new review.
     * @return the persistent entity.
     */
    public Mono<ProfileReview> createProfileReview(ReviewCreateDTO dto, Long revieweeId) {
        return profileHelper
            .getCurrentProfile()
            .flatMap(reviewer ->
                profileRepository
                    .findById(revieweeId)
                    .switchIfEmpty(
                        Mono.error(new NotFoundAlertException("Profile not found with id: " + revieweeId, ENTITY_NAME, "offerNotFound"))
                    )
                    .flatMap(reviewee ->
                        profileReviewRepository
                            .existsByReviewerId(reviewer.getId(), revieweeId)
                            .flatMap(exists -> {
                                if (exists) {
                                    return Mono.error(
                                        new BadRequestAlertException(
                                            "User already has a review for this profile",
                                            ENTITY_NAME,
                                            "userAlreadyHasAReviewForThisProfile"
                                        )
                                    );
                                }

                                ProfileReview review = new ProfileReview()
                                    .reviewer(reviewer)
                                    .text(dto.getText() != null ? dto.getText() : null)
                                    .rating(dto.getRating())
                                    .reviewee(reviewee)
                                    .createdBy(reviewer.getUser().getLogin());

                                return profileReviewRepository
                                    .save(review)
                                    .flatMap(saved ->
                                        profileReviewRepository
                                            .getAverageRatingOffer(revieweeId)
                                            .defaultIfEmpty(0.0)
                                            .flatMap(avg -> {
                                                reviewee.setRating(avg);
                                                return profileRepository.save(reviewee).thenReturn(saved);
                                            })
                                    );
                            })
                    )
            );
    }

    /**
     * Get all profile reviews.
     *
     * @param revieweeId id of the profile reviewed.
     * @param pageable the pagination information.
     * @return tje list of reviews.
     */
    public Mono<List<ReviewShortDTO>> getAllProfileReviews(Long revieweeId, Pageable pageable) {
        return profileReviewRepository
            .findReviewsShort(revieweeId, pageable.getPageSize(), ((long) pageable.getPageNumber() * pageable.getPageSize()))
            .collectList();
    }

    /**
     * Get the current user's review for a specific profile.
     *
     * @param revieweeId the id of the reviewed profile.
     * @return the review created by the current user for the profile.
     */
    public Mono<ReviewShortDTO> getMyProfileReview(Long revieweeId) {
        return profileHelper
            .getCurrentProfile()
            .flatMap(profile -> profileReviewRepository.findMyProfileReview(revieweeId, profile.getId()));
    }

    /**
     * Delete a profile review by its id.
     *
     * @param profileReviewId the id of the review to delete.
     * @return an empty Mono indicating completion.
     */
    public Mono<Void> deleteProfileReview(Long profileReviewId) {
        return profileReviewRepository
            .findById(profileReviewId)
            .flatMap(review ->
                profileReviewRepository
                    .delete(review)
                    .doOnNext(_ ->
                        profileReviewRepository
                            .getAverageRatingOffer(review.getRevieweeId())
                            .defaultIfEmpty(0.0)
                            .zipWith(profileRepository.findById(review.getRevieweeId()))
                            .flatMap(tuple -> {
                                tuple.getT2().setRating(tuple.getT1());
                                return profileRepository.save(tuple.getT2()).then();
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
                profileReviewRepository
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
                return profileReviewRepository
                    .deleteById(reviewId)
                    .doOnNext(_ ->
                        profileReviewRepository
                            .getAverageRatingOffer(tuple.getT2().getRevieweeId())
                            .defaultIfEmpty(0.0)
                            .zipWith(profileRepository.findById(tuple.getT2().getRevieweeId()))
                            .flatMap(tuple2 -> {
                                tuple2.getT2().setRating(tuple2.getT1());
                                return profileRepository.save(tuple2.getT2()).then();
                            })
                    );
            });
    }
}
