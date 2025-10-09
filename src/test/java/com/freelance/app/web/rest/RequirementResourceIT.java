package com.freelance.app.web.rest;

import static com.freelance.app.domain.RequirementAsserts.*;
import static com.freelance.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.freelance.app.IntegrationTest;
import com.freelance.app.domain.Order;
import com.freelance.app.domain.Requirement;
import com.freelance.app.repository.EntityManager;
import com.freelance.app.repository.OrderRepository;
import com.freelance.app.repository.RequirementRepository;
import com.freelance.app.service.dto.RequirementDTO;
import com.freelance.app.service.mapper.RequirementMapper;
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
 * Integration tests for the {@link RequirementResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class RequirementResourceIT {

    private static final String DEFAULT_PROMPT = "AAAAAAAAAA";
    private static final String UPDATED_PROMPT = "BBBBBBBBBB";

    private static final String DEFAULT_ANSWER = "AAAAAAAAAA";
    private static final String UPDATED_ANSWER = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/requirements";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private RequirementRepository requirementRepository;

    @Autowired
    private RequirementMapper requirementMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Requirement requirement;

    private Requirement insertedRequirement;

    @Autowired
    private OrderRepository orderRepository;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Requirement createEntity() {
        return new Requirement().prompt(DEFAULT_PROMPT).answer(DEFAULT_ANSWER);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Requirement createUpdatedEntity() {
        return new Requirement().prompt(UPDATED_PROMPT).answer(UPDATED_ANSWER);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Requirement.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        requirement = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedRequirement != null) {
            requirementRepository.delete(insertedRequirement).block();
            insertedRequirement = null;
        }
        deleteEntities(em);
    }

    @Test
    void createRequirement() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Requirement
        RequirementDTO requirementDTO = requirementMapper.toDto(requirement);
        var returnedRequirementDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(requirementDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(RequirementDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Requirement in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedRequirement = requirementMapper.toEntity(returnedRequirementDTO);
        assertRequirementUpdatableFieldsEquals(returnedRequirement, getPersistedRequirement(returnedRequirement));

        insertedRequirement = returnedRequirement;
    }

    @Test
    void createRequirementWithExistingId() throws Exception {
        // Create the Requirement with an existing ID
        requirement.setId(1L);
        RequirementDTO requirementDTO = requirementMapper.toDto(requirement);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(requirementDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Requirement in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkPromptIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        requirement.setPrompt(null);

        // Create the Requirement, which fails.
        RequirementDTO requirementDTO = requirementMapper.toDto(requirement);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(requirementDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllRequirements() {
        // Initialize the database
        insertedRequirement = requirementRepository.save(requirement).block();

        // Get all the requirementList
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
            .value(hasItem(requirement.getId().intValue()))
            .jsonPath("$.[*].prompt")
            .value(hasItem(DEFAULT_PROMPT))
            .jsonPath("$.[*].answer")
            .value(hasItem(DEFAULT_ANSWER));
    }

    @Test
    void getRequirement() {
        // Initialize the database
        insertedRequirement = requirementRepository.save(requirement).block();

        // Get the requirement
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, requirement.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(requirement.getId().intValue()))
            .jsonPath("$.prompt")
            .value(is(DEFAULT_PROMPT))
            .jsonPath("$.answer")
            .value(is(DEFAULT_ANSWER));
    }

    @Test
    void getRequirementsByIdFiltering() {
        // Initialize the database
        insertedRequirement = requirementRepository.save(requirement).block();

        Long id = requirement.getId();

        defaultRequirementFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultRequirementFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultRequirementFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllRequirementsByPromptIsEqualToSomething() {
        // Initialize the database
        insertedRequirement = requirementRepository.save(requirement).block();

        // Get all the requirementList where prompt equals to
        defaultRequirementFiltering("prompt.equals=" + DEFAULT_PROMPT, "prompt.equals=" + UPDATED_PROMPT);
    }

    @Test
    void getAllRequirementsByPromptIsInShouldWork() {
        // Initialize the database
        insertedRequirement = requirementRepository.save(requirement).block();

        // Get all the requirementList where prompt in
        defaultRequirementFiltering("prompt.in=" + DEFAULT_PROMPT + "," + UPDATED_PROMPT, "prompt.in=" + UPDATED_PROMPT);
    }

    @Test
    void getAllRequirementsByPromptIsNullOrNotNull() {
        // Initialize the database
        insertedRequirement = requirementRepository.save(requirement).block();

        // Get all the requirementList where prompt is not null
        defaultRequirementFiltering("prompt.specified=true", "prompt.specified=false");
    }

    @Test
    void getAllRequirementsByPromptContainsSomething() {
        // Initialize the database
        insertedRequirement = requirementRepository.save(requirement).block();

        // Get all the requirementList where prompt contains
        defaultRequirementFiltering("prompt.contains=" + DEFAULT_PROMPT, "prompt.contains=" + UPDATED_PROMPT);
    }

    @Test
    void getAllRequirementsByPromptNotContainsSomething() {
        // Initialize the database
        insertedRequirement = requirementRepository.save(requirement).block();

        // Get all the requirementList where prompt does not contain
        defaultRequirementFiltering("prompt.doesNotContain=" + UPDATED_PROMPT, "prompt.doesNotContain=" + DEFAULT_PROMPT);
    }

    @Test
    void getAllRequirementsByAnswerIsEqualToSomething() {
        // Initialize the database
        insertedRequirement = requirementRepository.save(requirement).block();

        // Get all the requirementList where answer equals to
        defaultRequirementFiltering("answer.equals=" + DEFAULT_ANSWER, "answer.equals=" + UPDATED_ANSWER);
    }

    @Test
    void getAllRequirementsByAnswerIsInShouldWork() {
        // Initialize the database
        insertedRequirement = requirementRepository.save(requirement).block();

        // Get all the requirementList where answer in
        defaultRequirementFiltering("answer.in=" + DEFAULT_ANSWER + "," + UPDATED_ANSWER, "answer.in=" + UPDATED_ANSWER);
    }

    @Test
    void getAllRequirementsByAnswerIsNullOrNotNull() {
        // Initialize the database
        insertedRequirement = requirementRepository.save(requirement).block();

        // Get all the requirementList where answer is not null
        defaultRequirementFiltering("answer.specified=true", "answer.specified=false");
    }

    @Test
    void getAllRequirementsByAnswerContainsSomething() {
        // Initialize the database
        insertedRequirement = requirementRepository.save(requirement).block();

        // Get all the requirementList where answer contains
        defaultRequirementFiltering("answer.contains=" + DEFAULT_ANSWER, "answer.contains=" + UPDATED_ANSWER);
    }

    @Test
    void getAllRequirementsByAnswerNotContainsSomething() {
        // Initialize the database
        insertedRequirement = requirementRepository.save(requirement).block();

        // Get all the requirementList where answer does not contain
        defaultRequirementFiltering("answer.doesNotContain=" + UPDATED_ANSWER, "answer.doesNotContain=" + DEFAULT_ANSWER);
    }

    @Test
    void getAllRequirementsByOrderIsEqualToSomething() {
        Order order = OrderResourceIT.createEntity();
        orderRepository.save(order).block();
        Long orderId = order.getId();
        requirement.setOrderId(orderId);
        insertedRequirement = requirementRepository.save(requirement).block();
        // Get all the requirementList where order equals to orderId
        defaultRequirementShouldBeFound("orderId.equals=" + orderId);

        // Get all the requirementList where order equals to (orderId + 1)
        defaultRequirementShouldNotBeFound("orderId.equals=" + (orderId + 1));
    }

    private void defaultRequirementFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultRequirementShouldBeFound(shouldBeFound);
        defaultRequirementShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultRequirementShouldBeFound(String filter) {
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
            .value(hasItem(requirement.getId().intValue()))
            .jsonPath("$.[*].prompt")
            .value(hasItem(DEFAULT_PROMPT))
            .jsonPath("$.[*].answer")
            .value(hasItem(DEFAULT_ANSWER));

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
    private void defaultRequirementShouldNotBeFound(String filter) {
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
    void getNonExistingRequirement() {
        // Get the requirement
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingRequirement() throws Exception {
        // Initialize the database
        insertedRequirement = requirementRepository.save(requirement).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the requirement
        Requirement updatedRequirement = requirementRepository.findById(requirement.getId()).block();
        updatedRequirement.prompt(UPDATED_PROMPT).answer(UPDATED_ANSWER);
        RequirementDTO requirementDTO = requirementMapper.toDto(updatedRequirement);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, requirementDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(requirementDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Requirement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedRequirementToMatchAllProperties(updatedRequirement);
    }

    @Test
    void putNonExistingRequirement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        requirement.setId(longCount.incrementAndGet());

        // Create the Requirement
        RequirementDTO requirementDTO = requirementMapper.toDto(requirement);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, requirementDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(requirementDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Requirement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchRequirement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        requirement.setId(longCount.incrementAndGet());

        // Create the Requirement
        RequirementDTO requirementDTO = requirementMapper.toDto(requirement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(requirementDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Requirement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamRequirement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        requirement.setId(longCount.incrementAndGet());

        // Create the Requirement
        RequirementDTO requirementDTO = requirementMapper.toDto(requirement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(requirementDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Requirement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateRequirementWithPatch() throws Exception {
        // Initialize the database
        insertedRequirement = requirementRepository.save(requirement).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the requirement using partial update
        Requirement partialUpdatedRequirement = new Requirement();
        partialUpdatedRequirement.setId(requirement.getId());

        partialUpdatedRequirement.prompt(UPDATED_PROMPT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRequirement.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedRequirement))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Requirement in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRequirementUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedRequirement, requirement),
            getPersistedRequirement(requirement)
        );
    }

    @Test
    void fullUpdateRequirementWithPatch() throws Exception {
        // Initialize the database
        insertedRequirement = requirementRepository.save(requirement).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the requirement using partial update
        Requirement partialUpdatedRequirement = new Requirement();
        partialUpdatedRequirement.setId(requirement.getId());

        partialUpdatedRequirement.prompt(UPDATED_PROMPT).answer(UPDATED_ANSWER);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRequirement.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedRequirement))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Requirement in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRequirementUpdatableFieldsEquals(partialUpdatedRequirement, getPersistedRequirement(partialUpdatedRequirement));
    }

    @Test
    void patchNonExistingRequirement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        requirement.setId(longCount.incrementAndGet());

        // Create the Requirement
        RequirementDTO requirementDTO = requirementMapper.toDto(requirement);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, requirementDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(requirementDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Requirement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchRequirement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        requirement.setId(longCount.incrementAndGet());

        // Create the Requirement
        RequirementDTO requirementDTO = requirementMapper.toDto(requirement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(requirementDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Requirement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamRequirement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        requirement.setId(longCount.incrementAndGet());

        // Create the Requirement
        RequirementDTO requirementDTO = requirementMapper.toDto(requirement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(requirementDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Requirement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteRequirement() {
        // Initialize the database
        insertedRequirement = requirementRepository.save(requirement).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the requirement
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, requirement.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return requirementRepository.count().block();
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

    protected Requirement getPersistedRequirement(Requirement requirement) {
        return requirementRepository.findById(requirement.getId()).block();
    }

    protected void assertPersistedRequirementToMatchAllProperties(Requirement expectedRequirement) {
        // Test fails because reactive api returns an empty object instead of null
        // assertRequirementAllPropertiesEquals(expectedRequirement, getPersistedRequirement(expectedRequirement));
        assertRequirementUpdatableFieldsEquals(expectedRequirement, getPersistedRequirement(expectedRequirement));
    }

    protected void assertPersistedRequirementToMatchUpdatableProperties(Requirement expectedRequirement) {
        // Test fails because reactive api returns an empty object instead of null
        // assertRequirementAllUpdatablePropertiesEquals(expectedRequirement, getPersistedRequirement(expectedRequirement));
        assertRequirementUpdatableFieldsEquals(expectedRequirement, getPersistedRequirement(expectedRequirement));
    }
}
