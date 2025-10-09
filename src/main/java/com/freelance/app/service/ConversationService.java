package com.freelance.app.service;

import com.freelance.app.domain.criteria.ConversationCriteria;
import com.freelance.app.repository.ConversationRepository;
import com.freelance.app.service.dto.ConversationDTO;
import com.freelance.app.service.mapper.ConversationMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.freelance.app.domain.Conversation}.
 */
@Service
@Transactional
public class ConversationService {

    private static final Logger LOG = LoggerFactory.getLogger(ConversationService.class);

    private final ConversationRepository conversationRepository;

    private final ConversationMapper conversationMapper;

    public ConversationService(ConversationRepository conversationRepository, ConversationMapper conversationMapper) {
        this.conversationRepository = conversationRepository;
        this.conversationMapper = conversationMapper;
    }

    /**
     * Save a conversation.
     *
     * @param conversationDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<ConversationDTO> save(ConversationDTO conversationDTO) {
        LOG.debug("Request to save Conversation : {}", conversationDTO);
        return conversationRepository.save(conversationMapper.toEntity(conversationDTO)).map(conversationMapper::toDto);
    }

    /**
     * Update a conversation.
     *
     * @param conversationDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<ConversationDTO> update(ConversationDTO conversationDTO) {
        LOG.debug("Request to update Conversation : {}", conversationDTO);
        return conversationRepository.save(conversationMapper.toEntity(conversationDTO)).map(conversationMapper::toDto);
    }

    /**
     * Partially update a conversation.
     *
     * @param conversationDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<ConversationDTO> partialUpdate(ConversationDTO conversationDTO) {
        LOG.debug("Request to partially update Conversation : {}", conversationDTO);

        return conversationRepository
            .findById(conversationDTO.getId())
            .map(existingConversation -> {
                conversationMapper.partialUpdate(existingConversation, conversationDTO);

                return existingConversation;
            })
            .flatMap(conversationRepository::save)
            .map(conversationMapper::toDto);
    }

    /**
     * Find conversations by Criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<ConversationDTO> findByCriteria(ConversationCriteria criteria, Pageable pageable) {
        LOG.debug("Request to get all Conversations by Criteria");
        return conversationRepository.findByCriteria(criteria, pageable).map(conversationMapper::toDto);
    }

    /**
     * Find the count of conversations by criteria.
     * @param criteria filtering criteria
     * @return the count of conversations
     */
    public Mono<Long> countByCriteria(ConversationCriteria criteria) {
        LOG.debug("Request to get the count of all Conversations by Criteria");
        return conversationRepository.countByCriteria(criteria);
    }

    /**
     * Returns the number of conversations available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return conversationRepository.count();
    }

    /**
     * Get one conversation by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<ConversationDTO> findOne(Long id) {
        LOG.debug("Request to get Conversation : {}", id);
        return conversationRepository.findById(id).map(conversationMapper::toDto);
    }

    /**
     * Delete the conversation by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Conversation : {}", id);
        return conversationRepository.deleteById(id);
    }
}
