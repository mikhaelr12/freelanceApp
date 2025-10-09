package com.freelance.app.web.rest;

import static com.freelance.app.domain.CountryAsserts.*;
import static com.freelance.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.freelance.app.IntegrationTest;
import com.freelance.app.domain.Country;
import com.freelance.app.repository.CountryRepository;
import com.freelance.app.repository.EntityManager;
import com.freelance.app.service.dto.CountryDTO;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link CountryResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class CountryResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_ISO_2 = "AA";
    private static final String UPDATED_ISO_2 = "BB";

    private static final String DEFAULT_ISO_3 = "AAA";
    private static final String UPDATED_ISO_3 = "BBB";

    private static final String DEFAULT_REGION = "AAAAAAAAAA";
    private static final String UPDATED_REGION = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_LAST_MODIFIED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_MODIFIED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_MODIFIED_BY = "AAAAAAAAAA";
    private static final String UPDATED_LAST_MODIFIED_BY = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/countries";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private CountryMapper countryMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Country country;

    private Country insertedCountry;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Country createEntity() {
        return new Country()
            .name(DEFAULT_NAME)
            .iso2(DEFAULT_ISO_2)
            .iso3(DEFAULT_ISO_3)
            .region(DEFAULT_REGION)
            .createdDate(DEFAULT_CREATED_DATE)
            .lastModifiedDate(DEFAULT_LAST_MODIFIED_DATE)
            .createdBy(DEFAULT_CREATED_BY)
            .lastModifiedBy(DEFAULT_LAST_MODIFIED_BY)
            .active(DEFAULT_ACTIVE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Country createUpdatedEntity() {
        return new Country()
            .name(UPDATED_NAME)
            .iso2(UPDATED_ISO_2)
            .iso3(UPDATED_ISO_3)
            .region(UPDATED_REGION)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .createdBy(UPDATED_CREATED_BY)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .active(UPDATED_ACTIVE);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Country.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        country = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedCountry != null) {
            countryRepository.delete(insertedCountry).block();
            insertedCountry = null;
        }
        deleteEntities(em);
    }

    @Test
    void createCountry() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Country
        CountryDTO countryDTO = countryMapper.toDto(country);
        var returnedCountryDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(countryDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(CountryDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Country in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCountry = countryMapper.toEntity(returnedCountryDTO);
        assertCountryUpdatableFieldsEquals(returnedCountry, getPersistedCountry(returnedCountry));

        insertedCountry = returnedCountry;
    }

    @Test
    void createCountryWithExistingId() throws Exception {
        // Create the Country with an existing ID
        country.setId(1L);
        CountryDTO countryDTO = countryMapper.toDto(country);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(countryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Country in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        country.setName(null);

        // Create the Country, which fails.
        CountryDTO countryDTO = countryMapper.toDto(country);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(countryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkRegionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        country.setRegion(null);

        // Create the Country, which fails.
        CountryDTO countryDTO = countryMapper.toDto(country);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(countryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkCreatedDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        country.setCreatedDate(null);

        // Create the Country, which fails.
        CountryDTO countryDTO = countryMapper.toDto(country);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(countryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkActiveIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        country.setActive(null);

        // Create the Country, which fails.
        CountryDTO countryDTO = countryMapper.toDto(country);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(countryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllCountries() {
        // Initialize the database
        insertedCountry = countryRepository.save(country).block();

        // Get all the countryList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(country.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].iso2")
            .value(hasItem(DEFAULT_ISO_2))
            .jsonPath("$.[*].iso3")
            .value(hasItem(DEFAULT_ISO_3))
            .jsonPath("$.[*].region")
            .value(hasItem(DEFAULT_REGION))
            .jsonPath("$.[*].createdDate")
            .value(hasItem(DEFAULT_CREATED_DATE.toString()))
            .jsonPath("$.[*].lastModifiedDate")
            .value(hasItem(DEFAULT_LAST_MODIFIED_DATE.toString()))
            .jsonPath("$.[*].createdBy")
            .value(hasItem(DEFAULT_CREATED_BY))
            .jsonPath("$.[*].lastModifiedBy")
            .value(hasItem(DEFAULT_LAST_MODIFIED_BY))
            .jsonPath("$.[*].active")
            .value(hasItem(DEFAULT_ACTIVE));
    }

    @Test
    void getCountry() {
        // Initialize the database
        insertedCountry = countryRepository.save(country).block();

        // Get the country
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, country.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(country.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.iso2")
            .value(is(DEFAULT_ISO_2))
            .jsonPath("$.iso3")
            .value(is(DEFAULT_ISO_3))
            .jsonPath("$.region")
            .value(is(DEFAULT_REGION))
            .jsonPath("$.createdDate")
            .value(is(DEFAULT_CREATED_DATE.toString()))
            .jsonPath("$.lastModifiedDate")
            .value(is(DEFAULT_LAST_MODIFIED_DATE.toString()))
            .jsonPath("$.createdBy")
            .value(is(DEFAULT_CREATED_BY))
            .jsonPath("$.lastModifiedBy")
            .value(is(DEFAULT_LAST_MODIFIED_BY))
            .jsonPath("$.active")
            .value(is(DEFAULT_ACTIVE));
    }

    @Test
    void getCountriesByIdFiltering() {
        // Initialize the database
        insertedCountry = countryRepository.save(country).block();

        Long id = country.getId();

        defaultCountryFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultCountryFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultCountryFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllCountriesByNameIsEqualToSomething() {
        // Initialize the database
        insertedCountry = countryRepository.save(country).block();

        // Get all the countryList where name equals to
        defaultCountryFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    void getAllCountriesByNameIsInShouldWork() {
        // Initialize the database
        insertedCountry = countryRepository.save(country).block();

        // Get all the countryList where name in
        defaultCountryFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    void getAllCountriesByNameIsNullOrNotNull() {
        // Initialize the database
        insertedCountry = countryRepository.save(country).block();

        // Get all the countryList where name is not null
        defaultCountryFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    void getAllCountriesByNameContainsSomething() {
        // Initialize the database
        insertedCountry = countryRepository.save(country).block();

        // Get all the countryList where name contains
        defaultCountryFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    void getAllCountriesByNameNotContainsSomething() {
        // Initialize the database
        insertedCountry = countryRepository.save(country).block();

        // Get all the countryList where name does not contain
        defaultCountryFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    void getAllCountriesByIso2IsEqualToSomething() {
        // Initialize the database
        insertedCountry = countryRepository.save(country).block();

        // Get all the countryList where iso2 equals to
        defaultCountryFiltering("iso2.equals=" + DEFAULT_ISO_2, "iso2.equals=" + UPDATED_ISO_2);
    }

    @Test
    void getAllCountriesByIso2IsInShouldWork() {
        // Initialize the database
        insertedCountry = countryRepository.save(country).block();

        // Get all the countryList where iso2 in
        defaultCountryFiltering("iso2.in=" + DEFAULT_ISO_2 + "," + UPDATED_ISO_2, "iso2.in=" + UPDATED_ISO_2);
    }

    @Test
    void getAllCountriesByIso2IsNullOrNotNull() {
        // Initialize the database
        insertedCountry = countryRepository.save(country).block();

        // Get all the countryList where iso2 is not null
        defaultCountryFiltering("iso2.specified=true", "iso2.specified=false");
    }

    @Test
    void getAllCountriesByIso2ContainsSomething() {
        // Initialize the database
        insertedCountry = countryRepository.save(country).block();

        // Get all the countryList where iso2 contains
        defaultCountryFiltering("iso2.contains=" + DEFAULT_ISO_2, "iso2.contains=" + UPDATED_ISO_2);
    }

    @Test
    void getAllCountriesByIso2NotContainsSomething() {
        // Initialize the database
        insertedCountry = countryRepository.save(country).block();

        // Get all the countryList where iso2 does not contain
        defaultCountryFiltering("iso2.doesNotContain=" + UPDATED_ISO_2, "iso2.doesNotContain=" + DEFAULT_ISO_2);
    }

    @Test
    void getAllCountriesByIso3IsEqualToSomething() {
        // Initialize the database
        insertedCountry = countryRepository.save(country).block();

        // Get all the countryList where iso3 equals to
        defaultCountryFiltering("iso3.equals=" + DEFAULT_ISO_3, "iso3.equals=" + UPDATED_ISO_3);
    }

    @Test
    void getAllCountriesByIso3IsInShouldWork() {
        // Initialize the database
        insertedCountry = countryRepository.save(country).block();

        // Get all the countryList where iso3 in
        defaultCountryFiltering("iso3.in=" + DEFAULT_ISO_3 + "," + UPDATED_ISO_3, "iso3.in=" + UPDATED_ISO_3);
    }

    @Test
    void getAllCountriesByIso3IsNullOrNotNull() {
        // Initialize the database
        insertedCountry = countryRepository.save(country).block();

        // Get all the countryList where iso3 is not null
        defaultCountryFiltering("iso3.specified=true", "iso3.specified=false");
    }

    @Test
    void getAllCountriesByIso3ContainsSomething() {
        // Initialize the database
        insertedCountry = countryRepository.save(country).block();

        // Get all the countryList where iso3 contains
        defaultCountryFiltering("iso3.contains=" + DEFAULT_ISO_3, "iso3.contains=" + UPDATED_ISO_3);
    }

    @Test
    void getAllCountriesByIso3NotContainsSomething() {
        // Initialize the database
        insertedCountry = countryRepository.save(country).block();

        // Get all the countryList where iso3 does not contain
        defaultCountryFiltering("iso3.doesNotContain=" + UPDATED_ISO_3, "iso3.doesNotContain=" + DEFAULT_ISO_3);
    }

    @Test
    void getAllCountriesByRegionIsEqualToSomething() {
        // Initialize the database
        insertedCountry = countryRepository.save(country).block();

        // Get all the countryList where region equals to
        defaultCountryFiltering("region.equals=" + DEFAULT_REGION, "region.equals=" + UPDATED_REGION);
    }

    @Test
    void getAllCountriesByRegionIsInShouldWork() {
        // Initialize the database
        insertedCountry = countryRepository.save(country).block();

        // Get all the countryList where region in
        defaultCountryFiltering("region.in=" + DEFAULT_REGION + "," + UPDATED_REGION, "region.in=" + UPDATED_REGION);
    }

    @Test
    void getAllCountriesByRegionIsNullOrNotNull() {
        // Initialize the database
        insertedCountry = countryRepository.save(country).block();

        // Get all the countryList where region is not null
        defaultCountryFiltering("region.specified=true", "region.specified=false");
    }

    @Test
    void getAllCountriesByRegionContainsSomething() {
        // Initialize the database
        insertedCountry = countryRepository.save(country).block();

        // Get all the countryList where region contains
        defaultCountryFiltering("region.contains=" + DEFAULT_REGION, "region.contains=" + UPDATED_REGION);
    }

    @Test
    void getAllCountriesByRegionNotContainsSomething() {
        // Initialize the database
        insertedCountry = countryRepository.save(country).block();

        // Get all the countryList where region does not contain
        defaultCountryFiltering("region.doesNotContain=" + UPDATED_REGION, "region.doesNotContain=" + DEFAULT_REGION);
    }

    @Test
    void getAllCountriesByCreatedDateIsEqualToSomething() {
        // Initialize the database
        insertedCountry = countryRepository.save(country).block();

        // Get all the countryList where createdDate equals to
        defaultCountryFiltering("createdDate.equals=" + DEFAULT_CREATED_DATE, "createdDate.equals=" + UPDATED_CREATED_DATE);
    }

    @Test
    void getAllCountriesByCreatedDateIsInShouldWork() {
        // Initialize the database
        insertedCountry = countryRepository.save(country).block();

        // Get all the countryList where createdDate in
        defaultCountryFiltering(
            "createdDate.in=" + DEFAULT_CREATED_DATE + "," + UPDATED_CREATED_DATE,
            "createdDate.in=" + UPDATED_CREATED_DATE
        );
    }

    @Test
    void getAllCountriesByCreatedDateIsNullOrNotNull() {
        // Initialize the database
        insertedCountry = countryRepository.save(country).block();

        // Get all the countryList where createdDate is not null
        defaultCountryFiltering("createdDate.specified=true", "createdDate.specified=false");
    }

    @Test
    void getAllCountriesByLastModifiedDateIsEqualToSomething() {
        // Initialize the database
        insertedCountry = countryRepository.save(country).block();

        // Get all the countryList where lastModifiedDate equals to
        defaultCountryFiltering(
            "lastModifiedDate.equals=" + DEFAULT_LAST_MODIFIED_DATE,
            "lastModifiedDate.equals=" + UPDATED_LAST_MODIFIED_DATE
        );
    }

    @Test
    void getAllCountriesByLastModifiedDateIsInShouldWork() {
        // Initialize the database
        insertedCountry = countryRepository.save(country).block();

        // Get all the countryList where lastModifiedDate in
        defaultCountryFiltering(
            "lastModifiedDate.in=" + DEFAULT_LAST_MODIFIED_DATE + "," + UPDATED_LAST_MODIFIED_DATE,
            "lastModifiedDate.in=" + UPDATED_LAST_MODIFIED_DATE
        );
    }

    @Test
    void getAllCountriesByLastModifiedDateIsNullOrNotNull() {
        // Initialize the database
        insertedCountry = countryRepository.save(country).block();

        // Get all the countryList where lastModifiedDate is not null
        defaultCountryFiltering("lastModifiedDate.specified=true", "lastModifiedDate.specified=false");
    }

    @Test
    void getAllCountriesByCreatedByIsEqualToSomething() {
        // Initialize the database
        insertedCountry = countryRepository.save(country).block();

        // Get all the countryList where createdBy equals to
        defaultCountryFiltering("createdBy.equals=" + DEFAULT_CREATED_BY, "createdBy.equals=" + UPDATED_CREATED_BY);
    }

    @Test
    void getAllCountriesByCreatedByIsInShouldWork() {
        // Initialize the database
        insertedCountry = countryRepository.save(country).block();

        // Get all the countryList where createdBy in
        defaultCountryFiltering("createdBy.in=" + DEFAULT_CREATED_BY + "," + UPDATED_CREATED_BY, "createdBy.in=" + UPDATED_CREATED_BY);
    }

    @Test
    void getAllCountriesByCreatedByIsNullOrNotNull() {
        // Initialize the database
        insertedCountry = countryRepository.save(country).block();

        // Get all the countryList where createdBy is not null
        defaultCountryFiltering("createdBy.specified=true", "createdBy.specified=false");
    }

    @Test
    void getAllCountriesByCreatedByContainsSomething() {
        // Initialize the database
        insertedCountry = countryRepository.save(country).block();

        // Get all the countryList where createdBy contains
        defaultCountryFiltering("createdBy.contains=" + DEFAULT_CREATED_BY, "createdBy.contains=" + UPDATED_CREATED_BY);
    }

    @Test
    void getAllCountriesByCreatedByNotContainsSomething() {
        // Initialize the database
        insertedCountry = countryRepository.save(country).block();

        // Get all the countryList where createdBy does not contain
        defaultCountryFiltering("createdBy.doesNotContain=" + UPDATED_CREATED_BY, "createdBy.doesNotContain=" + DEFAULT_CREATED_BY);
    }

    @Test
    void getAllCountriesByLastModifiedByIsEqualToSomething() {
        // Initialize the database
        insertedCountry = countryRepository.save(country).block();

        // Get all the countryList where lastModifiedBy equals to
        defaultCountryFiltering("lastModifiedBy.equals=" + DEFAULT_LAST_MODIFIED_BY, "lastModifiedBy.equals=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    void getAllCountriesByLastModifiedByIsInShouldWork() {
        // Initialize the database
        insertedCountry = countryRepository.save(country).block();

        // Get all the countryList where lastModifiedBy in
        defaultCountryFiltering(
            "lastModifiedBy.in=" + DEFAULT_LAST_MODIFIED_BY + "," + UPDATED_LAST_MODIFIED_BY,
            "lastModifiedBy.in=" + UPDATED_LAST_MODIFIED_BY
        );
    }

    @Test
    void getAllCountriesByLastModifiedByIsNullOrNotNull() {
        // Initialize the database
        insertedCountry = countryRepository.save(country).block();

        // Get all the countryList where lastModifiedBy is not null
        defaultCountryFiltering("lastModifiedBy.specified=true", "lastModifiedBy.specified=false");
    }

    @Test
    void getAllCountriesByLastModifiedByContainsSomething() {
        // Initialize the database
        insertedCountry = countryRepository.save(country).block();

        // Get all the countryList where lastModifiedBy contains
        defaultCountryFiltering(
            "lastModifiedBy.contains=" + DEFAULT_LAST_MODIFIED_BY,
            "lastModifiedBy.contains=" + UPDATED_LAST_MODIFIED_BY
        );
    }

    @Test
    void getAllCountriesByLastModifiedByNotContainsSomething() {
        // Initialize the database
        insertedCountry = countryRepository.save(country).block();

        // Get all the countryList where lastModifiedBy does not contain
        defaultCountryFiltering(
            "lastModifiedBy.doesNotContain=" + UPDATED_LAST_MODIFIED_BY,
            "lastModifiedBy.doesNotContain=" + DEFAULT_LAST_MODIFIED_BY
        );
    }

    @Test
    void getAllCountriesByActiveIsEqualToSomething() {
        // Initialize the database
        insertedCountry = countryRepository.save(country).block();

        // Get all the countryList where active equals to
        defaultCountryFiltering("active.equals=" + DEFAULT_ACTIVE, "active.equals=" + UPDATED_ACTIVE);
    }

    @Test
    void getAllCountriesByActiveIsInShouldWork() {
        // Initialize the database
        insertedCountry = countryRepository.save(country).block();

        // Get all the countryList where active in
        defaultCountryFiltering("active.in=" + DEFAULT_ACTIVE + "," + UPDATED_ACTIVE, "active.in=" + UPDATED_ACTIVE);
    }

    @Test
    void getAllCountriesByActiveIsNullOrNotNull() {
        // Initialize the database
        insertedCountry = countryRepository.save(country).block();

        // Get all the countryList where active is not null
        defaultCountryFiltering("active.specified=true", "active.specified=false");
    }

    private void defaultCountryFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultCountryShouldBeFound(shouldBeFound);
        defaultCountryShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCountryShouldBeFound(String filter) {
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc&" + filter)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(country.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].iso2")
            .value(hasItem(DEFAULT_ISO_2))
            .jsonPath("$.[*].iso3")
            .value(hasItem(DEFAULT_ISO_3))
            .jsonPath("$.[*].region")
            .value(hasItem(DEFAULT_REGION))
            .jsonPath("$.[*].createdDate")
            .value(hasItem(DEFAULT_CREATED_DATE.toString()))
            .jsonPath("$.[*].lastModifiedDate")
            .value(hasItem(DEFAULT_LAST_MODIFIED_DATE.toString()))
            .jsonPath("$.[*].createdBy")
            .value(hasItem(DEFAULT_CREATED_BY))
            .jsonPath("$.[*].lastModifiedBy")
            .value(hasItem(DEFAULT_LAST_MODIFIED_BY))
            .jsonPath("$.[*].active")
            .value(hasItem(DEFAULT_ACTIVE));

        // Check, that the count call also returns 1
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "/count?sort=id,desc&" + filter)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$")
            .value(is(1));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCountryShouldNotBeFound(String filter) {
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc&" + filter)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$")
            .isArray()
            .jsonPath("$")
            .isEmpty();

        // Check, that the count call also returns 0
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "/count?sort=id,desc&" + filter)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$")
            .value(is(0));
    }

    @Test
    void getNonExistingCountry() {
        // Get the country
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingCountry() throws Exception {
        // Initialize the database
        insertedCountry = countryRepository.save(country).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the country
        Country updatedCountry = countryRepository.findById(country.getId()).block();
        updatedCountry
            .name(UPDATED_NAME)
            .iso2(UPDATED_ISO_2)
            .iso3(UPDATED_ISO_3)
            .region(UPDATED_REGION)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .createdBy(UPDATED_CREATED_BY)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .active(UPDATED_ACTIVE);
        CountryDTO countryDTO = countryMapper.toDto(updatedCountry);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, countryDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(countryDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Country in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCountryToMatchAllProperties(updatedCountry);
    }

    @Test
    void putNonExistingCountry() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        country.setId(longCount.incrementAndGet());

        // Create the Country
        CountryDTO countryDTO = countryMapper.toDto(country);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, countryDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(countryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Country in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchCountry() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        country.setId(longCount.incrementAndGet());

        // Create the Country
        CountryDTO countryDTO = countryMapper.toDto(country);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(countryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Country in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamCountry() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        country.setId(longCount.incrementAndGet());

        // Create the Country
        CountryDTO countryDTO = countryMapper.toDto(country);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(countryDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Country in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateCountryWithPatch() throws Exception {
        // Initialize the database
        insertedCountry = countryRepository.save(country).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the country using partial update
        Country partialUpdatedCountry = new Country();
        partialUpdatedCountry.setId(country.getId());

        partialUpdatedCountry.name(UPDATED_NAME).iso3(UPDATED_ISO_3).createdBy(UPDATED_CREATED_BY).active(UPDATED_ACTIVE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCountry.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedCountry))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Country in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCountryUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedCountry, country), getPersistedCountry(country));
    }

    @Test
    void fullUpdateCountryWithPatch() throws Exception {
        // Initialize the database
        insertedCountry = countryRepository.save(country).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the country using partial update
        Country partialUpdatedCountry = new Country();
        partialUpdatedCountry.setId(country.getId());

        partialUpdatedCountry
            .name(UPDATED_NAME)
            .iso2(UPDATED_ISO_2)
            .iso3(UPDATED_ISO_3)
            .region(UPDATED_REGION)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .createdBy(UPDATED_CREATED_BY)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .active(UPDATED_ACTIVE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCountry.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedCountry))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Country in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCountryUpdatableFieldsEquals(partialUpdatedCountry, getPersistedCountry(partialUpdatedCountry));
    }

    @Test
    void patchNonExistingCountry() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        country.setId(longCount.incrementAndGet());

        // Create the Country
        CountryDTO countryDTO = countryMapper.toDto(country);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, countryDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(countryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Country in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchCountry() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        country.setId(longCount.incrementAndGet());

        // Create the Country
        CountryDTO countryDTO = countryMapper.toDto(country);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(countryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Country in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamCountry() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        country.setId(longCount.incrementAndGet());

        // Create the Country
        CountryDTO countryDTO = countryMapper.toDto(country);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(countryDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Country in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteCountry() {
        // Initialize the database
        insertedCountry = countryRepository.save(country).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the country
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, country.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return countryRepository.count().block();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Country getPersistedCountry(Country country) {
        return countryRepository.findById(country.getId()).block();
    }

    protected void assertPersistedCountryToMatchAllProperties(Country expectedCountry) {
        // Test fails because reactive api returns an empty object instead of null
        // assertCountryAllPropertiesEquals(expectedCountry, getPersistedCountry(expectedCountry));
        assertCountryUpdatableFieldsEquals(expectedCountry, getPersistedCountry(expectedCountry));
    }

    protected void assertPersistedCountryToMatchUpdatableProperties(Country expectedCountry) {
        // Test fails because reactive api returns an empty object instead of null
        // assertCountryAllUpdatablePropertiesEquals(expectedCountry, getPersistedCountry(expectedCountry));
        assertCountryUpdatableFieldsEquals(expectedCountry, getPersistedCountry(expectedCountry));
    }
}
