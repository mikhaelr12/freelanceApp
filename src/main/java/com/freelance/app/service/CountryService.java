package com.freelance.app.service;

import com.freelance.app.domain.Country;
import com.freelance.app.domain.criteria.CountryCriteria;
import com.freelance.app.repository.CountryRepository;
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
     * @param country the entity to save.
     * @return the persisted entity.
     */
    public Mono<Country> save(Country country) {
        LOG.debug("Request to save Country : {}", country);
        return countryRepository.save(country);
    }

    /**
     * Update a country.
     *
     * @param country the entity to save.
     * @return the persisted entity.
     */
    public Mono<Country> update(Country country) {
        LOG.debug("Request to update Country : {}", country);
        return countryRepository.save(country);
    }

    /**
     * Partially update a country.
     *
     * @param country the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<Country> partialUpdate(Country country) {
        LOG.debug("Request to partially update Country : {}", country);

        return countryRepository
            .findById(country.getId())
            .map(existingCountry -> {
                if (country.getName() != null) {
                    existingCountry.setName(country.getName());
                }
                if (country.getIso2() != null) {
                    existingCountry.setIso2(country.getIso2());
                }
                if (country.getIso3() != null) {
                    existingCountry.setIso3(country.getIso3());
                }
                if (country.getRegion() != null) {
                    existingCountry.setRegion(country.getRegion());
                }
                if (country.getCreatedDate() != null) {
                    existingCountry.setCreatedDate(country.getCreatedDate());
                }
                if (country.getLastModifiedDate() != null) {
                    existingCountry.setLastModifiedDate(country.getLastModifiedDate());
                }
                if (country.getCreatedBy() != null) {
                    existingCountry.setCreatedBy(country.getCreatedBy());
                }
                if (country.getLastModifiedBy() != null) {
                    existingCountry.setLastModifiedBy(country.getLastModifiedBy());
                }
                if (country.getActive() != null) {
                    existingCountry.setActive(country.getActive());
                }

                return existingCountry;
            })
            .flatMap(countryRepository::save);
    }

    /**
     * Find countries by Criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<Country> findByCriteria(CountryCriteria criteria, Pageable pageable) {
        LOG.debug("Request to get all Countries by Criteria");
        return countryRepository.findByCriteria(criteria, pageable);
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
    public Mono<Country> findOne(Long id) {
        LOG.debug("Request to get Country : {}", id);
        return countryRepository.findById(id);
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
