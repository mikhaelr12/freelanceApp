package com.freelance.app.web.rest;

import com.freelance.app.service.ProfileReviewService;
import com.freelance.app.service.dto.ReviewShortDTO;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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

    private static final String ENTITY_NAME = "profileReview";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProfileReviewService profileReviewService;

    public ProfileReviewResource(ProfileReviewService profileReviewService) {
        this.profileReviewService = profileReviewService;
    }

    @GetMapping("/{profileId}")
    public Mono<ResponseEntity<List<ReviewShortDTO>>> getAllProfileReviews(@PathVariable Long profileId) {}
}
