package com.freelance.app.service;

import com.freelance.app.domain.criteria.OfferCriteria;
import com.freelance.app.repository.OfferRepository;
import com.freelance.app.service.dto.OfferDTO;
import com.freelance.app.service.mapper.OfferMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.freelance.app.domain.Offer}.
 */
@Service
@Transactional
public class OfferService {

    private static final Logger LOG = LoggerFactory.getLogger(OfferService.class);

    private final OfferRepository offerRepository;

    private final OfferMapper offerMapper;

    public OfferService(OfferRepository offerRepository, OfferMapper offerMapper) {
        this.offerRepository = offerRepository;
        this.offerMapper = offerMapper;
    }

    /**
     * Save a offer.
     *
     * @param offerDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<OfferDTO> save(OfferDTO offerDTO) {
        LOG.debug("Request to save Offer : {}", offerDTO);
        return offerRepository.save(offerMapper.toEntity(offerDTO)).map(offerMapper::toDto);
    }

    /**
     * Update a offer.
     *
     * @param offerDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<OfferDTO> update(OfferDTO offerDTO) {
        LOG.debug("Request to update Offer : {}", offerDTO);
        return offerRepository.save(offerMapper.toEntity(offerDTO)).map(offerMapper::toDto);
    }

    /**
     * Partially update a offer.
     *
     * @param offerDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<OfferDTO> partialUpdate(OfferDTO offerDTO) {
        LOG.debug("Request to partially update Offer : {}", offerDTO);

        return offerRepository
            .findById(offerDTO.getId())
            .map(existingOffer -> {
                offerMapper.partialUpdate(existingOffer, offerDTO);

                return existingOffer;
            })
            .flatMap(offerRepository::save)
            .map(offerMapper::toDto);
    }

    /**
     * Find offers by Criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<OfferDTO> findByCriteria(OfferCriteria criteria, Pageable pageable) {
        LOG.debug("Request to get all Offers by Criteria");
        return offerRepository.findByCriteria(criteria, pageable).map(offerMapper::toDto);
    }

    /**
     * Find the count of offers by criteria.
     * @param criteria filtering criteria
     * @return the count of offers
     */
    public Mono<Long> countByCriteria(OfferCriteria criteria) {
        LOG.debug("Request to get the count of all Offers by Criteria");
        return offerRepository.countByCriteria(criteria);
    }

    /**
     * Get all the offers with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Flux<OfferDTO> findAllWithEagerRelationships(Pageable pageable) {
        return offerRepository.findAllWithEagerRelationships(pageable).map(offerMapper::toDto);
    }

    /**
     * Returns the number of offers available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return offerRepository.count();
    }

    /**
     * Get one offer by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<OfferDTO> findOne(Long id) {
        LOG.debug("Request to get Offer : {}", id);
        return offerRepository.findOneWithEagerRelationships(id).map(offerMapper::toDto);
    }

    /**
     * Delete the offer by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Offer : {}", id);
        return offerRepository.deleteById(id);
    }
}
