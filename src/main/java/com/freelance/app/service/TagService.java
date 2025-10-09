package com.freelance.app.service;

import com.freelance.app.domain.criteria.TagCriteria;
import com.freelance.app.repository.TagRepository;
import com.freelance.app.service.dto.TagDTO;
import com.freelance.app.service.mapper.TagMapper;
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

    private final TagMapper tagMapper;

    public TagService(TagRepository tagRepository, TagMapper tagMapper) {
        this.tagRepository = tagRepository;
        this.tagMapper = tagMapper;
    }

    /**
     * Save a tag.
     *
     * @param tagDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<TagDTO> save(TagDTO tagDTO) {
        LOG.debug("Request to save Tag : {}", tagDTO);
        return tagRepository.save(tagMapper.toEntity(tagDTO)).map(tagMapper::toDto);
    }

    /**
     * Update a tag.
     *
     * @param tagDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<TagDTO> update(TagDTO tagDTO) {
        LOG.debug("Request to update Tag : {}", tagDTO);
        return tagRepository.save(tagMapper.toEntity(tagDTO)).map(tagMapper::toDto);
    }

    /**
     * Partially update a tag.
     *
     * @param tagDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<TagDTO> partialUpdate(TagDTO tagDTO) {
        LOG.debug("Request to partially update Tag : {}", tagDTO);

        return tagRepository
            .findById(tagDTO.getId())
            .map(existingTag -> {
                tagMapper.partialUpdate(existingTag, tagDTO);

                return existingTag;
            })
            .flatMap(tagRepository::save)
            .map(tagMapper::toDto);
    }

    /**
     * Find tags by Criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<TagDTO> findByCriteria(TagCriteria criteria, Pageable pageable) {
        LOG.debug("Request to get all Tags by Criteria");
        return tagRepository.findByCriteria(criteria, pageable).map(tagMapper::toDto);
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
    public Mono<TagDTO> findOne(Long id) {
        LOG.debug("Request to get Tag : {}", id);
        return tagRepository.findById(id).map(tagMapper::toDto);
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
