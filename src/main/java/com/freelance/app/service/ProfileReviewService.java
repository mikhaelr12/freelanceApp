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

    /**
     * Save a profileReview.
     *
     * @param profileReview the entity to save.
     * @return the persisted entity.
     */
    public Mono<ProfileReview> save(ProfileReview profileReview) {
        LOG.debug("Request to save ProfileReview : {}", profileReview);
        return profileReviewRepository.save(profileReview);
    }

    /**
     * Update a profileReview.
     *
     * @param profileReview the entity to save.
     * @return the persisted entity.
     */
    public Mono<ProfileReview> update(ProfileReview profileReview) {
        LOG.debug("Request to update ProfileReview : {}", profileReview);
        return profileReviewRepository.save(profileReview);
    }

    /**
     * Partially update a profileReview.
     *
     * @param profileReview the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<ProfileReview> partialUpdate(ProfileReview profileReview) {
        LOG.debug("Request to partially update ProfileReview : {}", profileReview);

        return profileReviewRepository
            .findById(profileReview.getId())
            .map(existingProfileReview -> {
                if (profileReview.getText() != null) {
                    existingProfileReview.setText(profileReview.getText());
                }
                if (profileReview.getRating() != null) {
                    existingProfileReview.setRating(profileReview.getRating());
                }
                if (profileReview.getCreatedDate() != null) {
                    existingProfileReview.setCreatedDate(profileReview.getCreatedDate());
                }
                if (profileReview.getLastModifiedDate() != null) {
                    existingProfileReview.setLastModifiedDate(profileReview.getLastModifiedDate());
                }
                if (profileReview.getCreatedBy() != null) {
                    existingProfileReview.setCreatedBy(profileReview.getCreatedBy());
                }
                if (profileReview.getLastModifiedBy() != null) {
                    existingProfileReview.setLastModifiedBy(profileReview.getLastModifiedBy());
                }

                return existingProfileReview;
            })
            .flatMap(profileReviewRepository::save);
    }

    /**
     * Find profileReviews by Criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<ProfileReview> findByCriteria(ProfileReviewCriteria criteria, Pageable pageable) {
        LOG.debug("Request to get all ProfileReviews by Criteria");
        return profileReviewRepository.findByCriteria(criteria, pageable);
    }

    /**
     * Find the count of profileReviews by criteria.
     * @param criteria filtering criteria
     * @return the count of profileReviews
     */
    public Mono<Long> countByCriteria(ProfileReviewCriteria criteria) {
        LOG.debug("Request to get the count of all ProfileReviews by Criteria");
        return profileReviewRepository.countByCriteria(criteria);
    }

    /**
     * Returns the number of profileReviews available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return profileReviewRepository.count();
    }

    /**
     * Get one profileReview by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<ProfileReview> findOne(Long id) {
        LOG.debug("Request to get ProfileReview : {}", id);
        return profileReviewRepository.findById(id);
    }

    /**
     * Delete the profileReview by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete ProfileReview : {}", id);
        return profileReviewRepository.deleteById(id);
    }
}
