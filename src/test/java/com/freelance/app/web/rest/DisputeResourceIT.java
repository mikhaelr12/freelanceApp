package com.freelance.app.web.rest;

import static com.freelance.app.domain.DisputeAsserts.*;
import static com.freelance.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.freelance.app.IntegrationTest;
import com.freelance.app.domain.Dispute;
import com.freelance.app.domain.Order;
import com.freelance.app.repository.DisputeRepository;
import com.freelance.app.repository.EntityManager;
import com.freelance.app.repository.OrderRepository;
import com.freelance.app.service.dto.DisputeDTO;
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
 * Integration tests for the {@link DisputeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class DisputeResourceIT {

    private static final String DEFAULT_REASON = "AAAAAAAAAA";
    private static final String UPDATED_REASON = "BBBBBBBBBB";

    private static final Instant DEFAULT_OPENED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_OPENED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_CLOSED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CLOSED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/disputes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private DisputeRepository disputeRepository;

    @Autowired
    private DisputeMapper disputeMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Dispute dispute;

    private Dispute insertedDispute;

    @Autowired
    private OrderRepository orderRepository;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Dispute createEntity() {
        return new Dispute().reason(DEFAULT_REASON).openedAt(DEFAULT_OPENED_AT).closedAt(DEFAULT_CLOSED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Dispute createUpdatedEntity() {
        return new Dispute().reason(UPDATED_REASON).openedAt(UPDATED_OPENED_AT).closedAt(UPDATED_CLOSED_AT);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Dispute.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        dispute = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedDispute != null) {
            disputeRepository.delete(insertedDispute).block();
            insertedDispute = null;
        }
        deleteEntities(em);
    }

    @Test
    void createDispute() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Dispute
        DisputeDTO disputeDTO = disputeMapper.toDto(dispute);
        var returnedDisputeDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(disputeDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(DisputeDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Dispute in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedDispute = disputeMapper.toEntity(returnedDisputeDTO);
        assertDisputeUpdatableFieldsEquals(returnedDispute, getPersistedDispute(returnedDispute));

        insertedDispute = returnedDispute;
    }

    @Test
    void createDisputeWithExistingId() throws Exception {
        // Create the Dispute with an existing ID
        dispute.setId(1L);
        DisputeDTO disputeDTO = disputeMapper.toDto(dispute);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(disputeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Dispute in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkReasonIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        dispute.setReason(null);

        // Create the Dispute, which fails.
        DisputeDTO disputeDTO = disputeMapper.toDto(dispute);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(disputeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkOpenedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        dispute.setOpenedAt(null);

        // Create the Dispute, which fails.
        DisputeDTO disputeDTO = disputeMapper.toDto(dispute);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(disputeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllDisputes() {
        // Initialize the database
        insertedDispute = disputeRepository.save(dispute).block();

        // Get all the disputeList
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
            .value(hasItem(dispute.getId().intValue()))
            .jsonPath("$.[*].reason")
            .value(hasItem(DEFAULT_REASON))
            .jsonPath("$.[*].openedAt")
            .value(hasItem(DEFAULT_OPENED_AT.toString()))
            .jsonPath("$.[*].closedAt")
            .value(hasItem(DEFAULT_CLOSED_AT.toString()));
    }

    @Test
    void getDispute() {
        // Initialize the database
        insertedDispute = disputeRepository.save(dispute).block();

        // Get the dispute
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, dispute.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(dispute.getId().intValue()))
            .jsonPath("$.reason")
            .value(is(DEFAULT_REASON))
            .jsonPath("$.openedAt")
            .value(is(DEFAULT_OPENED_AT.toString()))
            .jsonPath("$.closedAt")
            .value(is(DEFAULT_CLOSED_AT.toString()));
    }

    @Test
    void getDisputesByIdFiltering() {
        // Initialize the database
        insertedDispute = disputeRepository.save(dispute).block();

        Long id = dispute.getId();

        defaultDisputeFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultDisputeFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultDisputeFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllDisputesByReasonIsEqualToSomething() {
        // Initialize the database
        insertedDispute = disputeRepository.save(dispute).block();

        // Get all the disputeList where reason equals to
        defaultDisputeFiltering("reason.equals=" + DEFAULT_REASON, "reason.equals=" + UPDATED_REASON);
    }

    @Test
    void getAllDisputesByReasonIsInShouldWork() {
        // Initialize the database
        insertedDispute = disputeRepository.save(dispute).block();

        // Get all the disputeList where reason in
        defaultDisputeFiltering("reason.in=" + DEFAULT_REASON + "," + UPDATED_REASON, "reason.in=" + UPDATED_REASON);
    }

    @Test
    void getAllDisputesByReasonIsNullOrNotNull() {
        // Initialize the database
        insertedDispute = disputeRepository.save(dispute).block();

        // Get all the disputeList where reason is not null
        defaultDisputeFiltering("reason.specified=true", "reason.specified=false");
    }

    @Test
    void getAllDisputesByReasonContainsSomething() {
        // Initialize the database
        insertedDispute = disputeRepository.save(dispute).block();

        // Get all the disputeList where reason contains
        defaultDisputeFiltering("reason.contains=" + DEFAULT_REASON, "reason.contains=" + UPDATED_REASON);
    }

    @Test
    void getAllDisputesByReasonNotContainsSomething() {
        // Initialize the database
        insertedDispute = disputeRepository.save(dispute).block();

        // Get all the disputeList where reason does not contain
        defaultDisputeFiltering("reason.doesNotContain=" + UPDATED_REASON, "reason.doesNotContain=" + DEFAULT_REASON);
    }

    @Test
    void getAllDisputesByOpenedAtIsEqualToSomething() {
        // Initialize the database
        insertedDispute = disputeRepository.save(dispute).block();

        // Get all the disputeList where openedAt equals to
        defaultDisputeFiltering("openedAt.equals=" + DEFAULT_OPENED_AT, "openedAt.equals=" + UPDATED_OPENED_AT);
    }

    @Test
    void getAllDisputesByOpenedAtIsInShouldWork() {
        // Initialize the database
        insertedDispute = disputeRepository.save(dispute).block();

        // Get all the disputeList where openedAt in
        defaultDisputeFiltering("openedAt.in=" + DEFAULT_OPENED_AT + "," + UPDATED_OPENED_AT, "openedAt.in=" + UPDATED_OPENED_AT);
    }

    @Test
    void getAllDisputesByOpenedAtIsNullOrNotNull() {
        // Initialize the database
        insertedDispute = disputeRepository.save(dispute).block();

        // Get all the disputeList where openedAt is not null
        defaultDisputeFiltering("openedAt.specified=true", "openedAt.specified=false");
    }

    @Test
    void getAllDisputesByClosedAtIsEqualToSomething() {
        // Initialize the database
        insertedDispute = disputeRepository.save(dispute).block();

        // Get all the disputeList where closedAt equals to
        defaultDisputeFiltering("closedAt.equals=" + DEFAULT_CLOSED_AT, "closedAt.equals=" + UPDATED_CLOSED_AT);
    }

    @Test
    void getAllDisputesByClosedAtIsInShouldWork() {
        // Initialize the database
        insertedDispute = disputeRepository.save(dispute).block();

        // Get all the disputeList where closedAt in
        defaultDisputeFiltering("closedAt.in=" + DEFAULT_CLOSED_AT + "," + UPDATED_CLOSED_AT, "closedAt.in=" + UPDATED_CLOSED_AT);
    }

    @Test
    void getAllDisputesByClosedAtIsNullOrNotNull() {
        // Initialize the database
        insertedDispute = disputeRepository.save(dispute).block();

        // Get all the disputeList where closedAt is not null
        defaultDisputeFiltering("closedAt.specified=true", "closedAt.specified=false");
    }

    @Test
    void getAllDisputesByOrderIsEqualToSomething() {
        Order order = OrderResourceIT.createEntity();
        orderRepository.save(order).block();
        Long orderId = order.getId();
        dispute.setOrderId(orderId);
        insertedDispute = disputeRepository.save(dispute).block();
        // Get all the disputeList where order equals to orderId
        defaultDisputeShouldBeFound("orderId.equals=" + orderId);

        // Get all the disputeList where order equals to (orderId + 1)
        defaultDisputeShouldNotBeFound("orderId.equals=" + (orderId + 1));
    }

    private void defaultDisputeFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultDisputeShouldBeFound(shouldBeFound);
        defaultDisputeShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultDisputeShouldBeFound(String filter) {
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
            .value(hasItem(dispute.getId().intValue()))
            .jsonPath("$.[*].reason")
            .value(hasItem(DEFAULT_REASON))
            .jsonPath("$.[*].openedAt")
            .value(hasItem(DEFAULT_OPENED_AT.toString()))
            .jsonPath("$.[*].closedAt")
            .value(hasItem(DEFAULT_CLOSED_AT.toString()));

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
    private void defaultDisputeShouldNotBeFound(String filter) {
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
    void getNonExistingDispute() {
        // Get the dispute
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingDispute() throws Exception {
        // Initialize the database
        insertedDispute = disputeRepository.save(dispute).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the dispute
        Dispute updatedDispute = disputeRepository.findById(dispute.getId()).block();
        updatedDispute.reason(UPDATED_REASON).openedAt(UPDATED_OPENED_AT).closedAt(UPDATED_CLOSED_AT);
        DisputeDTO disputeDTO = disputeMapper.toDto(updatedDispute);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, disputeDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(disputeDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Dispute in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedDisputeToMatchAllProperties(updatedDispute);
    }

    @Test
    void putNonExistingDispute() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        dispute.setId(longCount.incrementAndGet());

        // Create the Dispute
        DisputeDTO disputeDTO = disputeMapper.toDto(dispute);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, disputeDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(disputeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Dispute in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchDispute() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        dispute.setId(longCount.incrementAndGet());

        // Create the Dispute
        DisputeDTO disputeDTO = disputeMapper.toDto(dispute);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(disputeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Dispute in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamDispute() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        dispute.setId(longCount.incrementAndGet());

        // Create the Dispute
        DisputeDTO disputeDTO = disputeMapper.toDto(dispute);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(disputeDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Dispute in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateDisputeWithPatch() throws Exception {
        // Initialize the database
        insertedDispute = disputeRepository.save(dispute).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the dispute using partial update
        Dispute partialUpdatedDispute = new Dispute();
        partialUpdatedDispute.setId(dispute.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedDispute.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedDispute))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Dispute in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDisputeUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedDispute, dispute), getPersistedDispute(dispute));
    }

    @Test
    void fullUpdateDisputeWithPatch() throws Exception {
        // Initialize the database
        insertedDispute = disputeRepository.save(dispute).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the dispute using partial update
        Dispute partialUpdatedDispute = new Dispute();
        partialUpdatedDispute.setId(dispute.getId());

        partialUpdatedDispute.reason(UPDATED_REASON).openedAt(UPDATED_OPENED_AT).closedAt(UPDATED_CLOSED_AT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedDispute.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedDispute))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Dispute in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDisputeUpdatableFieldsEquals(partialUpdatedDispute, getPersistedDispute(partialUpdatedDispute));
    }

    @Test
    void patchNonExistingDispute() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        dispute.setId(longCount.incrementAndGet());

        // Create the Dispute
        DisputeDTO disputeDTO = disputeMapper.toDto(dispute);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, disputeDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(disputeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Dispute in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchDispute() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        dispute.setId(longCount.incrementAndGet());

        // Create the Dispute
        DisputeDTO disputeDTO = disputeMapper.toDto(dispute);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(disputeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Dispute in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamDispute() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        dispute.setId(longCount.incrementAndGet());

        // Create the Dispute
        DisputeDTO disputeDTO = disputeMapper.toDto(dispute);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(disputeDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Dispute in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteDispute() {
        // Initialize the database
        insertedDispute = disputeRepository.save(dispute).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the dispute
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, dispute.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return disputeRepository.count().block();
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

    protected Dispute getPersistedDispute(Dispute dispute) {
        return disputeRepository.findById(dispute.getId()).block();
    }

    protected void assertPersistedDisputeToMatchAllProperties(Dispute expectedDispute) {
        // Test fails because reactive api returns an empty object instead of null
        // assertDisputeAllPropertiesEquals(expectedDispute, getPersistedDispute(expectedDispute));
        assertDisputeUpdatableFieldsEquals(expectedDispute, getPersistedDispute(expectedDispute));
    }

    protected void assertPersistedDisputeToMatchUpdatableProperties(Dispute expectedDispute) {
        // Test fails because reactive api returns an empty object instead of null
        // assertDisputeAllUpdatablePropertiesEquals(expectedDispute, getPersistedDispute(expectedDispute));
        assertDisputeUpdatableFieldsEquals(expectedDispute, getPersistedDispute(expectedDispute));
    }
}
