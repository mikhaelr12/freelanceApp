package com.freelance.app.web.rest;

import com.freelance.app.domain.Conversation;
import com.freelance.app.domain.criteria.ConversationCriteria;
import com.freelance.app.repository.ConversationRepository;
import com.freelance.app.service.ConversationService;
import com.freelance.app.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.ForwardedHeaderUtils;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.freelance.app.domain.Conversation}.
 */
@RestController
@RequestMapping("/api/conversations")
public class ConversationResource {

    private static final Logger LOG = LoggerFactory.getLogger(ConversationResource.class);

    private static final String ENTITY_NAME = "conversation";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ConversationService conversationService;

    private final ConversationRepository conversationRepository;

    public ConversationResource(ConversationService conversationService, ConversationRepository conversationRepository) {
        this.conversationService = conversationService;
        this.conversationRepository = conversationRepository;
    }

    /**
     * {@code POST  /conversations} : Create a new conversation.
     *
     * @param conversation the conversation to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new conversation, or with status {@code 400 (Bad Request)} if the conversation has already an ID.
     */
    @PostMapping("")
    public Mono<ResponseEntity<Conversation>> createConversation(@Valid @RequestBody Conversation conversation) {
        LOG.debug("REST request to save Conversation : {}", conversation);
        if (conversation.getId() != null) {
            throw new BadRequestAlertException("A new conversation cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return conversationService
            .save(conversation)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/conversations/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /conversations/:id} : Updates an existing conversation.
     *
     * @param id the id of the conversation to save.
     * @param conversation the conversation to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated conversation,
     * or with status {@code 400 (Bad Request)} if the conversation is not valid,
     * or with status {@code 500 (Internal Server Error)} if the conversation couldn't be updated.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Conversation>> updateConversation(
        @PathVariable(required = false) final Long id,
        @Valid @RequestBody Conversation conversation
    ) {
        LOG.debug("REST request to update Conversation : {}, {}", id, conversation);
        if (conversation.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, conversation.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return conversationRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return conversationService
                    .update(conversation)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /conversations/:id} : Partial updates given fields of an existing conversation, field will ignore if it is null
     *
     * @param id the id of the conversation to save.
     * @param conversation the conversation to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated conversation,
     * or with status {@code 400 (Bad Request)} if the conversation is not valid,
     * or with status {@code 404 (Not Found)} if the conversation is not found,
     * or with status {@code 500 (Internal Server Error)} if the conversation couldn't be updated.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Conversation>> partialUpdateConversation(
        @PathVariable(required = false) final Long id,
        @NotNull @RequestBody Conversation conversation
    ) {
        LOG.debug("REST request to partial update Conversation partially : {}, {}", id, conversation);
        if (conversation.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, conversation.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return conversationRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Conversation> result = conversationService.partialUpdate(conversation);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /conversations} : get all the conversations.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of conversations in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<Conversation>>> getAllConversations(
        ConversationCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get Conversations by criteria: {}", criteria);
        return conversationService
            .countByCriteria(criteria)
            .zipWith(conversationService.findByCriteria(criteria, pageable).collectList())
            .map(countWithEntities ->
                ResponseEntity.ok()
                    .headers(
                        PaginationUtil.generatePaginationHttpHeaders(
                            ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                            new PageImpl<>(countWithEntities.getT2(), pageable, countWithEntities.getT1())
                        )
                    )
                    .body(countWithEntities.getT2())
            );
    }

    /**
     * {@code GET  /conversations/count} : count all the conversations.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countConversations(ConversationCriteria criteria) {
        LOG.debug("REST request to count Conversations by criteria: {}", criteria);
        return conversationService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /conversations/:id} : get the "id" conversation.
     *
     * @param id the id of the conversation to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the conversation, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Conversation>> getConversation(@PathVariable Long id) {
        LOG.debug("REST request to get Conversation : {}", id);
        Mono<Conversation> conversation = conversationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(conversation);
    }

    /**
     * {@code DELETE  /conversations/:id} : delete the "id" conversation.
     *
     * @param id the id of the conversation to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteConversation(@PathVariable Long id) {
        LOG.debug("REST request to delete Conversation : {}", id);
        return conversationService
            .delete(id)
            .then(
                Mono.just(
                    ResponseEntity.noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }
}
