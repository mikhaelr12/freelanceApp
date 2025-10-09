package com.freelance.app.service;

import com.freelance.app.domain.criteria.RequirementCriteria;
import com.freelance.app.repository.RequirementRepository;
import com.freelance.app.service.dto.RequirementDTO;
import com.freelance.app.service.mapper.RequirementMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.freelance.app.domain.Requirement}.
 */
@Service
@Transactional
public class RequirementService {

    private static final Logger LOG = LoggerFactory.getLogger(RequirementService.class);

    private final RequirementRepository requirementRepository;

    private final RequirementMapper requirementMapper;

    public RequirementService(RequirementRepository requirementRepository, RequirementMapper requirementMapper) {
        this.requirementRepository = requirementRepository;
        this.requirementMapper = requirementMapper;
    }

    /**
     * Save a requirement.
     *
     * @param requirementDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<RequirementDTO> save(RequirementDTO requirementDTO) {
        LOG.debug("Request to save Requirement : {}", requirementDTO);
        return requirementRepository.save(requirementMapper.toEntity(requirementDTO)).map(requirementMapper::toDto);
    }

    /**
     * Update a requirement.
     *
     * @param requirementDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<RequirementDTO> update(RequirementDTO requirementDTO) {
        LOG.debug("Request to update Requirement : {}", requirementDTO);
        return requirementRepository.save(requirementMapper.toEntity(requirementDTO)).map(requirementMapper::toDto);
    }

    /**
     * Partially update a requirement.
     *
     * @param requirementDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<RequirementDTO> partialUpdate(RequirementDTO requirementDTO) {
        LOG.debug("Request to partially update Requirement : {}", requirementDTO);

        return requirementRepository
            .findById(requirementDTO.getId())
            .map(existingRequirement -> {
                requirementMapper.partialUpdate(existingRequirement, requirementDTO);

                return existingRequirement;
            })
            .flatMap(requirementRepository::save)
            .map(requirementMapper::toDto);
    }

    /**
     * Find requirements by Criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<RequirementDTO> findByCriteria(RequirementCriteria criteria, Pageable pageable) {
        LOG.debug("Request to get all Requirements by Criteria");
        return requirementRepository.findByCriteria(criteria, pageable).map(requirementMapper::toDto);
    }

    /**
     * Find the count of requirements by criteria.
     * @param criteria filtering criteria
     * @return the count of requirements
     */
    public Mono<Long> countByCriteria(RequirementCriteria criteria) {
        LOG.debug("Request to get the count of all Requirements by Criteria");
        return requirementRepository.countByCriteria(criteria);
    }

    /**
     * Returns the number of requirements available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return requirementRepository.count();
    }

    /**
     * Get one requirement by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<RequirementDTO> findOne(Long id) {
        LOG.debug("Request to get Requirement : {}", id);
        return requirementRepository.findById(id).map(requirementMapper::toDto);
    }

    /**
     * Delete the requirement by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Requirement : {}", id);
        return requirementRepository.deleteById(id);
    }
}
