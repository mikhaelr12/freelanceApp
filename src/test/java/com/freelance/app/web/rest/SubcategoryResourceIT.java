package com.freelance.app.web.rest;

import static com.freelance.app.domain.SubcategoryAsserts.*;
import static com.freelance.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.freelance.app.IntegrationTest;
import com.freelance.app.domain.Category;
import com.freelance.app.domain.Subcategory;
import com.freelance.app.repository.CategoryRepository;
import com.freelance.app.repository.EntityManager;
import com.freelance.app.repository.SubcategoryRepository;
import com.freelance.app.service.SubcategoryService;
import com.freelance.app.service.dto.SubcategoryDTO;
import com.freelance.app.service.mapper.SubcategoryMapper;
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
 * Integration tests for the {@link SubcategoryResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class SubcategoryResourceIT {

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

    private static final String ENTITY_API_URL = "/api/subcategories";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SubcategoryRepository subcategoryRepository;

    @Mock
    private SubcategoryRepository subcategoryRepositoryMock;

    @Autowired
    private SubcategoryMapper subcategoryMapper;

    @Mock
    private SubcategoryService subcategoryServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Subcategory subcategory;

    private Subcategory insertedSubcategory;

    @Autowired
    private CategoryRepository categoryRepository;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Subcategory createEntity() {
        return new Subcategory()
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
    public static Subcategory createUpdatedEntity() {
        return new Subcategory()
            .name(UPDATED_NAME)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .createdBy(UPDATED_CREATED_BY)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .active(UPDATED_ACTIVE);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Subcategory.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        subcategory = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedSubcategory != null) {
            subcategoryRepository.delete(insertedSubcategory).block();
            insertedSubcategory = null;
        }
        deleteEntities(em);
    }

    @Test
    void createSubcategory() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Subcategory
        SubcategoryDTO subcategoryDTO = subcategoryMapper.toDto(subcategory);
        var returnedSubcategoryDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(subcategoryDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(SubcategoryDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Subcategory in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedSubcategory = subcategoryMapper.toEntity(returnedSubcategoryDTO);
        assertSubcategoryUpdatableFieldsEquals(returnedSubcategory, getPersistedSubcategory(returnedSubcategory));

        insertedSubcategory = returnedSubcategory;
    }

    @Test
    void createSubcategoryWithExistingId() throws Exception {
        // Create the Subcategory with an existing ID
        subcategory.setId(1L);
        SubcategoryDTO subcategoryDTO = subcategoryMapper.toDto(subcategory);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(subcategoryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Subcategory in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        subcategory.setName(null);

        // Create the Subcategory, which fails.
        SubcategoryDTO subcategoryDTO = subcategoryMapper.toDto(subcategory);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(subcategoryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkCreatedDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        subcategory.setCreatedDate(null);

        // Create the Subcategory, which fails.
        SubcategoryDTO subcategoryDTO = subcategoryMapper.toDto(subcategory);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(subcategoryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkActiveIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        subcategory.setActive(null);

        // Create the Subcategory, which fails.
        SubcategoryDTO subcategoryDTO = subcategoryMapper.toDto(subcategory);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(subcategoryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllSubcategories() {
        // Initialize the database
        insertedSubcategory = subcategoryRepository.save(subcategory).block();

        // Get all the subcategoryList
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
            .value(hasItem(subcategory.getId().intValue()))
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
    void getAllSubcategoriesWithEagerRelationshipsIsEnabled() {
        when(subcategoryServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(subcategoryServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSubcategoriesWithEagerRelationshipsIsNotEnabled() {
        when(subcategoryServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(subcategoryRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getSubcategory() {
        // Initialize the database
        insertedSubcategory = subcategoryRepository.save(subcategory).block();

        // Get the subcategory
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, subcategory.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(subcategory.getId().intValue()))
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
    void getSubcategoriesByIdFiltering() {
        // Initialize the database
        insertedSubcategory = subcategoryRepository.save(subcategory).block();

        Long id = subcategory.getId();

        defaultSubcategoryFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultSubcategoryFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultSubcategoryFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllSubcategoriesByNameIsEqualToSomething() {
        // Initialize the database
        insertedSubcategory = subcategoryRepository.save(subcategory).block();

        // Get all the subcategoryList where name equals to
        defaultSubcategoryFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    void getAllSubcategoriesByNameIsInShouldWork() {
        // Initialize the database
        insertedSubcategory = subcategoryRepository.save(subcategory).block();

        // Get all the subcategoryList where name in
        defaultSubcategoryFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    void getAllSubcategoriesByNameIsNullOrNotNull() {
        // Initialize the database
        insertedSubcategory = subcategoryRepository.save(subcategory).block();

        // Get all the subcategoryList where name is not null
        defaultSubcategoryFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    void getAllSubcategoriesByNameContainsSomething() {
        // Initialize the database
        insertedSubcategory = subcategoryRepository.save(subcategory).block();

        // Get all the subcategoryList where name contains
        defaultSubcategoryFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    void getAllSubcategoriesByNameNotContainsSomething() {
        // Initialize the database
        insertedSubcategory = subcategoryRepository.save(subcategory).block();

        // Get all the subcategoryList where name does not contain
        defaultSubcategoryFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    void getAllSubcategoriesByCreatedDateIsEqualToSomething() {
        // Initialize the database
        insertedSubcategory = subcategoryRepository.save(subcategory).block();

        // Get all the subcategoryList where createdDate equals to
        defaultSubcategoryFiltering("createdDate.equals=" + DEFAULT_CREATED_DATE, "createdDate.equals=" + UPDATED_CREATED_DATE);
    }

    @Test
    void getAllSubcategoriesByCreatedDateIsInShouldWork() {
        // Initialize the database
        insertedSubcategory = subcategoryRepository.save(subcategory).block();

        // Get all the subcategoryList where createdDate in
        defaultSubcategoryFiltering(
            "createdDate.in=" + DEFAULT_CREATED_DATE + "," + UPDATED_CREATED_DATE,
            "createdDate.in=" + UPDATED_CREATED_DATE
        );
    }

    @Test
    void getAllSubcategoriesByCreatedDateIsNullOrNotNull() {
        // Initialize the database
        insertedSubcategory = subcategoryRepository.save(subcategory).block();

        // Get all the subcategoryList where createdDate is not null
        defaultSubcategoryFiltering("createdDate.specified=true", "createdDate.specified=false");
    }

    @Test
    void getAllSubcategoriesByLastModifiedDateIsEqualToSomething() {
        // Initialize the database
        insertedSubcategory = subcategoryRepository.save(subcategory).block();

        // Get all the subcategoryList where lastModifiedDate equals to
        defaultSubcategoryFiltering(
            "lastModifiedDate.equals=" + DEFAULT_LAST_MODIFIED_DATE,
            "lastModifiedDate.equals=" + UPDATED_LAST_MODIFIED_DATE
        );
    }

    @Test
    void getAllSubcategoriesByLastModifiedDateIsInShouldWork() {
        // Initialize the database
        insertedSubcategory = subcategoryRepository.save(subcategory).block();

        // Get all the subcategoryList where lastModifiedDate in
        defaultSubcategoryFiltering(
            "lastModifiedDate.in=" + DEFAULT_LAST_MODIFIED_DATE + "," + UPDATED_LAST_MODIFIED_DATE,
            "lastModifiedDate.in=" + UPDATED_LAST_MODIFIED_DATE
        );
    }

    @Test
    void getAllSubcategoriesByLastModifiedDateIsNullOrNotNull() {
        // Initialize the database
        insertedSubcategory = subcategoryRepository.save(subcategory).block();

        // Get all the subcategoryList where lastModifiedDate is not null
        defaultSubcategoryFiltering("lastModifiedDate.specified=true", "lastModifiedDate.specified=false");
    }

    @Test
    void getAllSubcategoriesByCreatedByIsEqualToSomething() {
        // Initialize the database
        insertedSubcategory = subcategoryRepository.save(subcategory).block();

        // Get all the subcategoryList where createdBy equals to
        defaultSubcategoryFiltering("createdBy.equals=" + DEFAULT_CREATED_BY, "createdBy.equals=" + UPDATED_CREATED_BY);
    }

    @Test
    void getAllSubcategoriesByCreatedByIsInShouldWork() {
        // Initialize the database
        insertedSubcategory = subcategoryRepository.save(subcategory).block();

        // Get all the subcategoryList where createdBy in
        defaultSubcategoryFiltering("createdBy.in=" + DEFAULT_CREATED_BY + "," + UPDATED_CREATED_BY, "createdBy.in=" + UPDATED_CREATED_BY);
    }

    @Test
    void getAllSubcategoriesByCreatedByIsNullOrNotNull() {
        // Initialize the database
        insertedSubcategory = subcategoryRepository.save(subcategory).block();

        // Get all the subcategoryList where createdBy is not null
        defaultSubcategoryFiltering("createdBy.specified=true", "createdBy.specified=false");
    }

    @Test
    void getAllSubcategoriesByCreatedByContainsSomething() {
        // Initialize the database
        insertedSubcategory = subcategoryRepository.save(subcategory).block();

        // Get all the subcategoryList where createdBy contains
        defaultSubcategoryFiltering("createdBy.contains=" + DEFAULT_CREATED_BY, "createdBy.contains=" + UPDATED_CREATED_BY);
    }

    @Test
    void getAllSubcategoriesByCreatedByNotContainsSomething() {
        // Initialize the database
        insertedSubcategory = subcategoryRepository.save(subcategory).block();

        // Get all the subcategoryList where createdBy does not contain
        defaultSubcategoryFiltering("createdBy.doesNotContain=" + UPDATED_CREATED_BY, "createdBy.doesNotContain=" + DEFAULT_CREATED_BY);
    }

    @Test
    void getAllSubcategoriesByLastModifiedByIsEqualToSomething() {
        // Initialize the database
        insertedSubcategory = subcategoryRepository.save(subcategory).block();

        // Get all the subcategoryList where lastModifiedBy equals to
        defaultSubcategoryFiltering(
            "lastModifiedBy.equals=" + DEFAULT_LAST_MODIFIED_BY,
            "lastModifiedBy.equals=" + UPDATED_LAST_MODIFIED_BY
        );
    }

    @Test
    void getAllSubcategoriesByLastModifiedByIsInShouldWork() {
        // Initialize the database
        insertedSubcategory = subcategoryRepository.save(subcategory).block();

        // Get all the subcategoryList where lastModifiedBy in
        defaultSubcategoryFiltering(
            "lastModifiedBy.in=" + DEFAULT_LAST_MODIFIED_BY + "," + UPDATED_LAST_MODIFIED_BY,
            "lastModifiedBy.in=" + UPDATED_LAST_MODIFIED_BY
        );
    }

    @Test
    void getAllSubcategoriesByLastModifiedByIsNullOrNotNull() {
        // Initialize the database
        insertedSubcategory = subcategoryRepository.save(subcategory).block();

        // Get all the subcategoryList where lastModifiedBy is not null
        defaultSubcategoryFiltering("lastModifiedBy.specified=true", "lastModifiedBy.specified=false");
    }

    @Test
    void getAllSubcategoriesByLastModifiedByContainsSomething() {
        // Initialize the database
        insertedSubcategory = subcategoryRepository.save(subcategory).block();

        // Get all the subcategoryList where lastModifiedBy contains
        defaultSubcategoryFiltering(
            "lastModifiedBy.contains=" + DEFAULT_LAST_MODIFIED_BY,
            "lastModifiedBy.contains=" + UPDATED_LAST_MODIFIED_BY
        );
    }

    @Test
    void getAllSubcategoriesByLastModifiedByNotContainsSomething() {
        // Initialize the database
        insertedSubcategory = subcategoryRepository.save(subcategory).block();

        // Get all the subcategoryList where lastModifiedBy does not contain
        defaultSubcategoryFiltering(
            "lastModifiedBy.doesNotContain=" + UPDATED_LAST_MODIFIED_BY,
            "lastModifiedBy.doesNotContain=" + DEFAULT_LAST_MODIFIED_BY
        );
    }

    @Test
    void getAllSubcategoriesByActiveIsEqualToSomething() {
        // Initialize the database
        insertedSubcategory = subcategoryRepository.save(subcategory).block();

        // Get all the subcategoryList where active equals to
        defaultSubcategoryFiltering("active.equals=" + DEFAULT_ACTIVE, "active.equals=" + UPDATED_ACTIVE);
    }

    @Test
    void getAllSubcategoriesByActiveIsInShouldWork() {
        // Initialize the database
        insertedSubcategory = subcategoryRepository.save(subcategory).block();

        // Get all the subcategoryList where active in
        defaultSubcategoryFiltering("active.in=" + DEFAULT_ACTIVE + "," + UPDATED_ACTIVE, "active.in=" + UPDATED_ACTIVE);
    }

    @Test
    void getAllSubcategoriesByActiveIsNullOrNotNull() {
        // Initialize the database
        insertedSubcategory = subcategoryRepository.save(subcategory).block();

        // Get all the subcategoryList where active is not null
        defaultSubcategoryFiltering("active.specified=true", "active.specified=false");
    }

    @Test
    void getAllSubcategoriesByCategoryIsEqualToSomething() {
        Category category = CategoryResourceIT.createEntity();
        categoryRepository.save(category).block();
        Long categoryId = category.getId();
        subcategory.setCategoryId(categoryId);
        insertedSubcategory = subcategoryRepository.save(subcategory).block();
        // Get all the subcategoryList where category equals to categoryId
        defaultSubcategoryShouldBeFound("categoryId.equals=" + categoryId);

        // Get all the subcategoryList where category equals to (categoryId + 1)
        defaultSubcategoryShouldNotBeFound("categoryId.equals=" + (categoryId + 1));
    }

    private void defaultSubcategoryFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultSubcategoryShouldBeFound(shouldBeFound);
        defaultSubcategoryShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultSubcategoryShouldBeFound(String filter) {
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
            .value(hasItem(subcategory.getId().intValue()))
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
    private void defaultSubcategoryShouldNotBeFound(String filter) {
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
    void getNonExistingSubcategory() {
        // Get the subcategory
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingSubcategory() throws Exception {
        // Initialize the database
        insertedSubcategory = subcategoryRepository.save(subcategory).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the subcategory
        Subcategory updatedSubcategory = subcategoryRepository.findById(subcategory.getId()).block();
        updatedSubcategory
            .name(UPDATED_NAME)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .createdBy(UPDATED_CREATED_BY)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .active(UPDATED_ACTIVE);
        SubcategoryDTO subcategoryDTO = subcategoryMapper.toDto(updatedSubcategory);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, subcategoryDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(subcategoryDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Subcategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSubcategoryToMatchAllProperties(updatedSubcategory);
    }

    @Test
    void putNonExistingSubcategory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        subcategory.setId(longCount.incrementAndGet());

        // Create the Subcategory
        SubcategoryDTO subcategoryDTO = subcategoryMapper.toDto(subcategory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, subcategoryDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(subcategoryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Subcategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchSubcategory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        subcategory.setId(longCount.incrementAndGet());

        // Create the Subcategory
        SubcategoryDTO subcategoryDTO = subcategoryMapper.toDto(subcategory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(subcategoryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Subcategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamSubcategory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        subcategory.setId(longCount.incrementAndGet());

        // Create the Subcategory
        SubcategoryDTO subcategoryDTO = subcategoryMapper.toDto(subcategory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(subcategoryDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Subcategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateSubcategoryWithPatch() throws Exception {
        // Initialize the database
        insertedSubcategory = subcategoryRepository.save(subcategory).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the subcategory using partial update
        Subcategory partialUpdatedSubcategory = new Subcategory();
        partialUpdatedSubcategory.setId(subcategory.getId());

        partialUpdatedSubcategory.createdDate(UPDATED_CREATED_DATE).createdBy(UPDATED_CREATED_BY).active(UPDATED_ACTIVE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedSubcategory.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedSubcategory))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Subcategory in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSubcategoryUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedSubcategory, subcategory),
            getPersistedSubcategory(subcategory)
        );
    }

    @Test
    void fullUpdateSubcategoryWithPatch() throws Exception {
        // Initialize the database
        insertedSubcategory = subcategoryRepository.save(subcategory).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the subcategory using partial update
        Subcategory partialUpdatedSubcategory = new Subcategory();
        partialUpdatedSubcategory.setId(subcategory.getId());

        partialUpdatedSubcategory
            .name(UPDATED_NAME)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .createdBy(UPDATED_CREATED_BY)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .active(UPDATED_ACTIVE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedSubcategory.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedSubcategory))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Subcategory in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSubcategoryUpdatableFieldsEquals(partialUpdatedSubcategory, getPersistedSubcategory(partialUpdatedSubcategory));
    }

    @Test
    void patchNonExistingSubcategory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        subcategory.setId(longCount.incrementAndGet());

        // Create the Subcategory
        SubcategoryDTO subcategoryDTO = subcategoryMapper.toDto(subcategory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, subcategoryDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(subcategoryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Subcategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchSubcategory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        subcategory.setId(longCount.incrementAndGet());

        // Create the Subcategory
        SubcategoryDTO subcategoryDTO = subcategoryMapper.toDto(subcategory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(subcategoryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Subcategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamSubcategory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        subcategory.setId(longCount.incrementAndGet());

        // Create the Subcategory
        SubcategoryDTO subcategoryDTO = subcategoryMapper.toDto(subcategory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(subcategoryDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Subcategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteSubcategory() {
        // Initialize the database
        insertedSubcategory = subcategoryRepository.save(subcategory).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the subcategory
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, subcategory.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return subcategoryRepository.count().block();
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

    protected Subcategory getPersistedSubcategory(Subcategory subcategory) {
        return subcategoryRepository.findById(subcategory.getId()).block();
    }

    protected void assertPersistedSubcategoryToMatchAllProperties(Subcategory expectedSubcategory) {
        // Test fails because reactive api returns an empty object instead of null
        // assertSubcategoryAllPropertiesEquals(expectedSubcategory, getPersistedSubcategory(expectedSubcategory));
        assertSubcategoryUpdatableFieldsEquals(expectedSubcategory, getPersistedSubcategory(expectedSubcategory));
    }

    protected void assertPersistedSubcategoryToMatchUpdatableProperties(Subcategory expectedSubcategory) {
        // Test fails because reactive api returns an empty object instead of null
        // assertSubcategoryAllUpdatablePropertiesEquals(expectedSubcategory, getPersistedSubcategory(expectedSubcategory));
        assertSubcategoryUpdatableFieldsEquals(expectedSubcategory, getPersistedSubcategory(expectedSubcategory));
    }
}
