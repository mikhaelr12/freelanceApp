package com.freelance.app.service;

import com.freelance.app.domain.OfferReview;
import com.freelance.app.domain.criteria.OfferReviewCriteria;
import com.freelance.app.repository.OfferReviewRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.freelance.app.domain.OfferReview}.
 */
@Service
@Transactional
public class OfferReviewService {

    private static final Logger LOG = LoggerFactory.getLogger(OfferReviewService.class);

    private final OfferReviewRepository offerReviewRepository;

    public OfferReviewService(OfferReviewRepository offerReviewRepository) {
        this.offerReviewRepository = offerReviewRepository;
    }

    /**
     * Save a offerReview.
     *
     * @param offerReview the entity to save.
     * @return the persisted entity.
     */
    public Mono<OfferReview> save(OfferReview offerReview) {
        LOG.debug("Request to save OfferReview : {}", offerReview);
        return offerReviewRepository.save(offerReview);
    }

    /**
     * Update a offerReview.
     *
     * @param offerReview the entity to save.
     * @return the persisted entity.
     */
    public Mono<OfferReview> update(OfferReview offerReview) {
        LOG.debug("Request to update OfferReview : {}", offerReview);
        return offerReviewRepository.save(offerReview);
    }

    /**
     * Partially update a offerReview.
     *
     * @param offerReview the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<OfferReview> partialUpdate(OfferReview offerReview) {
        LOG.debug("Request to partially update OfferReview : {}", offerReview);

        return offerReviewRepository
            .findById(offerReview.getId())
            .map(existingOfferReview -> {
                if (offerReview.getText() != null) {
                    existingOfferReview.setText(offerReview.getText());
                }
                if (offerReview.getRating() != null) {
                    existingOfferReview.setRating(offerReview.getRating());
                }
                if (offerReview.getCreatedDate() != null) {
                    existingOfferReview.setCreatedDate(offerReview.getCreatedDate());
                }
                if (offerReview.getLastModifiedDate() != null) {
                    existingOfferReview.setLastModifiedDate(offerReview.getLastModifiedDate());
                }
                if (offerReview.getCreatedBy() != null) {
                    existingOfferReview.setCreatedBy(offerReview.getCreatedBy());
                }
                if (offerReview.getLastModifiedBy() != null) {
                    existingOfferReview.setLastModifiedBy(offerReview.getLastModifiedBy());
                }

                return existingOfferReview;
            })
            .flatMap(offerReviewRepository::save);
    }

    /**
     * Find offerReviews by Criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<OfferReview> findByCriteria(OfferReviewCriteria criteria, Pageable pageable) {
        LOG.debug("Request to get all OfferReviews by Criteria");
        return offerReviewRepository.findByCriteria(criteria, pageable);
    }

    /**
     * Find the count of offerReviews by criteria.
     * @param criteria filtering criteria
     * @return the count of offerReviews
     */
    public Mono<Long> countByCriteria(OfferReviewCriteria criteria) {
        LOG.debug("Request to get the count of all OfferReviews by Criteria");
        return offerReviewRepository.countByCriteria(criteria);
    }

    /**
     * Get all the offerReviews with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Flux<OfferReview> findAllWithEagerRelationships(Pageable pageable) {
        return offerReviewRepository.findAllWithEagerRelationships(pageable);
    }

    /**
     * Returns the number of offerReviews available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return offerReviewRepository.count();
    }

    /**
     * Get one offerReview by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<OfferReview> findOne(Long id) {
        LOG.debug("Request to get OfferReview : {}", id);
        return offerReviewRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the offerReview by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete OfferReview : {}", id);
        return offerReviewRepository.deleteById(id);
    }
}
