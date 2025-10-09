package com.freelance.app.service;

import com.freelance.app.domain.criteria.OfferTypeCriteria;
import com.freelance.app.repository.OfferTypeRepository;
import com.freelance.app.service.dto.OfferTypeDTO;
import com.freelance.app.service.mapper.OfferTypeMapper;
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

    private final OfferTypeMapper offerTypeMapper;

    public OfferTypeService(OfferTypeRepository offerTypeRepository, OfferTypeMapper offerTypeMapper) {
        this.offerTypeRepository = offerTypeRepository;
        this.offerTypeMapper = offerTypeMapper;
    }

    /**
     * Save a offerType.
     *
     * @param offerTypeDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<OfferTypeDTO> save(OfferTypeDTO offerTypeDTO) {
        LOG.debug("Request to save OfferType : {}", offerTypeDTO);
        return offerTypeRepository.save(offerTypeMapper.toEntity(offerTypeDTO)).map(offerTypeMapper::toDto);
    }

    /**
     * Update a offerType.
     *
     * @param offerTypeDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<OfferTypeDTO> update(OfferTypeDTO offerTypeDTO) {
        LOG.debug("Request to update OfferType : {}", offerTypeDTO);
        return offerTypeRepository.save(offerTypeMapper.toEntity(offerTypeDTO)).map(offerTypeMapper::toDto);
    }

    /**
     * Partially update a offerType.
     *
     * @param offerTypeDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<OfferTypeDTO> partialUpdate(OfferTypeDTO offerTypeDTO) {
        LOG.debug("Request to partially update OfferType : {}", offerTypeDTO);

        return offerTypeRepository
            .findById(offerTypeDTO.getId())
            .map(existingOfferType -> {
                offerTypeMapper.partialUpdate(existingOfferType, offerTypeDTO);

                return existingOfferType;
            })
            .flatMap(offerTypeRepository::save)
            .map(offerTypeMapper::toDto);
    }

    /**
     * Find offerTypes by Criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<OfferTypeDTO> findByCriteria(OfferTypeCriteria criteria, Pageable pageable) {
        LOG.debug("Request to get all OfferTypes by Criteria");
        return offerTypeRepository.findByCriteria(criteria, pageable).map(offerTypeMapper::toDto);
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
     * Get all the offerTypes with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Flux<OfferTypeDTO> findAllWithEagerRelationships(Pageable pageable) {
        return offerTypeRepository.findAllWithEagerRelationships(pageable).map(offerTypeMapper::toDto);
    }

    /**
     * Returns the number of offerTypes available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return offerTypeRepository.count();
    }

    /**
     * Get one offerType by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<OfferTypeDTO> findOne(Long id) {
        LOG.debug("Request to get OfferType : {}", id);
        return offerTypeRepository.findOneWithEagerRelationships(id).map(offerTypeMapper::toDto);
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
