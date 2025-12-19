package com.freelance.app.service;

import com.freelance.app.domain.Message;
import com.freelance.app.domain.criteria.MessageCriteria;
import com.freelance.app.repository.MessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.freelance.app.domain.Message}.
 */
@Service
@Transactional
public class MessageService {

    private static final Logger LOG = LoggerFactory.getLogger(MessageService.class);

    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    /**
     * Save a message.
     *
     * @param message the entity to save.
     * @return the persisted entity.
     */
    public Mono<Message> save(Message message) {
        LOG.debug("Request to save Message : {}", message);
        return messageRepository.save(message);
    }

    /**
     * Update a message.
     *
     * @param message the entity to save.
     * @return the persisted entity.
     */
    public Mono<Message> update(Message message) {
        LOG.debug("Request to update Message : {}", message);
        return messageRepository.save(message);
    }

    /**
     * Partially update a message.
     *
     * @param message the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<Message> partialUpdate(Message message) {
        LOG.debug("Request to partially update Message : {}", message);

        return messageRepository
            .findById(message.getId())
            .map(existingMessage -> {
                if (message.getBody() != null) {
                    existingMessage.setBody(message.getBody());
                }
                if (message.getSentAt() != null) {
                    existingMessage.setSentAt(message.getSentAt());
                }

                return existingMessage;
            })
            .flatMap(messageRepository::save);
    }

    /**
     * Find messages by Criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<Message> findByCriteria(MessageCriteria criteria, Pageable pageable) {
        LOG.debug("Request to get all Messages by Criteria");
        return messageRepository.findByCriteria(criteria, pageable);
    }

    /**
     * Find the count of messages by criteria.
     * @param criteria filtering criteria
     * @return the count of messages
     */
    public Mono<Long> countByCriteria(MessageCriteria criteria) {
        LOG.debug("Request to get the count of all Messages by Criteria");
        return messageRepository.countByCriteria(criteria);
    }

    /**
     * Get all the messages with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Flux<Message> findAllWithEagerRelationships(Pageable pageable) {
        return messageRepository.findAllWithEagerRelationships(pageable);
    }

    /**
     * Returns the number of messages available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return messageRepository.count();
    }

    /**
     * Get one message by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<Message> findOne(Long id) {
        LOG.debug("Request to get Message : {}", id);
        return messageRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the message by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Message : {}", id);
        return messageRepository.deleteById(id);
    }
}
