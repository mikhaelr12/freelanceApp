package com.freelance.app.web.rest;

import static java.util.Objects.nonNull;

import com.freelance.app.domain.Dispute;
import com.freelance.app.domain.criteria.DisputeCriteria;
import com.freelance.app.service.DisputeService;
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
 * REST controller for managing {@link com.freelance.app.domain.Dispute}.
 */
@RestController
@RequestMapping("/api/disputes")
public class DisputeResource {

    private static final Logger LOG = LoggerFactory.getLogger(DisputeResource.class);

    private static final String ENTITY_NAME = "dispute";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DisputeService disputeService;

    public DisputeResource(DisputeService disputeService) {
        this.disputeService = disputeService;
    }

    /**
     * {@code POST  /disputes} : Create a new dispute.
     *
     * @param dispute the dispute to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new dispute, or with status {@code 400 (Bad Request)} if the dispute has already an ID.
     */
    @PostMapping("")
    public Mono<ResponseEntity<Dispute>> createDispute(@Valid @RequestBody Dispute dispute) {
        LOG.debug("REST request to save Dispute : {}", dispute);
        if (dispute.getId() != null) {
            return Mono.error(new BadRequestAlertException("A new dispute cannot already have an ID", ENTITY_NAME, "idexists"));
        }
        return disputeService
            .save(dispute)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/disputes/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /disputes/:id} : Updates an existing dispute.
     *
     * @param id the id of the dispute to save.
     * @param dispute the dispute to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated dispute,
     * or with status {@code 400 (Bad Request)} if the dispute is not valid,
     * or with status {@code 500 (Internal Server Error)} if the dispute couldn't be updated.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Dispute>> updateDispute(@PathVariable(required = false) final Long id, @Valid @RequestBody Dispute dispute) {
        LOG.debug("REST request to update Dispute : {}, {}", id, dispute);
        if (dispute.getId() == null) {
            return Mono.error(new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull"));
        }
        if (!Objects.equals(id, dispute.getId())) {
            return Mono.error(new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid"));
        }

        return disputeService
            .findOne(id)
            .flatMap(exists -> {
                if (!nonNull(exists)) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return disputeService
                    .update(dispute)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /disputes/:id} : Partial updates given fields of an existing dispute, field will ignore if it is null
     *
     * @param id the id of the dispute to save.
     * @param dispute the dispute to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated dispute,
     * or with status {@code 400 (Bad Request)} if the dispute is not valid,
     * or with status {@code 404 (Not Found)} if the dispute is not found,
     * or with status {@code 500 (Internal Server Error)} if the dispute couldn't be updated.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Dispute>> partialUpdateDispute(
        @PathVariable(required = false) final Long id,
        @NotNull @RequestBody Dispute dispute
    ) {
        LOG.debug("REST request to partial update Dispute partially : {}, {}", id, dispute);
        if (dispute.getId() == null) {
            return Mono.error(new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull"));
        }
        if (!Objects.equals(id, dispute.getId())) {
            return Mono.error(new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid"));
        }

        return disputeService
            .findOne(id)
            .flatMap(exists -> {
                if (!nonNull(exists)) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Dispute> result = disputeService.partialUpdate(dispute);

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
     * {@code GET  /disputes} : get all the disputes.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of disputes in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<Dispute>>> getAllDisputes(
        DisputeCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get Disputes by criteria: {}", criteria);
        return disputeService
            .countByCriteria(criteria)
            .zipWith(disputeService.findByCriteria(criteria, pageable).collectList())
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
     * {@code GET  /disputes/count} : count all the disputes.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countDisputes(DisputeCriteria criteria) {
        LOG.debug("REST request to count Disputes by criteria: {}", criteria);
        return disputeService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /disputes/:id} : get the "id" dispute.
     *
     * @param id the id of the dispute to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the dispute, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Dispute>> getDispute(@PathVariable Long id) {
        LOG.debug("REST request to get Dispute : {}", id);
        Mono<Dispute> dispute = disputeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(dispute);
    }

    /**
     * {@code DELETE  /disputes/:id} : delete the "id" dispute.
     *
     * @param id the id of the dispute to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteDispute(@PathVariable Long id) {
        LOG.debug("REST request to delete Dispute : {}", id);
        return disputeService
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
