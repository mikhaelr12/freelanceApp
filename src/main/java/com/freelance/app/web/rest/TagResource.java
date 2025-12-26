package com.freelance.app.web.rest;

import static java.util.Objects.nonNull;

import com.freelance.app.domain.Tag;
import com.freelance.app.domain.criteria.TagCriteria;
import com.freelance.app.service.TagService;
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
 * REST controller for managing {@link com.freelance.app.domain.Tag}.
 */
@RestController
@RequestMapping("/api/tags")
public class TagResource {

    private static final Logger LOG = LoggerFactory.getLogger(TagResource.class);

    private static final String ENTITY_NAME = "tag";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TagService tagService;

    public TagResource(TagService tagService) {
        this.tagService = tagService;
    }

    /**
     * {@code POST  /tags} : Create a new tag.
     *
     * @param tag the tag to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new tag, or with status {@code 400 (Bad Request)} if the tag has already an ID.
     */
    @PostMapping("")
    public Mono<ResponseEntity<Tag>> createTag(@Valid @RequestBody Tag tag) {
        LOG.debug("REST request to save Tag : {}", tag);
        if (tag.getId() != null) {
            return Mono.error(new BadRequestAlertException("A new tag cannot already have an ID", ENTITY_NAME, "idexists"));
        }
        return tagService
            .save(tag)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/tags/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /tags/:id} : Updates an existing tag.
     *
     * @param id the id of the tag to save.
     * @param tag the tag to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tag,
     * or with status {@code 400 (Bad Request)} if the tag is not valid,
     * or with status {@code 500 (Internal Server Error)} if the tag couldn't be updated.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Tag>> updateTag(@PathVariable(required = false) final Long id, @Valid @RequestBody Tag tag) {
        LOG.debug("REST request to update Tag : {}, {}", id, tag);
        if (tag.getId() == null) {
            return Mono.error(new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull"));
        }
        if (!Objects.equals(id, tag.getId())) {
            return Mono.error(new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid"));
        }

        return tagService
            .findOne(id)
            .flatMap(exists -> {
                if (!nonNull(exists)) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return tagService
                    .update(tag)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /tags/:id} : Partial updates given fields of an existing tag, field will ignore if it is null
     *
     * @param id the id of the tag to save.
     * @param tag the tag to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tag,
     * or with status {@code 400 (Bad Request)} if the tag is not valid,
     * or with status {@code 404 (Not Found)} if the tag is not found,
     * or with status {@code 500 (Internal Server Error)} if the tag couldn't be updated.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Tag>> partialUpdateTag(@PathVariable(required = false) final Long id, @NotNull @RequestBody Tag tag) {
        LOG.debug("REST request to partial update Tag partially : {}, {}", id, tag);
        if (tag.getId() == null) {
            return Mono.error(new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull"));
        }
        if (!Objects.equals(id, tag.getId())) {
            return Mono.error(new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid"));
        }

        return tagService
            .findOne(id)
            .flatMap(exists -> {
                if (!nonNull(exists)) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Tag> result = tagService.partialUpdate(tag);

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
     * {@code GET  /tags} : get all the tags.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of tags in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<Tag>>> getAllTags(
        TagCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get Tags by criteria: {}", criteria);
        return tagService
            .countByCriteria(criteria)
            .zipWith(tagService.findByCriteria(criteria, pageable).collectList())
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
     * {@code GET  /tags/count} : count all the tags.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countTags(TagCriteria criteria) {
        LOG.debug("REST request to count Tags by criteria: {}", criteria);
        return tagService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /tags/:id} : get the "id" tag.
     *
     * @param id the id of the tag to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the tag, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Tag>> getTag(@PathVariable Long id) {
        LOG.debug("REST request to get Tag : {}", id);
        Mono<Tag> tag = tagService.findOne(id);
        return ResponseUtil.wrapOrNotFound(tag);
    }

    /**
     * {@code DELETE  /tags/:id} : delete the "id" tag.
     *
     * @param id the id of the tag to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteTag(@PathVariable Long id) {
        LOG.debug("REST request to delete Tag : {}", id);
        return tagService
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
