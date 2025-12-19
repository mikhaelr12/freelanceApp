package com.freelance.app.service;

import com.freelance.app.domain.Tag;
import com.freelance.app.domain.criteria.TagCriteria;
import com.freelance.app.repository.TagRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.freelance.app.domain.Tag}.
 */
@Service
@Transactional
public class TagService {

    private static final Logger LOG = LoggerFactory.getLogger(TagService.class);

    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    /**
     * Save a tag.
     *
     * @param tag the entity to save.
     * @return the persisted entity.
     */
    public Mono<Tag> save(Tag tag) {
        LOG.debug("Request to save Tag : {}", tag);
        return tagRepository.save(tag);
    }

    /**
     * Update a tag.
     *
     * @param tag the entity to save.
     * @return the persisted entity.
     */
    public Mono<Tag> update(Tag tag) {
        LOG.debug("Request to update Tag : {}", tag);
        return tagRepository.save(tag);
    }

    /**
     * Partially update a tag.
     *
     * @param tag the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<Tag> partialUpdate(Tag tag) {
        LOG.debug("Request to partially update Tag : {}", tag);

        return tagRepository
            .findById(tag.getId())
            .map(existingTag -> {
                if (tag.getName() != null) {
                    existingTag.setName(tag.getName());
                }
                if (tag.getCreatedDate() != null) {
                    existingTag.setCreatedDate(tag.getCreatedDate());
                }
                if (tag.getLastModifiedDate() != null) {
                    existingTag.setLastModifiedDate(tag.getLastModifiedDate());
                }
                if (tag.getCreatedBy() != null) {
                    existingTag.setCreatedBy(tag.getCreatedBy());
                }
                if (tag.getLastModifiedBy() != null) {
                    existingTag.setLastModifiedBy(tag.getLastModifiedBy());
                }

                return existingTag;
            })
            .flatMap(tagRepository::save);
    }

    /**
     * Find tags by Criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<Tag> findByCriteria(TagCriteria criteria, Pageable pageable) {
        LOG.debug("Request to get all Tags by Criteria");
        return tagRepository.findByCriteria(criteria, pageable);
    }

    /**
     * Find the count of tags by criteria.
     * @param criteria filtering criteria
     * @return the count of tags
     */
    public Mono<Long> countByCriteria(TagCriteria criteria) {
        LOG.debug("Request to get the count of all Tags by Criteria");
        return tagRepository.countByCriteria(criteria);
    }

    /**
     * Returns the number of tags available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return tagRepository.count();
    }

    /**
     * Get one tag by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<Tag> findOne(Long id) {
        LOG.debug("Request to get Tag : {}", id);
        return tagRepository.findById(id);
    }

    /**
     * Delete the tag by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Tag : {}", id);
        return tagRepository.deleteById(id);
    }
}
