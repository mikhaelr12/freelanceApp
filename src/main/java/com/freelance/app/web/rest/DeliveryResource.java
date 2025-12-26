package com.freelance.app.web.rest;

import static java.util.Objects.nonNull;

import com.freelance.app.domain.Delivery;
import com.freelance.app.domain.criteria.DeliveryCriteria;
import com.freelance.app.service.DeliveryService;
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
 * REST controller for managing {@link com.freelance.app.domain.Delivery}.
 */
@RestController
@RequestMapping("/api/deliveries")
public class DeliveryResource {

    private static final Logger LOG = LoggerFactory.getLogger(DeliveryResource.class);

    private static final String ENTITY_NAME = "delivery";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DeliveryService deliveryService;

    public DeliveryResource(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    /**
     * {@code POST  /deliveries} : Create a new delivery.
     *
     * @param delivery the delivery to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new delivery, or with status {@code 400 (Bad Request)} if the delivery has already an ID.
     */
    @PostMapping("")
    public Mono<ResponseEntity<Delivery>> createDelivery(@Valid @RequestBody Delivery delivery) {
        LOG.debug("REST request to save Delivery : {}", delivery);
        if (delivery.getId() != null) {
            return Mono.error(new BadRequestAlertException("A new delivery cannot already have an ID", ENTITY_NAME, "idexists"));
        }
        return deliveryService
            .save(delivery)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/deliveries/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /deliveries/:id} : Updates an existing delivery.
     *
     * @param id the id of the delivery to save.
     * @param delivery the delivery to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated delivery,
     * or with status {@code 400 (Bad Request)} if the delivery is not valid,
     * or with status {@code 500 (Internal Server Error)} if the delivery couldn't be updated.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Delivery>> updateDelivery(
        @PathVariable(required = false) final Long id,
        @Valid @RequestBody Delivery delivery
    ) {
        LOG.debug("REST request to update Delivery : {}, {}", id, delivery);
        if (delivery.getId() == null) {
            return Mono.error(new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull"));
        }
        if (!Objects.equals(id, delivery.getId())) {
            return Mono.error(new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid"));
        }

        return deliveryService
            .findOne(id)
            .flatMap(exists -> {
                if (!nonNull(exists)) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return deliveryService
                    .update(delivery)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /deliveries/:id} : Partial updates given fields of an existing delivery, field will ignore if it is null
     *
     * @param id the id of the delivery to save.
     * @param delivery the delivery to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated delivery,
     * or with status {@code 400 (Bad Request)} if the delivery is not valid,
     * or with status {@code 404 (Not Found)} if the delivery is not found,
     * or with status {@code 500 (Internal Server Error)} if the delivery couldn't be updated.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Delivery>> partialUpdateDelivery(
        @PathVariable(required = false) final Long id,
        @NotNull @RequestBody Delivery delivery
    ) {
        LOG.debug("REST request to partial update Delivery partially : {}, {}", id, delivery);
        if (delivery.getId() == null) {
            return Mono.error(new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull"));
        }
        if (!Objects.equals(id, delivery.getId())) {
            return Mono.error(new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid"));
        }

        return deliveryService
            .findOne(id)
            .flatMap(exists -> {
                if (!nonNull(exists)) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Delivery> result = deliveryService.partialUpdate(delivery);

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
     * {@code GET  /deliveries} : get all the deliveries.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of deliveries in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<Delivery>>> getAllDeliveries(
        DeliveryCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get Deliveries by criteria: {}", criteria);
        return deliveryService
            .countByCriteria(criteria)
            .zipWith(deliveryService.findByCriteria(criteria, pageable).collectList())
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
     * {@code GET  /deliveries/count} : count all the deliveries.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countDeliveries(DeliveryCriteria criteria) {
        LOG.debug("REST request to count Deliveries by criteria: {}", criteria);
        return deliveryService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /deliveries/:id} : get the "id" delivery.
     *
     * @param id the id of the delivery to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the delivery, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Delivery>> getDelivery(@PathVariable Long id) {
        LOG.debug("REST request to get Delivery : {}", id);
        Mono<Delivery> delivery = deliveryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(delivery);
    }

    /**
     * {@code DELETE  /deliveries/:id} : delete the "id" delivery.
     *
     * @param id the id of the delivery to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteDelivery(@PathVariable Long id) {
        LOG.debug("REST request to delete Delivery : {}", id);
        return deliveryService
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
