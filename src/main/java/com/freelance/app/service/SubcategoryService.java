package com.freelance.app.service;

import com.freelance.app.domain.criteria.SubcategoryCriteria;
import com.freelance.app.repository.SubcategoryRepository;
import com.freelance.app.service.dto.SubcategoryDTO;
import com.freelance.app.service.mapper.SubcategoryMapper;
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

    private final SubcategoryMapper subcategoryMapper;

    public SubcategoryService(SubcategoryRepository subcategoryRepository, SubcategoryMapper subcategoryMapper) {
        this.subcategoryRepository = subcategoryRepository;
        this.subcategoryMapper = subcategoryMapper;
    }

    /**
     * Save a subcategory.
     *
     * @param subcategoryDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<SubcategoryDTO> save(SubcategoryDTO subcategoryDTO) {
        LOG.debug("Request to save Subcategory : {}", subcategoryDTO);
        return subcategoryRepository.save(subcategoryMapper.toEntity(subcategoryDTO)).map(subcategoryMapper::toDto);
    }

    /**
     * Update a subcategory.
     *
     * @param subcategoryDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<SubcategoryDTO> update(SubcategoryDTO subcategoryDTO) {
        LOG.debug("Request to update Subcategory : {}", subcategoryDTO);
        return subcategoryRepository.save(subcategoryMapper.toEntity(subcategoryDTO)).map(subcategoryMapper::toDto);
    }

    /**
     * Partially update a subcategory.
     *
     * @param subcategoryDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<SubcategoryDTO> partialUpdate(SubcategoryDTO subcategoryDTO) {
        LOG.debug("Request to partially update Subcategory : {}", subcategoryDTO);

        return subcategoryRepository
            .findById(subcategoryDTO.getId())
            .map(existingSubcategory -> {
                subcategoryMapper.partialUpdate(existingSubcategory, subcategoryDTO);

                return existingSubcategory;
            })
            .flatMap(subcategoryRepository::save)
            .map(subcategoryMapper::toDto);
    }

    /**
     * Find subcategories by Criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<SubcategoryDTO> findByCriteria(SubcategoryCriteria criteria, Pageable pageable) {
        LOG.debug("Request to get all Subcategories by Criteria");
        return subcategoryRepository.findByCriteria(criteria, pageable).map(subcategoryMapper::toDto);
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
    public Flux<SubcategoryDTO> findAllWithEagerRelationships(Pageable pageable) {
        return subcategoryRepository.findAllWithEagerRelationships(pageable).map(subcategoryMapper::toDto);
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
    public Mono<SubcategoryDTO> findOne(Long id) {
        LOG.debug("Request to get Subcategory : {}", id);
        return subcategoryRepository.findOneWithEagerRelationships(id).map(subcategoryMapper::toDto);
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
