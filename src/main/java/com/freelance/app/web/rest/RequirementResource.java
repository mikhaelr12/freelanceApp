package com.freelance.app.web.rest;

import com.freelance.app.domain.Requirement;
import com.freelance.app.domain.criteria.RequirementCriteria;
import com.freelance.app.repository.RequirementRepository;
import com.freelance.app.service.RequirementService;
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
 * REST controller for managing {@link com.freelance.app.domain.Requirement}.
 */
@RestController
@RequestMapping("/api/requirements")
public class RequirementResource {

    private static final Logger LOG = LoggerFactory.getLogger(RequirementResource.class);

    private static final String ENTITY_NAME = "requirement";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RequirementService requirementService;

    private final RequirementRepository requirementRepository;

    public RequirementResource(RequirementService requirementService, RequirementRepository requirementRepository) {
        this.requirementService = requirementService;
        this.requirementRepository = requirementRepository;
    }

    /**
     * {@code POST  /requirements} : Create a new requirement.
     *
     * @param requirement the requirement to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new requirement, or with status {@code 400 (Bad Request)} if the requirement has already an ID.
     */
    @PostMapping("")
    public Mono<ResponseEntity<Requirement>> createRequirement(@Valid @RequestBody Requirement requirement) {
        LOG.debug("REST request to save Requirement : {}", requirement);
        if (requirement.getId() != null) {
            throw new BadRequestAlertException("A new requirement cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return requirementService
            .save(requirement)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/requirements/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /requirements/:id} : Updates an existing requirement.
     *
     * @param id the id of the requirement to save.
     * @param requirement the requirement to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated requirement,
     * or with status {@code 400 (Bad Request)} if the requirement is not valid,
     * or with status {@code 500 (Internal Server Error)} if the requirement couldn't be updated.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Requirement>> updateRequirement(
        @PathVariable(required = false) final Long id,
        @Valid @RequestBody Requirement requirement
    ) {
        LOG.debug("REST request to update Requirement : {}, {}", id, requirement);
        if (requirement.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, requirement.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return requirementRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return requirementService
                    .update(requirement)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /requirements/:id} : Partial updates given fields of an existing requirement, field will ignore if it is null
     *
     * @param id the id of the requirement to save.
     * @param requirement the requirement to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated requirement,
     * or with status {@code 400 (Bad Request)} if the requirement is not valid,
     * or with status {@code 404 (Not Found)} if the requirement is not found,
     * or with status {@code 500 (Internal Server Error)} if the requirement couldn't be updated.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Requirement>> partialUpdateRequirement(
        @PathVariable(required = false) final Long id,
        @NotNull @RequestBody Requirement requirement
    ) {
        LOG.debug("REST request to partial update Requirement partially : {}, {}", id, requirement);
        if (requirement.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, requirement.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return requirementRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Requirement> result = requirementService.partialUpdate(requirement);

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
     * {@code GET  /requirements} : get all the requirements.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of requirements in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<Requirement>>> getAllRequirements(
        RequirementCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get Requirements by criteria: {}", criteria);
        return requirementService
            .countByCriteria(criteria)
            .zipWith(requirementService.findByCriteria(criteria, pageable).collectList())
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
     * {@code GET  /requirements/count} : count all the requirements.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countRequirements(RequirementCriteria criteria) {
        LOG.debug("REST request to count Requirements by criteria: {}", criteria);
        return requirementService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /requirements/:id} : get the "id" requirement.
     *
     * @param id the id of the requirement to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the requirement, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Requirement>> getRequirement(@PathVariable Long id) {
        LOG.debug("REST request to get Requirement : {}", id);
        Mono<Requirement> requirement = requirementService.findOne(id);
        return ResponseUtil.wrapOrNotFound(requirement);
    }

    /**
     * {@code DELETE  /requirements/:id} : delete the "id" requirement.
     *
     * @param id the id of the requirement to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteRequirement(@PathVariable Long id) {
        LOG.debug("REST request to delete Requirement : {}", id);
        return requirementService
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
