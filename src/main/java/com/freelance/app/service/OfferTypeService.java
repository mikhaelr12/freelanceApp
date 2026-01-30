package com.freelance.app.service;

import com.freelance.app.domain.OfferType;
import com.freelance.app.domain.criteria.OfferTypeCriteria;
import com.freelance.app.repository.OfferTypeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.freelance.app.domain.OfferType}.
 */
@Service
@Transactional
public class OfferTypeService {

    private static final Logger LOG = LoggerFactory.getLogger(OfferTypeService.class);

    private final OfferTypeRepository offerTypeRepository;

    public OfferTypeService(OfferTypeRepository offerTypeRepository) {
        this.offerTypeRepository = offerTypeRepository;
    }

    /**
     * Save a offerType.
     *
     * @param offerType the entity to save.
     * @return the persisted entity.
     */
    public Mono<OfferType> save(OfferType offerType) {
        LOG.debug("Request to save OfferType : {}", offerType);
        return offerTypeRepository.save(offerType);
    }

    /**
     * Update a offerType.
     *
     * @param offerType the entity to save.
     * @return the persisted entity.
     */
    public Mono<OfferType> update(OfferType offerType) {
        LOG.debug("Request to update OfferType : {}", offerType);
        return offerTypeRepository.save(offerType);
    }

    /**
     * Partially update a offerType.
     *
     * @param offerType the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<OfferType> partialUpdate(OfferType offerType) {
        LOG.debug("Request to partially update OfferType : {}", offerType);

        return offerTypeRepository
            .findById(offerType.getId())
            .map(existingOfferType -> {
                if (offerType.getName() != null) {
                    existingOfferType.setName(offerType.getName());
                }
                if (offerType.getCreatedDate() != null) {
                    existingOfferType.setCreatedDate(offerType.getCreatedDate());
                }
                if (offerType.getLastModifiedDate() != null) {
                    existingOfferType.setLastModifiedDate(offerType.getLastModifiedDate());
                }
                if (offerType.getCreatedBy() != null) {
                    existingOfferType.setCreatedBy(offerType.getCreatedBy());
                }
                if (offerType.getLastModifiedBy() != null) {
                    existingOfferType.setLastModifiedBy(offerType.getLastModifiedBy());
                }
                if (offerType.getActive() != null) {
                    existingOfferType.setActive(offerType.getActive());
                }

                return existingOfferType;
            })
            .flatMap(offerTypeRepository::save);
    }

    /**
     * Find offerTypes by Criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<OfferType> findByCriteria(OfferTypeCriteria criteria, Pageable pageable) {
        LOG.debug("Request to get all OfferTypes by Criteria");
        return offerTypeRepository.findByCriteria(criteria, pageable);
    }

    /**
     * Find the count of offerTypes by criteria.
     * @param criteria filtering criteria
     * @return the count of offerTypes
     */
    public Mono<Long> countByCriteria(OfferTypeCriteria criteria) {
        LOG.debug("Request to get the count of all OfferTypes by Criteria");
        return offerTypeRepository.countByCriteria(criteria);
    }

    /**
     * Get one offerType by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<OfferType> findOne(Long id) {
        LOG.debug("Request to get OfferType : {}", id);
        return offerTypeRepository.findById(id);
    }

    /**
     * Delete the offerType by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete OfferType : {}", id);
        return offerTypeRepository.deleteById(id);
    }
}
