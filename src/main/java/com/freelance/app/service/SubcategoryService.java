package com.freelance.app.service;

import com.freelance.app.domain.Subcategory;
import com.freelance.app.domain.criteria.SubcategoryCriteria;
import com.freelance.app.repository.SubcategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.freelance.app.domain.Subcategory}.
 */
@Service
@Transactional
public class SubcategoryService {

    private static final Logger LOG = LoggerFactory.getLogger(SubcategoryService.class);

    private final SubcategoryRepository subcategoryRepository;

    public SubcategoryService(SubcategoryRepository subcategoryRepository) {
        this.subcategoryRepository = subcategoryRepository;
    }

    /**
     * Save a subcategory.
     *
     * @param subcategory the entity to save.
     * @return the persisted entity.
     */
    public Mono<Subcategory> save(Subcategory subcategory) {
        LOG.debug("Request to save Subcategory : {}", subcategory);
        return subcategoryRepository.save(subcategory);
    }

    /**
     * Update a subcategory.
     *
     * @param subcategory the entity to save.
     * @return the persisted entity.
     */
    public Mono<Subcategory> update(Subcategory subcategory) {
        LOG.debug("Request to update Subcategory : {}", subcategory);
        return subcategoryRepository.save(subcategory);
    }

    /**
     * Partially update a subcategory.
     *
     * @param subcategory the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<Subcategory> partialUpdate(Subcategory subcategory) {
        LOG.debug("Request to partially update Subcategory : {}", subcategory);

        return subcategoryRepository
            .findById(subcategory.getId())
            .map(existingSubcategory -> {
                if (subcategory.getName() != null) {
                    existingSubcategory.setName(subcategory.getName());
                }
                if (subcategory.getCreatedDate() != null) {
                    existingSubcategory.setCreatedDate(subcategory.getCreatedDate());
                }
                if (subcategory.getLastModifiedDate() != null) {
                    existingSubcategory.setLastModifiedDate(subcategory.getLastModifiedDate());
                }
                if (subcategory.getCreatedBy() != null) {
                    existingSubcategory.setCreatedBy(subcategory.getCreatedBy());
                }
                if (subcategory.getLastModifiedBy() != null) {
                    existingSubcategory.setLastModifiedBy(subcategory.getLastModifiedBy());
                }
                if (subcategory.getActive() != null) {
                    existingSubcategory.setActive(subcategory.getActive());
                }

                return existingSubcategory;
            })
            .flatMap(subcategoryRepository::save);
    }

    /**
     * Find subcategories by Criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<Subcategory> findByCriteria(SubcategoryCriteria criteria, Pageable pageable) {
        LOG.debug("Request to get all Subcategories by Criteria");
        return subcategoryRepository.findByCriteria(criteria, pageable);
    }

    /**
     * Find the count of subcategories by criteria.
     * @param criteria filtering criteria
     * @return the count of subcategories
     */
    public Mono<Long> countByCriteria(SubcategoryCriteria criteria) {
        LOG.debug("Request to get the count of all Subcategories by Criteria");
        return subcategoryRepository.countByCriteria(criteria);
    }

    /**
     * Get all the subcategories with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Flux<Subcategory> findAllWithEagerRelationships(Pageable pageable) {
        return subcategoryRepository.findAllWithEagerRelationships(pageable);
    }

    /**
     * Returns the number of subcategories available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return subcategoryRepository.count();
    }

    /**
     * Get one subcategory by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<Subcategory> findOne(Long id) {
        LOG.debug("Request to get Subcategory : {}", id);
        return subcategoryRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the subcategory by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Subcategory : {}", id);
        return subcategoryRepository.deleteById(id);
    }
}
