package com.freelance.app.service;

import com.freelance.app.domain.OfferPackage;
import com.freelance.app.domain.criteria.OfferPackageCriteria;
import com.freelance.app.repository.OfferPackageRepository;
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

    public OfferPackageService(OfferPackageRepository offerPackageRepository) {
        this.offerPackageRepository = offerPackageRepository;
    }

    /**
     * Save a offerPackage.
     *
     * @param offerPackage the entity to save.
     * @return the persisted entity.
     */
    public Mono<OfferPackage> save(OfferPackage offerPackage) {
        LOG.debug("Request to save OfferPackage : {}", offerPackage);
        return offerPackageRepository.save(offerPackage);
    }

    /**
     * Update a offerPackage.
     *
     * @param offerPackage the entity to save.
     * @return the persisted entity.
     */
    public Mono<OfferPackage> update(OfferPackage offerPackage) {
        LOG.debug("Request to update OfferPackage : {}", offerPackage);
        return offerPackageRepository.save(offerPackage);
    }

    /**
     * Partially update a offerPackage.
     *
     * @param offerPackage the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<OfferPackage> partialUpdate(OfferPackage offerPackage) {
        LOG.debug("Request to partially update OfferPackage : {}", offerPackage);

        return offerPackageRepository
            .findById(offerPackage.getId())
            .map(existingOfferPackage -> {
                if (offerPackage.getName() != null) {
                    existingOfferPackage.setName(offerPackage.getName());
                }
                if (offerPackage.getDescription() != null) {
                    existingOfferPackage.setDescription(offerPackage.getDescription());
                }
                if (offerPackage.getPrice() != null) {
                    existingOfferPackage.setPrice(offerPackage.getPrice());
                }
                if (offerPackage.getCurrency() != null) {
                    existingOfferPackage.setCurrency(offerPackage.getCurrency());
                }
                if (offerPackage.getDeliveryDays() != null) {
                    existingOfferPackage.setDeliveryDays(offerPackage.getDeliveryDays());
                }
                if (offerPackage.getPackageTier() != null) {
                    existingOfferPackage.setPackageTier(offerPackage.getPackageTier());
                }
                if (offerPackage.getActive() != null) {
                    existingOfferPackage.setActive(offerPackage.getActive());
                }
                if (offerPackage.getCreatedDate() != null) {
                    existingOfferPackage.setCreatedDate(offerPackage.getCreatedDate());
                }
                if (offerPackage.getLastModifiedDate() != null) {
                    existingOfferPackage.setLastModifiedDate(offerPackage.getLastModifiedDate());
                }
                if (offerPackage.getCreatedBy() != null) {
                    existingOfferPackage.setCreatedBy(offerPackage.getCreatedBy());
                }
                if (offerPackage.getLastModifiedBy() != null) {
                    existingOfferPackage.setLastModifiedBy(offerPackage.getLastModifiedBy());
                }

                return existingOfferPackage;
            })
            .flatMap(offerPackageRepository::save);
    }

    /**
     * Find offerPackages by Criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<OfferPackage> findByCriteria(OfferPackageCriteria criteria, Pageable pageable) {
        LOG.debug("Request to get all OfferPackages by Criteria");
        return offerPackageRepository.findByCriteria(criteria, pageable);
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
    public Flux<OfferPackage> findAllWithEagerRelationships(Pageable pageable) {
        return offerPackageRepository.findAllWithEagerRelationships(pageable);
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
    public Mono<OfferPackage> findOne(Long id) {
        LOG.debug("Request to get OfferPackage : {}", id);
        return offerPackageRepository.findOneWithEagerRelationships(id);
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
