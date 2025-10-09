package com.freelance.app.web.rest;

import com.freelance.app.domain.criteria.DeliveryCriteria;
import com.freelance.app.repository.DeliveryRepository;
import com.freelance.app.service.DeliveryService;
import com.freelance.app.service.dto.DeliveryDTO;
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

    private final DeliveryRepository deliveryRepository;

    public DeliveryResource(DeliveryService deliveryService, DeliveryRepository deliveryRepository) {
        this.deliveryService = deliveryService;
        this.deliveryRepository = deliveryRepository;
    }

    /**
     * {@code POST  /deliveries} : Create a new delivery.
     *
     * @param deliveryDTO the deliveryDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new deliveryDTO, or with status {@code 400 (Bad Request)} if the delivery has already an ID.
     */
    @PostMapping("")
    public Mono<ResponseEntity<DeliveryDTO>> createDelivery(@Valid @RequestBody DeliveryDTO deliveryDTO) {
        LOG.debug("REST request to save Delivery : {}", deliveryDTO);
        if (deliveryDTO.getId() != null) {
            return Mono.error(new BadRequestAlertException("A new delivery cannot already have an ID", ENTITY_NAME, "idexists"));
        }
        return deliveryService
            .save(deliveryDTO)
            .handle((result, sink) -> {
                try {
                    sink.next(
                        ResponseEntity.created(new URI("/api/deliveries/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
                } catch (URISyntaxException e) {
                    sink.error(new RuntimeException(e));
                }
            });
    }

    /**
     * {@code PUT  /deliveries/:id} : Updates an existing delivery.
     *
     * @param id the id of the deliveryDTO to save.
     * @param deliveryDTO the deliveryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated deliveryDTO,
     * or with status {@code 400 (Bad Request)} if the deliveryDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the deliveryDTO couldn't be updated.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<DeliveryDTO>> updateDelivery(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody DeliveryDTO deliveryDTO
    ) {
        LOG.debug("REST request to update Delivery : {}, {}", id, deliveryDTO);
        if (deliveryDTO.getId() == null) {
            return Mono.error(new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull"));
        }
        if (!Objects.equals(id, deliveryDTO.getId())) {
            return Mono.error(new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid"));
        }

        return deliveryRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return deliveryService
                    .update(deliveryDTO)
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
     * @param id the id of the deliveryDTO to save.
     * @param deliveryDTO the deliveryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated deliveryDTO,
     * or with status {@code 400 (Bad Request)} if the deliveryDTO is not valid,
     * or with status {@code 404 (Not Found)} if the deliveryDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the deliveryDTO couldn't be updated.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<DeliveryDTO>> partialUpdateDelivery(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody DeliveryDTO deliveryDTO
    ) {
        LOG.debug("REST request to partial update Delivery partially : {}, {}", id, deliveryDTO);
        if (deliveryDTO.getId() == null) {
            return Mono.error(new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull"));
        }
        if (!Objects.equals(id, deliveryDTO.getId())) {
            return Mono.error(new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid"));
        }

        return deliveryRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<DeliveryDTO> result = deliveryService.partialUpdate(deliveryDTO);

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
    public Mono<ResponseEntity<List<DeliveryDTO>>> getAllDeliveries(
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
     * @param id the id of the deliveryDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the deliveryDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<DeliveryDTO>> getDelivery(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Delivery : {}", id);
        Mono<DeliveryDTO> deliveryDTO = deliveryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(deliveryDTO);
    }

    /**
     * {@code DELETE  /deliveries/:id} : delete the "id" delivery.
     *
     * @param id the id of the deliveryDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteDelivery(@PathVariable("id") Long id) {
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
