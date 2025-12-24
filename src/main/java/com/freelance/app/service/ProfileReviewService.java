package com.freelance.app.service;

import com.freelance.app.domain.ProfileReview;
import com.freelance.app.domain.criteria.ProfileReviewCriteria;
import com.freelance.app.repository.ProfileReviewRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.freelance.app.domain.ProfileReview}.
 */
@Service
@Transactional
public class ProfileReviewService {

    private static final Logger LOG = LoggerFactory.getLogger(ProfileReviewService.class);

    private final ProfileReviewRepository profileReviewRepository;

    public ProfileReviewService(ProfileReviewRepository profileReviewRepository) {
        this.profileReviewRepository = profileReviewRepository;
    }
}
