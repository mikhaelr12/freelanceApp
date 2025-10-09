package com.freelance.app.service;

import com.freelance.app.domain.criteria.OfferPackageCriteria;
import com.freelance.app.repository.OfferPackageRepository;
import com.freelance.app.service.dto.OfferPackageDTO;
import com.freelance.app.service.mapper.OfferPackageMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.freelance.app.domain.OfferPackage}.
 */
@Service
@Transactional
public class OfferPackageService {

    private static final Logger LOG = LoggerFactory.getLogger(OfferPackageService.class);

    private final OfferPackageRepository offerPackageRepository;

    private final OfferPackageMapper offerPackageMapper;

    public OfferPackageService(OfferPackageRepository offerPackageRepository, OfferPackageMapper offerPackageMapper) {
        this.offerPackageRepository = offerPackageRepository;
        this.offerPackageMapper = offerPackageMapper;
    }

    /**
     * Save a offerPackage.
     *
     * @param offerPackageDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<OfferPackageDTO> save(OfferPackageDTO offerPackageDTO) {
        LOG.debug("Request to save OfferPackage : {}", offerPackageDTO);
        return offerPackageRepository.save(offerPackageMapper.toEntity(offerPackageDTO)).map(offerPackageMapper::toDto);
    }

    /**
     * Update a offerPackage.
     *
     * @param offerPackageDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<OfferPackageDTO> update(OfferPackageDTO offerPackageDTO) {
        LOG.debug("Request to update OfferPackage : {}", offerPackageDTO);
        return offerPackageRepository.save(offerPackageMapper.toEntity(offerPackageDTO)).map(offerPackageMapper::toDto);
    }

    /**
     * Partially update a offerPackage.
     *
     * @param offerPackageDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<OfferPackageDTO> partialUpdate(OfferPackageDTO offerPackageDTO) {
        LOG.debug("Request to partially update OfferPackage : {}", offerPackageDTO);

        return offerPackageRepository
            .findById(offerPackageDTO.getId())
            .map(existingOfferPackage -> {
                offerPackageMapper.partialUpdate(existingOfferPackage, offerPackageDTO);

                return existingOfferPackage;
            })
            .flatMap(offerPackageRepository::save)
            .map(offerPackageMapper::toDto);
    }

    /**
     * Find offerPackages by Criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<OfferPackageDTO> findByCriteria(OfferPackageCriteria criteria, Pageable pageable) {
        LOG.debug("Request to get all OfferPackages by Criteria");
        return offerPackageRepository.findByCriteria(criteria, pageable).map(offerPackageMapper::toDto);
    }

    /**
     * Find the count of offerPackages by criteria.
     * @param criteria filtering criteria
     * @return the count of offerPackages
     */
    public Mono<Long> countByCriteria(OfferPackageCriteria criteria) {
        LOG.debug("Request to get the count of all OfferPackages by Criteria");
        return offerPackageRepository.countByCriteria(criteria);
    }

    /**
     * Get all the offerPackages with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Flux<OfferPackageDTO> findAllWithEagerRelationships(Pageable pageable) {
        return offerPackageRepository.findAllWithEagerRelationships(pageable).map(offerPackageMapper::toDto);
    }

    /**
     * Returns the number of offerPackages available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return offerPackageRepository.count();
    }

    /**
     * Get one offerPackage by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<OfferPackageDTO> findOne(Long id) {
        LOG.debug("Request to get OfferPackage : {}", id);
        return offerPackageRepository.findOneWithEagerRelationships(id).map(offerPackageMapper::toDto);
    }

    /**
     * Delete the offerPackage by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete OfferPackage : {}", id);
        return offerPackageRepository.deleteById(id);
    }
}
