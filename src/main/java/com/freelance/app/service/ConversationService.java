package com.freelance.app.service;

import com.freelance.app.domain.Conversation;
import com.freelance.app.domain.criteria.ConversationCriteria;
import com.freelance.app.repository.ConversationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.freelance.app.domain.Conversation}.
 */
@Service
@Transactional
public class ConversationService {

    private static final Logger LOG = LoggerFactory.getLogger(ConversationService.class);

    private final ConversationRepository conversationRepository;

    public ConversationService(ConversationRepository conversationRepository) {
        this.conversationRepository = conversationRepository;
    }

    /**
     * Save a conversation.
     *
     * @param conversation the entity to save.
     * @return the persisted entity.
     */
    public Mono<Conversation> save(Conversation conversation) {
        LOG.debug("Request to save Conversation : {}", conversation);
        return conversationRepository.save(conversation);
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
    public Mono<Conversation> findOne(Long id) {
        LOG.debug("Request to get Conversation : {}", id);
        return conversationRepository.findById(id);
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
