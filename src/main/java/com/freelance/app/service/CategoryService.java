package com.freelance.app.service;

import com.freelance.app.domain.Category;
import com.freelance.app.domain.criteria.CategoryCriteria;
import com.freelance.app.repository.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.freelance.app.domain.Category}.
 */
@Service
@Transactional
public class CategoryService {

    private static final Logger LOG = LoggerFactory.getLogger(CategoryService.class);

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * Save a category.
     *
     * @param category the entity to save.
     * @return the persisted entity.
     */
    public Mono<Category> save(Category category) {
        LOG.debug("Request to save Category : {}", category);
        return categoryRepository.save(category);
    }

    /**
     * Update a category.
     *
     * @param category the entity to save.
     * @return the persisted entity.
     */
    public Mono<Category> update(Category category) {
        LOG.debug("Request to update Category : {}", category);
        return categoryRepository.save(category);
    }

    /**
     * Partially update a category.
     *
     * @param category the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<Category> partialUpdate(Category category) {
        LOG.debug("Request to partially update Category : {}", category);

        return categoryRepository
            .findById(category.getId())
            .map(existingCategory -> {
                if (category.getName() != null) {
                    existingCategory.setName(category.getName());
                }
                if (category.getCreatedDate() != null) {
                    existingCategory.setCreatedDate(category.getCreatedDate());
                }
                if (category.getLastModifiedDate() != null) {
                    existingCategory.setLastModifiedDate(category.getLastModifiedDate());
                }
                if (category.getCreatedBy() != null) {
                    existingCategory.setCreatedBy(category.getCreatedBy());
                }
                if (category.getLastModifiedBy() != null) {
                    existingCategory.setLastModifiedBy(category.getLastModifiedBy());
                }
                if (category.getActive() != null) {
                    existingCategory.setActive(category.getActive());
                }

                return existingCategory;
            })
            .flatMap(categoryRepository::save);
    }

    /**
     * Find categories by Criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<Category> findByCriteria(CategoryCriteria criteria, Pageable pageable) {
        LOG.debug("Request to get all Categories by Criteria");
        return categoryRepository.findByCriteria(criteria, pageable);
    }

    /**
     * Find the count of categories by criteria.
     * @param criteria filtering criteria
     * @return the count of categories
     */
    public Mono<Long> countByCriteria(CategoryCriteria criteria) {
        LOG.debug("Request to get the count of all Categories by Criteria");
        return categoryRepository.countByCriteria(criteria);
    }

    /**
     * Returns the number of categories available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return categoryRepository.count();
    }

    /**
     * Get one category by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<Category> findOne(Long id) {
        LOG.debug("Request to get Category : {}", id);
        return categoryRepository.findById(id);
    }

    /**
     * Delete the category by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Category : {}", id);
        return categoryRepository.deleteById(id);
    }
}
