package com.freelance.app.service;

import com.freelance.app.domain.criteria.OfferReviewCriteria;
import com.freelance.app.repository.OfferReviewRepository;
import com.freelance.app.service.dto.OfferReviewDTO;
import com.freelance.app.service.mapper.OfferReviewMapper;
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

    private final OfferReviewMapper offerReviewMapper;

    public OfferReviewService(OfferReviewRepository offerReviewRepository, OfferReviewMapper offerReviewMapper) {
        this.offerReviewRepository = offerReviewRepository;
        this.offerReviewMapper = offerReviewMapper;
    }

    /**
     * Save a offerReview.
     *
     * @param offerReviewDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<OfferReviewDTO> save(OfferReviewDTO offerReviewDTO) {
        LOG.debug("Request to save OfferReview : {}", offerReviewDTO);
        return offerReviewRepository.save(offerReviewMapper.toEntity(offerReviewDTO)).map(offerReviewMapper::toDto);
    }

    /**
     * Update a offerReview.
     *
     * @param offerReviewDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<OfferReviewDTO> update(OfferReviewDTO offerReviewDTO) {
        LOG.debug("Request to update OfferReview : {}", offerReviewDTO);
        return offerReviewRepository.save(offerReviewMapper.toEntity(offerReviewDTO)).map(offerReviewMapper::toDto);
    }

    /**
     * Partially update a offerReview.
     *
     * @param offerReviewDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<OfferReviewDTO> partialUpdate(OfferReviewDTO offerReviewDTO) {
        LOG.debug("Request to partially update OfferReview : {}", offerReviewDTO);

        return offerReviewRepository
            .findById(offerReviewDTO.getId())
            .map(existingOfferReview -> {
                offerReviewMapper.partialUpdate(existingOfferReview, offerReviewDTO);

                return existingOfferReview;
            })
            .flatMap(offerReviewRepository::save)
            .map(offerReviewMapper::toDto);
    }

    /**
     * Find offerReviews by Criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<OfferReviewDTO> findByCriteria(OfferReviewCriteria criteria, Pageable pageable) {
        LOG.debug("Request to get all OfferReviews by Criteria");
        return offerReviewRepository.findByCriteria(criteria, pageable).map(offerReviewMapper::toDto);
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
    public Flux<OfferReviewDTO> findAllWithEagerRelationships(Pageable pageable) {
        return offerReviewRepository.findAllWithEagerRelationships(pageable).map(offerReviewMapper::toDto);
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
    public Mono<OfferReviewDTO> findOne(Long id) {
        LOG.debug("Request to get OfferReview : {}", id);
        return offerReviewRepository.findOneWithEagerRelationships(id).map(offerReviewMapper::toDto);
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
