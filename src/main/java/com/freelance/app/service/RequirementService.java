package com.freelance.app.service;

import com.freelance.app.domain.Requirement;
import com.freelance.app.domain.criteria.RequirementCriteria;
import com.freelance.app.repository.RequirementRepository;
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

    public RequirementService(RequirementRepository requirementRepository) {
        this.requirementRepository = requirementRepository;
    }

    /**
     * Save a requirement.
     *
     * @param requirement the entity to save.
     * @return the persisted entity.
     */
    public Mono<Requirement> save(Requirement requirement) {
        LOG.debug("Request to save Requirement : {}", requirement);
        return requirementRepository.save(requirement);
    }

    /**
     * Update a requirement.
     *
     * @param requirement the entity to save.
     * @return the persisted entity.
     */
    public Mono<Requirement> update(Requirement requirement) {
        LOG.debug("Request to update Requirement : {}", requirement);
        return requirementRepository.save(requirement);
    }

    /**
     * Partially update a requirement.
     *
     * @param requirement the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<Requirement> partialUpdate(Requirement requirement) {
        LOG.debug("Request to partially update Requirement : {}", requirement);

        return requirementRepository
            .findById(requirement.getId())
            .map(existingRequirement -> {
                if (requirement.getPrompt() != null) {
                    existingRequirement.setPrompt(requirement.getPrompt());
                }
                if (requirement.getAnswer() != null) {
                    existingRequirement.setAnswer(requirement.getAnswer());
                }

                return existingRequirement;
            })
            .flatMap(requirementRepository::save);
    }

    /**
     * Find requirements by Criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<Requirement> findByCriteria(RequirementCriteria criteria, Pageable pageable) {
        LOG.debug("Request to get all Requirements by Criteria");
        return requirementRepository.findByCriteria(criteria, pageable);
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
    public Mono<Requirement> findOne(Long id) {
        LOG.debug("Request to get Requirement : {}", id);
        return requirementRepository.findById(id);
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
