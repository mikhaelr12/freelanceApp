package com.freelance.app.web.rest;

import static java.util.Objects.nonNull;

import com.freelance.app.domain.Country;
import com.freelance.app.domain.criteria.CountryCriteria;
import com.freelance.app.service.CountryService;
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
 * REST controller for managing {@link com.freelance.app.domain.Country}.
 */
@RestController
@RequestMapping("/api/countries")
public class CountryResource {

    private static final Logger LOG = LoggerFactory.getLogger(CountryResource.class);

    private static final String ENTITY_NAME = "country";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CountryService countryService;

    public CountryResource(CountryService countryService) {
        this.countryService = countryService;
    }

    /**
     * {@code POST  /countries} : Create a new country.
     *
     * @param country the country to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new country, or with status {@code 400 (Bad Request)} if the country has already an ID.
     */
    @PostMapping("")
    public Mono<ResponseEntity<Country>> createCountry(@Valid @RequestBody Country country) {
        LOG.debug("REST request to save Country : {}", country);
        if (country.getId() != null) {
            return Mono.error(new BadRequestAlertException("A) new country cannot already have an ID", ENTITY_NAME, "idexists"));
        }
        return countryService
            .save(country)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/countries/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /countries/:id} : Updates an existing country.
     *
     * @param id the id of the country to save.
     * @param country the country to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated country,
     * or with status {@code 400 (Bad Request)} if the country is not valid,
     * or with status {@code 500 (Internal Server Error)} if the country couldn't be updated.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Country>> updateCountry(@PathVariable(required = false) final Long id, @Valid @RequestBody Country country) {
        LOG.debug("REST request to update Country : {}, {}", id, country);
        if (country.getId() == null) {
            return Mono.error(new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull"));
        }
        if (!Objects.equals(id, country.getId())) {
            return Mono.error(new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid"));
        }

        return countryService
            .findOne(id)
            .flatMap(exists -> {
                if (!nonNull(exists)) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return countryService
                    .update(country)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /countries/:id} : Partial updates given fields of an existing country, field will ignore if it is null
     *
     * @param id the id of the country to save.
     * @param country the country to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated country,
     * or with status {@code 400 (Bad Request)} if the country is not valid,
     * or with status {@code 404 (Not Found)} if the country is not found,
     * or with status {@code 500 (Internal Server Error)} if the country couldn't be updated.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Country>> partialUpdateCountry(
        @PathVariable(required = false) final Long id,
        @NotNull @RequestBody Country country
    ) {
        LOG.debug("REST request to partial update Country partially : {}, {}", id, country);
        if (country.getId() == null) {
            return Mono.error(new BadRequestAlertException("Invalid) id", ENTITY_NAME, "idnull"));
        }
        if (!Objects.equals(id, country.getId())) {
            return Mono.error(new BadRequestAlertException("Invalid) ID", ENTITY_NAME, "idinvalid"));
        }

        return countryService
            .findOne(id)
            .flatMap(exists -> {
                if (!nonNull(exists)) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Country> result = countryService.partialUpdate(country);

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
     * {@code GET  /countries} : get all the countries.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of countries in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<Country>>> getAllCountries(
        CountryCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get Countries by criteria: {}", criteria);
        return countryService
            .countByCriteria(criteria)
            .zipWith(countryService.findByCriteria(criteria, pageable).collectList())
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
     * {@code GET  /countries/count} : count all the countries.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countCountries(CountryCriteria criteria) {
        LOG.debug("REST request to count Countries by criteria: {}", criteria);
        return countryService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /countries/:id} : get the "id" country.
     *
     * @param id the id of the country to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the country, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Country>> getCountry(@PathVariable Long id) {
        LOG.debug("REST request to get Country : {}", id);
        Mono<Country> country = countryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(country);
    }

    /**
     * {@code DELETE  /countries/:id} : delete the "id" country.
     *
     * @param id the id of the country to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteCountry(@PathVariable Long id) {
        LOG.debug("REST request to delete Country : {}", id);
        return countryService
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
