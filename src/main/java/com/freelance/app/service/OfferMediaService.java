package com.freelance.app.service;

import com.freelance.app.domain.criteria.OfferMediaCriteria;
import com.freelance.app.repository.OfferMediaRepository;
import com.freelance.app.service.dto.OfferMediaDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.freelance.app.domain.OfferMedia}.
 */
@Service
@Transactional
public class OfferMediaService {

    private static final Logger LOG = LoggerFactory.getLogger(OfferMediaService.class);

    private final OfferMediaRepository offerMediaRepository;

    public OfferMediaService(OfferMediaRepository offerMediaRepository) {
        this.offerMediaRepository = offerMediaRepository;
    }

    /**
     * Save a offerMedia.
     *
     * @param offerMediaDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<OfferMediaDTO> save(OfferMediaDTO offerMediaDTO) {
        LOG.debug("Request to save OfferMedia : {}", offerMediaDTO);
        return null;
    }

    /**
     * Update a offerMedia.
     *
     * @param offerMediaDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<OfferMediaDTO> update(OfferMediaDTO offerMediaDTO) {
        LOG.debug("Request to update OfferMedia : {}", offerMediaDTO);
        return null;
    }

    /**
     * Partially update a offerMedia.
     *
     * @param offerMediaDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<OfferMediaDTO> partialUpdate(OfferMediaDTO offerMediaDTO) {
        LOG.debug("Request to partially update OfferMedia : {}", offerMediaDTO);
        return null;
    }

    /**
     * Find offerMedias by Criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<OfferMediaDTO> findByCriteria(OfferMediaCriteria criteria, Pageable pageable) {
        LOG.debug("Request to get all OfferMedias by Criteria");
        return null;
    }

    /**
     * Find the count of offerMedias by criteria.
     *
     * @param criteria filtering criteria
     * @return the count of offerMedias
     */
    public Mono<Long> countByCriteria(OfferMediaCriteria criteria) {
        LOG.debug("Request to get the count of all OfferMedias by Criteria");
        return offerMediaRepository.countByCriteria(criteria);
    }

    /**
     * Get all the offerMedias with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Flux<OfferMediaDTO> findAllWithEagerRelationships(Pageable pageable) {
        return null;
    }

    /**
     * Returns the number of offerMedias available.
     *
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return offerMediaRepository.count();
    }

    /**
     * Get one offerMedia by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<OfferMediaDTO> findOne(Long id) {
        LOG.debug("Request to get OfferMedia : {}", id);
        return null;
    }

    /**
     * Delete the offerMedia by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete OfferMedia : {}", id);
        return offerMediaRepository.deleteById(id);
    }
}
