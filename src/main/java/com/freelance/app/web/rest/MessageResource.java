package com.freelance.app.web.rest;

import com.freelance.app.domain.criteria.MessageCriteria;
import com.freelance.app.repository.MessageRepository;
import com.freelance.app.service.MessageService;
import com.freelance.app.service.dto.MessageDTO;
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
 * REST controller for managing {@link com.freelance.app.domain.Message}.
 */
@RestController
@RequestMapping("/api/messages")
public class MessageResource {

    private static final Logger LOG = LoggerFactory.getLogger(MessageResource.class);

    private static final String ENTITY_NAME = "message";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MessageService messageService;

    private final MessageRepository messageRepository;

    public MessageResource(MessageService messageService, MessageRepository messageRepository) {
        this.messageService = messageService;
        this.messageRepository = messageRepository;
    }

    /**
     * {@code POST  /messages} : Create a new message.
     *
     * @param messageDTO the messageDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new messageDTO, or with status {@code 400 (Bad Request)} if the message has already an ID.
     */
    @PostMapping("")
    public Mono<ResponseEntity<MessageDTO>> createMessage(@Valid @RequestBody MessageDTO messageDTO) {
        LOG.debug("REST request to save Message : {}", messageDTO);
        if (messageDTO.getId() != null) {
            return Mono.error(new BadRequestAlertException("A new message cannot already have an ID", ENTITY_NAME, "idexists"));
        }
        return messageService
            .save(messageDTO)
            .handle((result, sink) -> {
                try {
                    sink.next(
                        ResponseEntity.created(new URI("/api/messages/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
                } catch (URISyntaxException e) {
                    sink.error(new RuntimeException(e));
                }
            });
    }

    /**
     * {@code PUT  /messages/:id} : Updates an existing message.
     *
     * @param id the id of the messageDTO to save.
     * @param messageDTO the messageDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated messageDTO,
     * or with status {@code 400 (Bad Request)} if the messageDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the messageDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<MessageDTO>> updateMessage(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody MessageDTO messageDTO
    ) {
        LOG.debug("REST request to update Message : {}, {}", id, messageDTO);
        if (messageDTO.getId() == null) {
            return Mono.error(new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull"));
        }
        if (!Objects.equals(id, messageDTO.getId())) {
            return Mono.error(new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid"));
        }

        return messageRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return messageService
                    .update(messageDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /messages/:id} : Partial updates given fields of an existing message, field will ignore if it is null
     *
     * @param id the id of the messageDTO to save.
     * @param messageDTO the messageDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated messageDTO,
     * or with status {@code 400 (Bad Request)} if the messageDTO is not valid,
     * or with status {@code 404 (Not Found)} if the messageDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the messageDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<MessageDTO>> partialUpdateMessage(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody MessageDTO messageDTO
    ) {
        LOG.debug("REST request to partial update Message partially : {}, {}", id, messageDTO);
        if (messageDTO.getId() == null) {
            return Mono.error(new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull"));
        }
        if (!Objects.equals(id, messageDTO.getId())) {
            return Mono.error(new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid"));
        }

        return messageRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<MessageDTO> result = messageService.partialUpdate(messageDTO);

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
     * {@code GET  /messages} : get all the messages.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of messages in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<MessageDTO>>> getAllMessages(
        MessageCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get Messages by criteria: {}", criteria);
        return messageService
            .countByCriteria(criteria)
            .zipWith(messageService.findByCriteria(criteria, pageable).collectList())
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
     * {@code GET  /messages/count} : count all the messages.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countMessages(MessageCriteria criteria) {
        LOG.debug("REST request to count Messages by criteria: {}", criteria);
        return messageService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /messages/:id} : get the "id" message.
     *
     * @param id the id of the messageDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the messageDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<MessageDTO>> getMessage(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Message : {}", id);
        Mono<MessageDTO> messageDTO = messageService.findOne(id);
        return ResponseUtil.wrapOrNotFound(messageDTO);
    }

    /**
     * {@code DELETE  /messages/:id} : delete the "id" message.
     *
     * @param id the id of the messageDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteMessage(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Message : {}", id);
        return messageService
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
