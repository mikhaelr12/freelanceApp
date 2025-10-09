package com.freelance.app.web.rest;

import static com.freelance.app.domain.SkillAsserts.*;
import static com.freelance.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.freelance.app.IntegrationTest;
import com.freelance.app.domain.Category;
import com.freelance.app.domain.Skill;
import com.freelance.app.repository.CategoryRepository;
import com.freelance.app.repository.EntityManager;
import com.freelance.app.repository.SkillRepository;
import com.freelance.app.service.SkillService;
import com.freelance.app.service.dto.SkillDTO;
import com.freelance.app.service.mapper.SkillMapper;
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
 * Integration tests for the {@link SkillResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class SkillResourceIT {

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

    private static final String ENTITY_API_URL = "/api/skills";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SkillRepository skillRepository;

    @Mock
    private SkillRepository skillRepositoryMock;

    @Autowired
    private SkillMapper skillMapper;

    @Mock
    private SkillService skillServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Skill skill;

    private Skill insertedSkill;

    @Autowired
    private CategoryRepository categoryRepository;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Skill createEntity() {
        return new Skill()
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
    public static Skill createUpdatedEntity() {
        return new Skill()
            .name(UPDATED_NAME)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .createdBy(UPDATED_CREATED_BY)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .active(UPDATED_ACTIVE);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Skill.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        skill = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedSkill != null) {
            skillRepository.delete(insertedSkill).block();
            insertedSkill = null;
        }
        deleteEntities(em);
    }

    @Test
    void createSkill() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Skill
        SkillDTO skillDTO = skillMapper.toDto(skill);
        var returnedSkillDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(skillDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(SkillDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Skill in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedSkill = skillMapper.toEntity(returnedSkillDTO);
        assertSkillUpdatableFieldsEquals(returnedSkill, getPersistedSkill(returnedSkill));

        insertedSkill = returnedSkill;
    }

    @Test
    void createSkillWithExistingId() throws Exception {
        // Create the Skill with an existing ID
        skill.setId(1L);
        SkillDTO skillDTO = skillMapper.toDto(skill);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(skillDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Skill in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        skill.setName(null);

        // Create the Skill, which fails.
        SkillDTO skillDTO = skillMapper.toDto(skill);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(skillDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkCreatedDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        skill.setCreatedDate(null);

        // Create the Skill, which fails.
        SkillDTO skillDTO = skillMapper.toDto(skill);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(skillDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkActiveIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        skill.setActive(null);

        // Create the Skill, which fails.
        SkillDTO skillDTO = skillMapper.toDto(skill);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(skillDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllSkills() {
        // Initialize the database
        insertedSkill = skillRepository.save(skill).block();

        // Get all the skillList
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
            .value(hasItem(skill.getId().intValue()))
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
    void getAllSkillsWithEagerRelationshipsIsEnabled() {
        when(skillServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(skillServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSkillsWithEagerRelationshipsIsNotEnabled() {
        when(skillServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(skillRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getSkill() {
        // Initialize the database
        insertedSkill = skillRepository.save(skill).block();

        // Get the skill
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, skill.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(skill.getId().intValue()))
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
    void getSkillsByIdFiltering() {
        // Initialize the database
        insertedSkill = skillRepository.save(skill).block();

        Long id = skill.getId();

        defaultSkillFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultSkillFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultSkillFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllSkillsByNameIsEqualToSomething() {
        // Initialize the database
        insertedSkill = skillRepository.save(skill).block();

        // Get all the skillList where name equals to
        defaultSkillFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    void getAllSkillsByNameIsInShouldWork() {
        // Initialize the database
        insertedSkill = skillRepository.save(skill).block();

        // Get all the skillList where name in
        defaultSkillFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    void getAllSkillsByNameIsNullOrNotNull() {
        // Initialize the database
        insertedSkill = skillRepository.save(skill).block();

        // Get all the skillList where name is not null
        defaultSkillFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    void getAllSkillsByNameContainsSomething() {
        // Initialize the database
        insertedSkill = skillRepository.save(skill).block();

        // Get all the skillList where name contains
        defaultSkillFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    void getAllSkillsByNameNotContainsSomething() {
        // Initialize the database
        insertedSkill = skillRepository.save(skill).block();

        // Get all the skillList where name does not contain
        defaultSkillFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    void getAllSkillsByCreatedDateIsEqualToSomething() {
        // Initialize the database
        insertedSkill = skillRepository.save(skill).block();

        // Get all the skillList where createdDate equals to
        defaultSkillFiltering("createdDate.equals=" + DEFAULT_CREATED_DATE, "createdDate.equals=" + UPDATED_CREATED_DATE);
    }

    @Test
    void getAllSkillsByCreatedDateIsInShouldWork() {
        // Initialize the database
        insertedSkill = skillRepository.save(skill).block();

        // Get all the skillList where createdDate in
        defaultSkillFiltering(
            "createdDate.in=" + DEFAULT_CREATED_DATE + "," + UPDATED_CREATED_DATE,
            "createdDate.in=" + UPDATED_CREATED_DATE
        );
    }

    @Test
    void getAllSkillsByCreatedDateIsNullOrNotNull() {
        // Initialize the database
        insertedSkill = skillRepository.save(skill).block();

        // Get all the skillList where createdDate is not null
        defaultSkillFiltering("createdDate.specified=true", "createdDate.specified=false");
    }

    @Test
    void getAllSkillsByLastModifiedDateIsEqualToSomething() {
        // Initialize the database
        insertedSkill = skillRepository.save(skill).block();

        // Get all the skillList where lastModifiedDate equals to
        defaultSkillFiltering(
            "lastModifiedDate.equals=" + DEFAULT_LAST_MODIFIED_DATE,
            "lastModifiedDate.equals=" + UPDATED_LAST_MODIFIED_DATE
        );
    }

    @Test
    void getAllSkillsByLastModifiedDateIsInShouldWork() {
        // Initialize the database
        insertedSkill = skillRepository.save(skill).block();

        // Get all the skillList where lastModifiedDate in
        defaultSkillFiltering(
            "lastModifiedDate.in=" + DEFAULT_LAST_MODIFIED_DATE + "," + UPDATED_LAST_MODIFIED_DATE,
            "lastModifiedDate.in=" + UPDATED_LAST_MODIFIED_DATE
        );
    }

    @Test
    void getAllSkillsByLastModifiedDateIsNullOrNotNull() {
        // Initialize the database
        insertedSkill = skillRepository.save(skill).block();

        // Get all the skillList where lastModifiedDate is not null
        defaultSkillFiltering("lastModifiedDate.specified=true", "lastModifiedDate.specified=false");
    }

    @Test
    void getAllSkillsByCreatedByIsEqualToSomething() {
        // Initialize the database
        insertedSkill = skillRepository.save(skill).block();

        // Get all the skillList where createdBy equals to
        defaultSkillFiltering("createdBy.equals=" + DEFAULT_CREATED_BY, "createdBy.equals=" + UPDATED_CREATED_BY);
    }

    @Test
    void getAllSkillsByCreatedByIsInShouldWork() {
        // Initialize the database
        insertedSkill = skillRepository.save(skill).block();

        // Get all the skillList where createdBy in
        defaultSkillFiltering("createdBy.in=" + DEFAULT_CREATED_BY + "," + UPDATED_CREATED_BY, "createdBy.in=" + UPDATED_CREATED_BY);
    }

    @Test
    void getAllSkillsByCreatedByIsNullOrNotNull() {
        // Initialize the database
        insertedSkill = skillRepository.save(skill).block();

        // Get all the skillList where createdBy is not null
        defaultSkillFiltering("createdBy.specified=true", "createdBy.specified=false");
    }

    @Test
    void getAllSkillsByCreatedByContainsSomething() {
        // Initialize the database
        insertedSkill = skillRepository.save(skill).block();

        // Get all the skillList where createdBy contains
        defaultSkillFiltering("createdBy.contains=" + DEFAULT_CREATED_BY, "createdBy.contains=" + UPDATED_CREATED_BY);
    }

    @Test
    void getAllSkillsByCreatedByNotContainsSomething() {
        // Initialize the database
        insertedSkill = skillRepository.save(skill).block();

        // Get all the skillList where createdBy does not contain
        defaultSkillFiltering("createdBy.doesNotContain=" + UPDATED_CREATED_BY, "createdBy.doesNotContain=" + DEFAULT_CREATED_BY);
    }

    @Test
    void getAllSkillsByLastModifiedByIsEqualToSomething() {
        // Initialize the database
        insertedSkill = skillRepository.save(skill).block();

        // Get all the skillList where lastModifiedBy equals to
        defaultSkillFiltering("lastModifiedBy.equals=" + DEFAULT_LAST_MODIFIED_BY, "lastModifiedBy.equals=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    void getAllSkillsByLastModifiedByIsInShouldWork() {
        // Initialize the database
        insertedSkill = skillRepository.save(skill).block();

        // Get all the skillList where lastModifiedBy in
        defaultSkillFiltering(
            "lastModifiedBy.in=" + DEFAULT_LAST_MODIFIED_BY + "," + UPDATED_LAST_MODIFIED_BY,
            "lastModifiedBy.in=" + UPDATED_LAST_MODIFIED_BY
        );
    }

    @Test
    void getAllSkillsByLastModifiedByIsNullOrNotNull() {
        // Initialize the database
        insertedSkill = skillRepository.save(skill).block();

        // Get all the skillList where lastModifiedBy is not null
        defaultSkillFiltering("lastModifiedBy.specified=true", "lastModifiedBy.specified=false");
    }

    @Test
    void getAllSkillsByLastModifiedByContainsSomething() {
        // Initialize the database
        insertedSkill = skillRepository.save(skill).block();

        // Get all the skillList where lastModifiedBy contains
        defaultSkillFiltering("lastModifiedBy.contains=" + DEFAULT_LAST_MODIFIED_BY, "lastModifiedBy.contains=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    void getAllSkillsByLastModifiedByNotContainsSomething() {
        // Initialize the database
        insertedSkill = skillRepository.save(skill).block();

        // Get all the skillList where lastModifiedBy does not contain
        defaultSkillFiltering(
            "lastModifiedBy.doesNotContain=" + UPDATED_LAST_MODIFIED_BY,
            "lastModifiedBy.doesNotContain=" + DEFAULT_LAST_MODIFIED_BY
        );
    }

    @Test
    void getAllSkillsByActiveIsEqualToSomething() {
        // Initialize the database
        insertedSkill = skillRepository.save(skill).block();

        // Get all the skillList where active equals to
        defaultSkillFiltering("active.equals=" + DEFAULT_ACTIVE, "active.equals=" + UPDATED_ACTIVE);
    }

    @Test
    void getAllSkillsByActiveIsInShouldWork() {
        // Initialize the database
        insertedSkill = skillRepository.save(skill).block();

        // Get all the skillList where active in
        defaultSkillFiltering("active.in=" + DEFAULT_ACTIVE + "," + UPDATED_ACTIVE, "active.in=" + UPDATED_ACTIVE);
    }

    @Test
    void getAllSkillsByActiveIsNullOrNotNull() {
        // Initialize the database
        insertedSkill = skillRepository.save(skill).block();

        // Get all the skillList where active is not null
        defaultSkillFiltering("active.specified=true", "active.specified=false");
    }

    @Test
    void getAllSkillsByCategoryIsEqualToSomething() {
        Category category = CategoryResourceIT.createEntity();
        categoryRepository.save(category).block();
        Long categoryId = category.getId();
        skill.setCategoryId(categoryId);
        insertedSkill = skillRepository.save(skill).block();
        // Get all the skillList where category equals to categoryId
        defaultSkillShouldBeFound("categoryId.equals=" + categoryId);

        // Get all the skillList where category equals to (categoryId + 1)
        defaultSkillShouldNotBeFound("categoryId.equals=" + (categoryId + 1));
    }

    private void defaultSkillFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultSkillShouldBeFound(shouldBeFound);
        defaultSkillShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultSkillShouldBeFound(String filter) {
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
            .value(hasItem(skill.getId().intValue()))
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
    private void defaultSkillShouldNotBeFound(String filter) {
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
    void getNonExistingSkill() {
        // Get the skill
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingSkill() throws Exception {
        // Initialize the database
        insertedSkill = skillRepository.save(skill).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the skill
        Skill updatedSkill = skillRepository.findById(skill.getId()).block();
        updatedSkill
            .name(UPDATED_NAME)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .createdBy(UPDATED_CREATED_BY)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .active(UPDATED_ACTIVE);
        SkillDTO skillDTO = skillMapper.toDto(updatedSkill);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, skillDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(skillDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Skill in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSkillToMatchAllProperties(updatedSkill);
    }

    @Test
    void putNonExistingSkill() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        skill.setId(longCount.incrementAndGet());

        // Create the Skill
        SkillDTO skillDTO = skillMapper.toDto(skill);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, skillDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(skillDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Skill in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchSkill() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        skill.setId(longCount.incrementAndGet());

        // Create the Skill
        SkillDTO skillDTO = skillMapper.toDto(skill);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(skillDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Skill in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamSkill() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        skill.setId(longCount.incrementAndGet());

        // Create the Skill
        SkillDTO skillDTO = skillMapper.toDto(skill);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(skillDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Skill in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateSkillWithPatch() throws Exception {
        // Initialize the database
        insertedSkill = skillRepository.save(skill).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the skill using partial update
        Skill partialUpdatedSkill = new Skill();
        partialUpdatedSkill.setId(skill.getId());

        partialUpdatedSkill
            .name(UPDATED_NAME)
            .createdDate(UPDATED_CREATED_DATE)
            .createdBy(UPDATED_CREATED_BY)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .active(UPDATED_ACTIVE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedSkill.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedSkill))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Skill in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSkillUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedSkill, skill), getPersistedSkill(skill));
    }

    @Test
    void fullUpdateSkillWithPatch() throws Exception {
        // Initialize the database
        insertedSkill = skillRepository.save(skill).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the skill using partial update
        Skill partialUpdatedSkill = new Skill();
        partialUpdatedSkill.setId(skill.getId());

        partialUpdatedSkill
            .name(UPDATED_NAME)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .createdBy(UPDATED_CREATED_BY)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .active(UPDATED_ACTIVE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedSkill.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedSkill))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Skill in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSkillUpdatableFieldsEquals(partialUpdatedSkill, getPersistedSkill(partialUpdatedSkill));
    }

    @Test
    void patchNonExistingSkill() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        skill.setId(longCount.incrementAndGet());

        // Create the Skill
        SkillDTO skillDTO = skillMapper.toDto(skill);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, skillDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(skillDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Skill in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchSkill() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        skill.setId(longCount.incrementAndGet());

        // Create the Skill
        SkillDTO skillDTO = skillMapper.toDto(skill);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(skillDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Skill in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamSkill() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        skill.setId(longCount.incrementAndGet());

        // Create the Skill
        SkillDTO skillDTO = skillMapper.toDto(skill);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(skillDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Skill in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteSkill() {
        // Initialize the database
        insertedSkill = skillRepository.save(skill).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the skill
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, skill.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return skillRepository.count().block();
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

    protected Skill getPersistedSkill(Skill skill) {
        return skillRepository.findById(skill.getId()).block();
    }

    protected void assertPersistedSkillToMatchAllProperties(Skill expectedSkill) {
        // Test fails because reactive api returns an empty object instead of null
        // assertSkillAllPropertiesEquals(expectedSkill, getPersistedSkill(expectedSkill));
        assertSkillUpdatableFieldsEquals(expectedSkill, getPersistedSkill(expectedSkill));
    }

    protected void assertPersistedSkillToMatchUpdatableProperties(Skill expectedSkill) {
        // Test fails because reactive api returns an empty object instead of null
        // assertSkillAllUpdatablePropertiesEquals(expectedSkill, getPersistedSkill(expectedSkill));
        assertSkillUpdatableFieldsEquals(expectedSkill, getPersistedSkill(expectedSkill));
    }
}
