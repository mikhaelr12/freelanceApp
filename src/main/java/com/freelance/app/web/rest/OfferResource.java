package com.freelance.app.web.rest;

import com.freelance.app.domain.criteria.OfferCriteria;
import com.freelance.app.repository.OfferRepository;
import com.freelance.app.service.OfferService;
import com.freelance.app.service.dto.OfferDTO;
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
 * REST controller for managing {@link com.freelance.app.domain.Offer}.
 */
@RestController
@RequestMapping("/api/offers")
public class OfferResource {

    private static final Logger LOG = LoggerFactory.getLogger(OfferResource.class);

    private static final String ENTITY_NAME = "offer";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final OfferService offerService;

    private final OfferRepository offerRepository;

    public OfferResource(OfferService offerService, OfferRepository offerRepository) {
        this.offerService = offerService;
        this.offerRepository = offerRepository;
    }

    /**
     * {@code POST  /offers} : Create a new offer.
     *
     * @param offerDTO the offerDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new offerDTO, or with status {@code 400 (Bad Request)} if the offer has already an ID.
     */
    @PostMapping("")
    public Mono<ResponseEntity<OfferDTO>> createOffer(@Valid @RequestBody OfferDTO offerDTO) {
        LOG.debug("REST request to save Offer : {}", offerDTO);
        if (offerDTO.getId() != null) {
            return Mono.error(new BadRequestAlertException("A new offer cannot already have an ID", ENTITY_NAME, "idexists"));
        }
        return offerService
            .save(offerDTO)
            .handle((result, sink) -> {
                try {
                    sink.next(
                        ResponseEntity.created(new URI("/api/offers/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
                } catch (URISyntaxException e) {
                    sink.error(new RuntimeException(e));
                }
            });
    }

    /**
     * {@code PUT  /offers/:id} : Updates an existing offer.
     *
     * @param id the id of the offerDTO to save.
     * @param offerDTO the offerDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated offerDTO,
     * or with status {@code 400 (Bad Request)} if the offerDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the offerDTO couldn't be updated.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<OfferDTO>> updateOffer(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody OfferDTO offerDTO
    ) {
        LOG.debug("REST request to update Offer : {}, {}", id, offerDTO);
        if (offerDTO.getId() == null) {
            return Mono.error(new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull"));
        }
        if (!Objects.equals(id, offerDTO.getId())) {
            return Mono.error(new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid"));
        }

        return offerRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return offerService
                    .update(offerDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /offers/:id} : Partial updates given fields of an existing offer, field will ignore if it is null
     *
     * @param id the id of the offerDTO to save.
     * @param offerDTO the offerDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated offerDTO,
     * or with status {@code 400 (Bad Request)} if the offerDTO is not valid,
     * or with status {@code 404 (Not Found)} if the offerDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the offerDTO couldn't be updated.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<OfferDTO>> partialUpdateOffer(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody OfferDTO offerDTO
    ) {
        LOG.debug("REST request to partial update Offer partially : {}, {}", id, offerDTO);
        if (offerDTO.getId() == null) {
            return Mono.error(new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull"));
        }
        if (!Objects.equals(id, offerDTO.getId())) {
            return Mono.error(new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid"));
        }

        return offerRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<OfferDTO> result = offerService.partialUpdate(offerDTO);

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
     * {@code GET  /offers} : get all the offers.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of offers in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<OfferDTO>>> getAllOffers(
        OfferCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get Offers by criteria: {}", criteria);
        return offerService
            .countByCriteria(criteria)
            .zipWith(offerService.findByCriteria(criteria, pageable).collectList())
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
     * {@code GET  /offers/count} : count all the offers.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countOffers(OfferCriteria criteria) {
        LOG.debug("REST request to count Offers by criteria: {}", criteria);
        return offerService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /offers/:id} : get the "id" offer.
     *
     * @param id the id of the offerDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the offerDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<OfferDTO>> getOffer(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Offer : {}", id);
        Mono<OfferDTO> offerDTO = offerService.findOne(id);
        return ResponseUtil.wrapOrNotFound(offerDTO);
    }

    /**
     * {@code DELETE  /offers/:id} : delete the "id" offer.
     *
     * @param id the id of the offerDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteOffer(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Offer : {}", id);
        return offerService
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
