package com.freelance.app.service;

import com.freelance.app.domain.criteria.CountryCriteria;
import com.freelance.app.repository.CountryRepository;
import com.freelance.app.service.dto.CountryDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.freelance.app.domain.Country}.
 */
@Service
@Transactional
public class CountryService {

    private static final Logger LOG = LoggerFactory.getLogger(CountryService.class);

    private final CountryRepository countryRepository;

    public CountryService(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    /**
     * Save a country.
     *
     * @param countryDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<CountryDTO> save(CountryDTO countryDTO) {
        LOG.debug("Request to save Country : {}", countryDTO);
        return null;
    }

    /**
     * Update a country.
     *
     * @param countryDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<CountryDTO> update(CountryDTO countryDTO) {
        LOG.debug("Request to update Country : {}", countryDTO);
        return null;
    }

    /**
     * Partially update a country.
     *
     * @param countryDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<CountryDTO> partialUpdate(CountryDTO countryDTO) {
        LOG.debug("Request to partially update Country : {}", countryDTO);
        return null;
    }

    /**
     * Find countries by Criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<CountryDTO> findByCriteria(CountryCriteria criteria, Pageable pageable) {
        LOG.debug("Request to get all Countries by Criteria");
        return null;
    }

    /**
     * Find the count of countries by criteria.
     * @param criteria filtering criteria
     * @return the count of countries
     */
    public Mono<Long> countByCriteria(CountryCriteria criteria) {
        LOG.debug("Request to get the count of all Countries by Criteria");
        return countryRepository.countByCriteria(criteria);
    }

    /**
     * Returns the number of countries available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return countryRepository.count();
    }

    /**
     * Get one country by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<CountryDTO> findOne(Long id) {
        LOG.debug("Request to get Country : {}", id);
        return null;
    }

    /**
     * Delete the country by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Country : {}", id);
        return countryRepository.deleteById(id);
    }
}
