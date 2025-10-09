package com.freelance.app.service;

import com.freelance.app.domain.criteria.MessageCriteria;
import com.freelance.app.repository.MessageRepository;
import com.freelance.app.service.dto.MessageDTO;
import com.freelance.app.service.mapper.MessageMapper;
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

    private final MessageMapper messageMapper;

    public MessageService(MessageRepository messageRepository, MessageMapper messageMapper) {
        this.messageRepository = messageRepository;
        this.messageMapper = messageMapper;
    }

    /**
     * Save a message.
     *
     * @param messageDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<MessageDTO> save(MessageDTO messageDTO) {
        LOG.debug("Request to save Message : {}", messageDTO);
        return messageRepository.save(messageMapper.toEntity(messageDTO)).map(messageMapper::toDto);
    }

    /**
     * Update a message.
     *
     * @param messageDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<MessageDTO> update(MessageDTO messageDTO) {
        LOG.debug("Request to update Message : {}", messageDTO);
        return messageRepository.save(messageMapper.toEntity(messageDTO)).map(messageMapper::toDto);
    }

    /**
     * Partially update a message.
     *
     * @param messageDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<MessageDTO> partialUpdate(MessageDTO messageDTO) {
        LOG.debug("Request to partially update Message : {}", messageDTO);

        return messageRepository
            .findById(messageDTO.getId())
            .map(existingMessage -> {
                messageMapper.partialUpdate(existingMessage, messageDTO);

                return existingMessage;
            })
            .flatMap(messageRepository::save)
            .map(messageMapper::toDto);
    }

    /**
     * Find messages by Criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<MessageDTO> findByCriteria(MessageCriteria criteria, Pageable pageable) {
        LOG.debug("Request to get all Messages by Criteria");
        return messageRepository.findByCriteria(criteria, pageable).map(messageMapper::toDto);
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
    public Flux<MessageDTO> findAllWithEagerRelationships(Pageable pageable) {
        return messageRepository.findAllWithEagerRelationships(pageable).map(messageMapper::toDto);
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
    public Mono<MessageDTO> findOne(Long id) {
        LOG.debug("Request to get Message : {}", id);
        return messageRepository.findOneWithEagerRelationships(id).map(messageMapper::toDto);
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
