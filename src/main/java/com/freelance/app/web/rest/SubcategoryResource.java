package com.freelance.app.web.rest;

import static java.util.Objects.nonNull;

import com.freelance.app.domain.Subcategory;
import com.freelance.app.domain.criteria.SubcategoryCriteria;
import com.freelance.app.service.SubcategoryService;
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
 * REST controller for managing {@link com.freelance.app.domain.Subcategory}.
 */
@RestController
@RequestMapping("/api/subcategories")
public class SubcategoryResource {

    private static final Logger LOG = LoggerFactory.getLogger(SubcategoryResource.class);

    private static final String ENTITY_NAME = "subcategory";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SubcategoryService subcategoryService;

    public SubcategoryResource(SubcategoryService subcategoryService) {
        this.subcategoryService = subcategoryService;
    }

    /**
     * {@code POST  /subcategories} : Create a new subcategory.
     *
     * @param subcategory the subcategory to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new subcategory, or with status {@code 400 (Bad Request)} if the subcategory has already an ID.
     */
    @PostMapping("")
    public Mono<ResponseEntity<Subcategory>> createSubcategory(@Valid @RequestBody Subcategory subcategory) {
        LOG.debug("REST request to save Subcategory : {}", subcategory);
        if (subcategory.getId() != null) {
            return Mono.error(new BadRequestAlertException("A new subcategory cannot already have an ID", ENTITY_NAME, "idexists"));
        }
        return subcategoryService
            .save(subcategory)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/subcategories/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /subcategories/:id} : Updates an existing subcategory.
     *
     * @param id the id of the subcategory to save.
     * @param subcategory the subcategory to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated subcategory,
     * or with status {@code 400 (Bad Request)} if the subcategory is not valid,
     * or with status {@code 500 (Internal Server Error)} if the subcategory couldn't be updated.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Subcategory>> updateSubcategory(
        @PathVariable(required = false) final Long id,
        @Valid @RequestBody Subcategory subcategory
    ) {
        LOG.debug("REST request to update Subcategory : {}, {}", id, subcategory);
        if (subcategory.getId() == null) {
            return Mono.error(new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull"));
        }
        if (!Objects.equals(id, subcategory.getId())) {
            return Mono.error(new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid"));
        }

        return subcategoryService
            .findOne(id)
            .flatMap(exists -> {
                if (!nonNull(exists)) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return subcategoryService
                    .update(subcategory)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /subcategories/:id} : Partial updates given fields of an existing subcategory, field will ignore if it is null
     *
     * @param id the id of the subcategory to save.
     * @param subcategory the subcategory to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated subcategory,
     * or with status {@code 400 (Bad Request)} if the subcategory is not valid,
     * or with status {@code 404 (Not Found)} if the subcategory is not found,
     * or with status {@code 500 (Internal Server Error)} if the subcategory couldn't be updated.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Subcategory>> partialUpdateSubcategory(
        @PathVariable(required = false) final Long id,
        @NotNull @RequestBody Subcategory subcategory
    ) {
        LOG.debug("REST request to partial update Subcategory partially : {}, {}", id, subcategory);
        if (subcategory.getId() == null) {
            return Mono.error(new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull"));
        }
        if (!Objects.equals(id, subcategory.getId())) {
            return Mono.error(new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid"));
        }

        return subcategoryService
            .findOne(id)
            .flatMap(exists -> {
                if (!nonNull(exists)) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Subcategory> result = subcategoryService.partialUpdate(subcategory);

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
     * {@code GET  /subcategories} : get all the subcategories.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of subcategories in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<Subcategory>>> getAllSubcategories(
        SubcategoryCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get Subcategories by criteria: {}", criteria);
        return subcategoryService
            .countByCriteria(criteria)
            .zipWith(subcategoryService.findByCriteria(criteria, pageable).collectList())
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
     * {@code GET  /subcategories/count} : count all the subcategories.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countSubcategories(SubcategoryCriteria criteria) {
        LOG.debug("REST request to count Subcategories by criteria: {}", criteria);
        return subcategoryService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /subcategories/:id} : get the "id" subcategory.
     *
     * @param id the id of the subcategory to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the subcategory, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Subcategory>> getSubcategory(@PathVariable Long id) {
        LOG.debug("REST request to get Subcategory : {}", id);
        Mono<Subcategory> subcategory = subcategoryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(subcategory);
    }

    /**
     * {@code DELETE  /subcategories/:id} : delete the "id" subcategory.
     *
     * @param id the id of the subcategory to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteSubcategory(@PathVariable Long id) {
        LOG.debug("REST request to delete Subcategory : {}", id);
        return subcategoryService
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
