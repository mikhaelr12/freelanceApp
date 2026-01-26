package com.freelance.app.web.rest;

import com.freelance.app.domain.ProfileReview;
import com.freelance.app.service.ProfileReviewService;
import com.freelance.app.service.dto.ReviewCreateDTO;
import com.freelance.app.service.dto.ReviewShortDTO;
import jakarta.validation.Valid;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * REST controller for managing {@link com.freelance.app.domain.ProfileReview}.
 */
@RestController
@RequestMapping("/api/profile-reviews")
public class ProfileReviewResource {

    private static final Logger LOG = LoggerFactory.getLogger(ProfileReviewResource.class);

    private final ProfileReviewService profileReviewService;

    public ProfileReviewResource(ProfileReviewService profileReviewService) {
        this.profileReviewService = profileReviewService;
    }

    /**
     * Create a new profile review.
     *
     * <p>
     * Creates a review for the specified profile (reviewee) by the currently authenticated user.
     * </p>
     *
     * @param revieweeId the id of the profile being reviewed.
     * @param dto the review creation payload.
     * @return the created ProfileReview.
     */
    @PostMapping("/{revieweeId}")
    public Mono<ResponseEntity<ProfileReview>> createProfileReview(@PathVariable Long revieweeId, @Valid @RequestBody ReviewCreateDTO dto) {
        LOG.debug("REST request to create ProfileReview : {}", dto);
        return profileReviewService.createProfileReview(dto, revieweeId).map(ResponseEntity::ok);
    }

    /**
     * Get all reviews received by a profile.
     *
     * <p>
     * Returns a paginated list of reviews received by the specified profile,
     * including reviewer information.
     * </p>
     *
     * @param revieweeId the id of the profile whose reviews are requested.
     * @param pageable the pagination information.
     * @return the list of profile reviews.
     */
    @GetMapping("/{revieweeId}")
    public Mono<ResponseEntity<List<ReviewShortDTO>>> getAllProfileReviews(
        @PathVariable Long revieweeId,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get a page of ProfileReviews : {}", pageable);
        return profileReviewService.getAllProfileReviews(revieweeId, pageable).map(ResponseEntity::ok);
    }

    /**
     * Get the current user's review for a specific profile.
     *
     * <p>
     * Returns the review created by the currently authenticated user
     * for the given profile.
     * </p>
     *
     * @param revieweeId the id of the profile being reviewed.
     * @return the current user's profile review.
     */
    @GetMapping("/my/{revieweeId}")
    public Mono<ResponseEntity<ReviewShortDTO>> getMyProfileReviews(@PathVariable Long revieweeId) {
        LOG.debug("REST request to get my ProfileReview for reviewee with id : {}", revieweeId);
        return profileReviewService.getMyProfileReview(revieweeId).map(ResponseEntity::ok);
    }

    /**
     * Delete a profile review by its id.
     *
     * @param profileReviewId the id of the profile review to delete.
     * @return an empty response indicating successful deletion.
     */
    @DeleteMapping("/{profileReviewId}")
    public Mono<ResponseEntity<Void>> deleteProfileReview(@PathVariable Long profileReviewId) {
        LOG.debug("REST request to delete a ProfileReview : {}", profileReviewId);
        return profileReviewService.deleteProfileReview(profileReviewId).map(ResponseEntity::ok);
    }

    /**
     * Delete the current user's profile review.
     *
     * <p>
     * This operation is allowed only if the review belongs to
     * the currently authenticated user.
     * </p>
     *
     * @param profileReviewId the id of the profile review to delete.
     * @return an empty response indicating successful deletion.
     */
    @DeleteMapping("/my/{profileReviewId}")
    public Mono<ResponseEntity<Void>> deleteMyProfileReview(@PathVariable Long profileReviewId) {
        LOG.debug("REST request to delete my ProfileReview : {}", profileReviewId);
        return profileReviewService.deleteMyReview(profileReviewId).map(ResponseEntity::ok);
    }
}
