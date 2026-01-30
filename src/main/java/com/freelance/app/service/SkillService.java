package com.freelance.app.service;

import com.freelance.app.domain.Skill;
import com.freelance.app.domain.criteria.SkillCriteria;
import com.freelance.app.repository.SkillRepository;
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
     * @param skill the entity to save.
     * @return the persisted entity.
     */
    public Mono<Skill> save(Skill skill) {
        LOG.debug("Request to save Skill : {}", skill);
        return skillRepository.save(skill);
    }

    /**
     * Update a skill.
     *
     * @param skill the entity to save.
     * @return the persisted entity.
     */
    public Mono<Skill> update(Skill skill) {
        LOG.debug("Request to update Skill : {}", skill);
        return skillRepository.save(skill);
    }

    /**
     * Partially update a skill.
     *
     * @param skill the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<Skill> partialUpdate(Skill skill) {
        LOG.debug("Request to partially update Skill : {}", skill);

        return skillRepository
            .findById(skill.getId())
            .map(existingSkill -> {
                if (skill.getName() != null) {
                    existingSkill.setName(skill.getName());
                }
                if (skill.getCreatedDate() != null) {
                    existingSkill.setCreatedDate(skill.getCreatedDate());
                }
                if (skill.getLastModifiedDate() != null) {
                    existingSkill.setLastModifiedDate(skill.getLastModifiedDate());
                }
                if (skill.getCreatedBy() != null) {
                    existingSkill.setCreatedBy(skill.getCreatedBy());
                }
                if (skill.getLastModifiedBy() != null) {
                    existingSkill.setLastModifiedBy(skill.getLastModifiedBy());
                }
                if (skill.getActive() != null) {
                    existingSkill.setActive(skill.getActive());
                }

                return existingSkill;
            })
            .flatMap(skillRepository::save);
    }

    /**
     * Find skills by Criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<Skill> findByCriteria(SkillCriteria criteria, Pageable pageable) {
        LOG.debug("Request to get all Skills by Criteria");
        return skillRepository.findByCriteria(criteria, pageable);
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
    public Mono<Skill> findOne(Long id) {
        LOG.debug("Request to get Skill : {}", id);
        return skillRepository.findById(id);
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
