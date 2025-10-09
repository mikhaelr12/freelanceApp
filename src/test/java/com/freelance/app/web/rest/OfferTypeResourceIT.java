package com.freelance.app.web.rest;

import static com.freelance.app.domain.OfferTypeAsserts.*;
import static com.freelance.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.freelance.app.IntegrationTest;
import com.freelance.app.domain.OfferType;
import com.freelance.app.domain.Subcategory;
import com.freelance.app.repository.EntityManager;
import com.freelance.app.repository.OfferTypeRepository;
import com.freelance.app.repository.SubcategoryRepository;
import com.freelance.app.service.OfferTypeService;
import com.freelance.app.service.dto.OfferTypeDTO;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

/**
 * Integration tests for the {@link OfferTypeResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class OfferTypeResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

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

    private static final String ENTITY_API_URL = "/api/offer-types";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private OfferTypeRepository offerTypeRepository;

    @Mock
    private OfferTypeRepository offerTypeRepositoryMock;

    @Autowired
    private OfferTypeMapper offerTypeMapper;

    @Mock
    private OfferTypeService offerTypeServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private OfferType offerType;

    private OfferType insertedOfferType;

    @Autowired
    private SubcategoryRepository subcategoryRepository;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OfferType createEntity() {
        return new OfferType()
            .name(DEFAULT_NAME)
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
    public static OfferType createUpdatedEntity() {
        return new OfferType()
            .name(UPDATED_NAME)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .createdBy(UPDATED_CREATED_BY)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .active(UPDATED_ACTIVE);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(OfferType.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        offerType = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedOfferType != null) {
            offerTypeRepository.delete(insertedOfferType).block();
            insertedOfferType = null;
        }
        deleteEntities(em);
    }

    @Test
    void createOfferType() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the OfferType
        OfferTypeDTO offerTypeDTO = offerTypeMapper.toDto(offerType);
        var returnedOfferTypeDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(offerTypeDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(OfferTypeDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the OfferType in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedOfferType = offerTypeMapper.toEntity(returnedOfferTypeDTO);
        assertOfferTypeUpdatableFieldsEquals(returnedOfferType, getPersistedOfferType(returnedOfferType));

        insertedOfferType = returnedOfferType;
    }

    @Test
    void createOfferTypeWithExistingId() throws Exception {
        // Create the OfferType with an existing ID
        offerType.setId(1L);
        OfferTypeDTO offerTypeDTO = offerTypeMapper.toDto(offerType);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(offerTypeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the OfferType in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        offerType.setName(null);

        // Create the OfferType, which fails.
        OfferTypeDTO offerTypeDTO = offerTypeMapper.toDto(offerType);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(offerTypeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkCreatedDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        offerType.setCreatedDate(null);

        // Create the OfferType, which fails.
        OfferTypeDTO offerTypeDTO = offerTypeMapper.toDto(offerType);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(offerTypeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkActiveIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        offerType.setActive(null);

        // Create the OfferType, which fails.
        OfferTypeDTO offerTypeDTO = offerTypeMapper.toDto(offerType);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(offerTypeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllOfferTypes() {
        // Initialize the database
        insertedOfferType = offerTypeRepository.save(offerType).block();

        // Get all the offerTypeList
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
            .value(hasItem(offerType.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
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

    @SuppressWarnings({ "unchecked" })
    void getAllOfferTypesWithEagerRelationshipsIsEnabled() {
        when(offerTypeServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(offerTypeServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllOfferTypesWithEagerRelationshipsIsNotEnabled() {
        when(offerTypeServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(offerTypeRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getOfferType() {
        // Initialize the database
        insertedOfferType = offerTypeRepository.save(offerType).block();

        // Get the offerType
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, offerType.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(offerType.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
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
    void getOfferTypesByIdFiltering() {
        // Initialize the database
        insertedOfferType = offerTypeRepository.save(offerType).block();

        Long id = offerType.getId();

        defaultOfferTypeFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultOfferTypeFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultOfferTypeFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllOfferTypesByNameIsEqualToSomething() {
        // Initialize the database
        insertedOfferType = offerTypeRepository.save(offerType).block();

        // Get all the offerTypeList where name equals to
        defaultOfferTypeFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    void getAllOfferTypesByNameIsInShouldWork() {
        // Initialize the database
        insertedOfferType = offerTypeRepository.save(offerType).block();

        // Get all the offerTypeList where name in
        defaultOfferTypeFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    void getAllOfferTypesByNameIsNullOrNotNull() {
        // Initialize the database
        insertedOfferType = offerTypeRepository.save(offerType).block();

        // Get all the offerTypeList where name is not null
        defaultOfferTypeFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    void getAllOfferTypesByNameContainsSomething() {
        // Initialize the database
        insertedOfferType = offerTypeRepository.save(offerType).block();

        // Get all the offerTypeList where name contains
        defaultOfferTypeFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    void getAllOfferTypesByNameNotContainsSomething() {
        // Initialize the database
        insertedOfferType = offerTypeRepository.save(offerType).block();

        // Get all the offerTypeList where name does not contain
        defaultOfferTypeFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    void getAllOfferTypesByCreatedDateIsEqualToSomething() {
        // Initialize the database
        insertedOfferType = offerTypeRepository.save(offerType).block();

        // Get all the offerTypeList where createdDate equals to
        defaultOfferTypeFiltering("createdDate.equals=" + DEFAULT_CREATED_DATE, "createdDate.equals=" + UPDATED_CREATED_DATE);
    }

    @Test
    void getAllOfferTypesByCreatedDateIsInShouldWork() {
        // Initialize the database
        insertedOfferType = offerTypeRepository.save(offerType).block();

        // Get all the offerTypeList where createdDate in
        defaultOfferTypeFiltering(
            "createdDate.in=" + DEFAULT_CREATED_DATE + "," + UPDATED_CREATED_DATE,
            "createdDate.in=" + UPDATED_CREATED_DATE
        );
    }

    @Test
    void getAllOfferTypesByCreatedDateIsNullOrNotNull() {
        // Initialize the database
        insertedOfferType = offerTypeRepository.save(offerType).block();

        // Get all the offerTypeList where createdDate is not null
        defaultOfferTypeFiltering("createdDate.specified=true", "createdDate.specified=false");
    }

    @Test
    void getAllOfferTypesByLastModifiedDateIsEqualToSomething() {
        // Initialize the database
        insertedOfferType = offerTypeRepository.save(offerType).block();

        // Get all the offerTypeList where lastModifiedDate equals to
        defaultOfferTypeFiltering(
            "lastModifiedDate.equals=" + DEFAULT_LAST_MODIFIED_DATE,
            "lastModifiedDate.equals=" + UPDATED_LAST_MODIFIED_DATE
        );
    }

    @Test
    void getAllOfferTypesByLastModifiedDateIsInShouldWork() {
        // Initialize the database
        insertedOfferType = offerTypeRepository.save(offerType).block();

        // Get all the offerTypeList where lastModifiedDate in
        defaultOfferTypeFiltering(
            "lastModifiedDate.in=" + DEFAULT_LAST_MODIFIED_DATE + "," + UPDATED_LAST_MODIFIED_DATE,
            "lastModifiedDate.in=" + UPDATED_LAST_MODIFIED_DATE
        );
    }

    @Test
    void getAllOfferTypesByLastModifiedDateIsNullOrNotNull() {
        // Initialize the database
        insertedOfferType = offerTypeRepository.save(offerType).block();

        // Get all the offerTypeList where lastModifiedDate is not null
        defaultOfferTypeFiltering("lastModifiedDate.specified=true", "lastModifiedDate.specified=false");
    }

    @Test
    void getAllOfferTypesByCreatedByIsEqualToSomething() {
        // Initialize the database
        insertedOfferType = offerTypeRepository.save(offerType).block();

        // Get all the offerTypeList where createdBy equals to
        defaultOfferTypeFiltering("createdBy.equals=" + DEFAULT_CREATED_BY, "createdBy.equals=" + UPDATED_CREATED_BY);
    }

    @Test
    void getAllOfferTypesByCreatedByIsInShouldWork() {
        // Initialize the database
        insertedOfferType = offerTypeRepository.save(offerType).block();

        // Get all the offerTypeList where createdBy in
        defaultOfferTypeFiltering("createdBy.in=" + DEFAULT_CREATED_BY + "," + UPDATED_CREATED_BY, "createdBy.in=" + UPDATED_CREATED_BY);
    }

    @Test
    void getAllOfferTypesByCreatedByIsNullOrNotNull() {
        // Initialize the database
        insertedOfferType = offerTypeRepository.save(offerType).block();

        // Get all the offerTypeList where createdBy is not null
        defaultOfferTypeFiltering("createdBy.specified=true", "createdBy.specified=false");
    }

    @Test
    void getAllOfferTypesByCreatedByContainsSomething() {
        // Initialize the database
        insertedOfferType = offerTypeRepository.save(offerType).block();

        // Get all the offerTypeList where createdBy contains
        defaultOfferTypeFiltering("createdBy.contains=" + DEFAULT_CREATED_BY, "createdBy.contains=" + UPDATED_CREATED_BY);
    }

    @Test
    void getAllOfferTypesByCreatedByNotContainsSomething() {
        // Initialize the database
        insertedOfferType = offerTypeRepository.save(offerType).block();

        // Get all the offerTypeList where createdBy does not contain
        defaultOfferTypeFiltering("createdBy.doesNotContain=" + UPDATED_CREATED_BY, "createdBy.doesNotContain=" + DEFAULT_CREATED_BY);
    }

    @Test
    void getAllOfferTypesByLastModifiedByIsEqualToSomething() {
        // Initialize the database
        insertedOfferType = offerTypeRepository.save(offerType).block();

        // Get all the offerTypeList where lastModifiedBy equals to
        defaultOfferTypeFiltering("lastModifiedBy.equals=" + DEFAULT_LAST_MODIFIED_BY, "lastModifiedBy.equals=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    void getAllOfferTypesByLastModifiedByIsInShouldWork() {
        // Initialize the database
        insertedOfferType = offerTypeRepository.save(offerType).block();

        // Get all the offerTypeList where lastModifiedBy in
        defaultOfferTypeFiltering(
            "lastModifiedBy.in=" + DEFAULT_LAST_MODIFIED_BY + "," + UPDATED_LAST_MODIFIED_BY,
            "lastModifiedBy.in=" + UPDATED_LAST_MODIFIED_BY
        );
    }

    @Test
    void getAllOfferTypesByLastModifiedByIsNullOrNotNull() {
        // Initialize the database
        insertedOfferType = offerTypeRepository.save(offerType).block();

        // Get all the offerTypeList where lastModifiedBy is not null
        defaultOfferTypeFiltering("lastModifiedBy.specified=true", "lastModifiedBy.specified=false");
    }

    @Test
    void getAllOfferTypesByLastModifiedByContainsSomething() {
        // Initialize the database
        insertedOfferType = offerTypeRepository.save(offerType).block();

        // Get all the offerTypeList where lastModifiedBy contains
        defaultOfferTypeFiltering(
            "lastModifiedBy.contains=" + DEFAULT_LAST_MODIFIED_BY,
            "lastModifiedBy.contains=" + UPDATED_LAST_MODIFIED_BY
        );
    }

    @Test
    void getAllOfferTypesByLastModifiedByNotContainsSomething() {
        // Initialize the database
        insertedOfferType = offerTypeRepository.save(offerType).block();

        // Get all the offerTypeList where lastModifiedBy does not contain
        defaultOfferTypeFiltering(
            "lastModifiedBy.doesNotContain=" + UPDATED_LAST_MODIFIED_BY,
            "lastModifiedBy.doesNotContain=" + DEFAULT_LAST_MODIFIED_BY
        );
    }

    @Test
    void getAllOfferTypesByActiveIsEqualToSomething() {
        // Initialize the database
        insertedOfferType = offerTypeRepository.save(offerType).block();

        // Get all the offerTypeList where active equals to
        defaultOfferTypeFiltering("active.equals=" + DEFAULT_ACTIVE, "active.equals=" + UPDATED_ACTIVE);
    }

    @Test
    void getAllOfferTypesByActiveIsInShouldWork() {
        // Initialize the database
        insertedOfferType = offerTypeRepository.save(offerType).block();

        // Get all the offerTypeList where active in
        defaultOfferTypeFiltering("active.in=" + DEFAULT_ACTIVE + "," + UPDATED_ACTIVE, "active.in=" + UPDATED_ACTIVE);
    }

    @Test
    void getAllOfferTypesByActiveIsNullOrNotNull() {
        // Initialize the database
        insertedOfferType = offerTypeRepository.save(offerType).block();

        // Get all the offerTypeList where active is not null
        defaultOfferTypeFiltering("active.specified=true", "active.specified=false");
    }

    @Test
    void getAllOfferTypesBySubcategoryIsEqualToSomething() {
        Subcategory subcategory = SubcategoryResourceIT.createEntity();
        subcategoryRepository.save(subcategory).block();
        Long subcategoryId = subcategory.getId();
        offerType.setSubcategoryId(subcategoryId);
        insertedOfferType = offerTypeRepository.save(offerType).block();
        // Get all the offerTypeList where subcategory equals to subcategoryId
        defaultOfferTypeShouldBeFound("subcategoryId.equals=" + subcategoryId);

        // Get all the offerTypeList where subcategory equals to (subcategoryId + 1)
        defaultOfferTypeShouldNotBeFound("subcategoryId.equals=" + (subcategoryId + 1));
    }

    private void defaultOfferTypeFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultOfferTypeShouldBeFound(shouldBeFound);
        defaultOfferTypeShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultOfferTypeShouldBeFound(String filter) {
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
            .value(hasItem(offerType.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
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
    private void defaultOfferTypeShouldNotBeFound(String filter) {
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
    void getNonExistingOfferType() {
        // Get the offerType
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingOfferType() throws Exception {
        // Initialize the database
        insertedOfferType = offerTypeRepository.save(offerType).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the offerType
        OfferType updatedOfferType = offerTypeRepository.findById(offerType.getId()).block();
        updatedOfferType
            .name(UPDATED_NAME)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .createdBy(UPDATED_CREATED_BY)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .active(UPDATED_ACTIVE);
        OfferTypeDTO offerTypeDTO = offerTypeMapper.toDto(updatedOfferType);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, offerTypeDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(offerTypeDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the OfferType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedOfferTypeToMatchAllProperties(updatedOfferType);
    }

    @Test
    void putNonExistingOfferType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        offerType.setId(longCount.incrementAndGet());

        // Create the OfferType
        OfferTypeDTO offerTypeDTO = offerTypeMapper.toDto(offerType);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, offerTypeDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(offerTypeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the OfferType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchOfferType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        offerType.setId(longCount.incrementAndGet());

        // Create the OfferType
        OfferTypeDTO offerTypeDTO = offerTypeMapper.toDto(offerType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(offerTypeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the OfferType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamOfferType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        offerType.setId(longCount.incrementAndGet());

        // Create the OfferType
        OfferTypeDTO offerTypeDTO = offerTypeMapper.toDto(offerType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(offerTypeDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the OfferType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateOfferTypeWithPatch() throws Exception {
        // Initialize the database
        insertedOfferType = offerTypeRepository.save(offerType).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the offerType using partial update
        OfferType partialUpdatedOfferType = new OfferType();
        partialUpdatedOfferType.setId(offerType.getId());

        partialUpdatedOfferType
            .name(UPDATED_NAME)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .createdBy(UPDATED_CREATED_BY)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedOfferType.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedOfferType))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the OfferType in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertOfferTypeUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedOfferType, offerType),
            getPersistedOfferType(offerType)
        );
    }

    @Test
    void fullUpdateOfferTypeWithPatch() throws Exception {
        // Initialize the database
        insertedOfferType = offerTypeRepository.save(offerType).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the offerType using partial update
        OfferType partialUpdatedOfferType = new OfferType();
        partialUpdatedOfferType.setId(offerType.getId());

        partialUpdatedOfferType
            .name(UPDATED_NAME)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .createdBy(UPDATED_CREATED_BY)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .active(UPDATED_ACTIVE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedOfferType.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedOfferType))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the OfferType in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertOfferTypeUpdatableFieldsEquals(partialUpdatedOfferType, getPersistedOfferType(partialUpdatedOfferType));
    }

    @Test
    void patchNonExistingOfferType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        offerType.setId(longCount.incrementAndGet());

        // Create the OfferType
        OfferTypeDTO offerTypeDTO = offerTypeMapper.toDto(offerType);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, offerTypeDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(offerTypeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the OfferType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchOfferType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        offerType.setId(longCount.incrementAndGet());

        // Create the OfferType
        OfferTypeDTO offerTypeDTO = offerTypeMapper.toDto(offerType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(offerTypeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the OfferType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamOfferType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        offerType.setId(longCount.incrementAndGet());

        // Create the OfferType
        OfferTypeDTO offerTypeDTO = offerTypeMapper.toDto(offerType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(offerTypeDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the OfferType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteOfferType() {
        // Initialize the database
        insertedOfferType = offerTypeRepository.save(offerType).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the offerType
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, offerType.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return offerTypeRepository.count().block();
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

    protected OfferType getPersistedOfferType(OfferType offerType) {
        return offerTypeRepository.findById(offerType.getId()).block();
    }

    protected void assertPersistedOfferTypeToMatchAllProperties(OfferType expectedOfferType) {
        // Test fails because reactive api returns an empty object instead of null
        // assertOfferTypeAllPropertiesEquals(expectedOfferType, getPersistedOfferType(expectedOfferType));
        assertOfferTypeUpdatableFieldsEquals(expectedOfferType, getPersistedOfferType(expectedOfferType));
    }

    protected void assertPersistedOfferTypeToMatchUpdatableProperties(OfferType expectedOfferType) {
        // Test fails because reactive api returns an empty object instead of null
        // assertOfferTypeAllUpdatablePropertiesEquals(expectedOfferType, getPersistedOfferType(expectedOfferType));
        assertOfferTypeUpdatableFieldsEquals(expectedOfferType, getPersistedOfferType(expectedOfferType));
    }
}
