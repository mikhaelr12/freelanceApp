package com.freelance.app.service;

import com.freelance.app.domain.criteria.SkillCriteria;
import com.freelance.app.repository.SkillRepository;
import com.freelance.app.service.dto.SkillDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.freelance.app.domain.Skill}.
 */
@Service
@Transactional
public class SkillService {

    private static final Logger LOG = LoggerFactory.getLogger(SkillService.class);

    private final SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    /**
     * Save a skill.
     *
     * @param skillDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<SkillDTO> save(SkillDTO skillDTO) {
        LOG.debug("Request to save Skill : {}", skillDTO);
        return null;
    }

    /**
     * Update a skill.
     *
     * @param skillDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<SkillDTO> update(SkillDTO skillDTO) {
        LOG.debug("Request to update Skill : {}", skillDTO);
        return null;
    }

    /**
     * Partially update a skill.
     *
     * @param skillDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<SkillDTO> partialUpdate(SkillDTO skillDTO) {
        LOG.debug("Request to partially update Skill : {}", skillDTO);

        return null;
    }

    /**
     * Find skills by Criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<SkillDTO> findByCriteria(SkillCriteria criteria, Pageable pageable) {
        LOG.debug("Request to get all Skills by Criteria");
        return null;
    }

    /**
     * Find the count of skills by criteria.
     * @param criteria filtering criteria
     * @return the count of skills
     */
    public Mono<Long> countByCriteria(SkillCriteria criteria) {
        LOG.debug("Request to get the count of all Skills by Criteria");
        return skillRepository.countByCriteria(criteria);
    }

    /**
     * Get all the skills with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Flux<SkillDTO> findAllWithEagerRelationships(Pageable pageable) {
        return null;
    }

    /**
     * Returns the number of skills available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return skillRepository.count();
    }

    /**
     * Get one skill by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<SkillDTO> findOne(Long id) {
        LOG.debug("Request to get Skill : {}", id);
        return null;
    }

    /**
     * Delete the skill by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Skill : {}", id);
        return skillRepository.deleteById(id);
    }
}
