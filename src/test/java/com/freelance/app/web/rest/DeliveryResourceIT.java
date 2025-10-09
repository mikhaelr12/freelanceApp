package com.freelance.app.web.rest;

import static com.freelance.app.domain.DeliveryAsserts.*;
import static com.freelance.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.freelance.app.IntegrationTest;
import com.freelance.app.domain.Delivery;
import com.freelance.app.domain.FileObject;
import com.freelance.app.domain.Order;
import com.freelance.app.repository.DeliveryRepository;
import com.freelance.app.repository.EntityManager;
import com.freelance.app.repository.FileObjectRepository;
import com.freelance.app.repository.OrderRepository;
import com.freelance.app.service.DeliveryService;
import com.freelance.app.service.dto.DeliveryDTO;
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
 * Integration tests for the {@link DeliveryResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class DeliveryResourceIT {

    private static final String DEFAULT_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_NOTE = "BBBBBBBBBB";

    private static final Instant DEFAULT_DELIVERED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DELIVERED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/deliveries";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Mock
    private DeliveryRepository deliveryRepositoryMock;

    @Autowired
    private DeliveryMapper deliveryMapper;

    @Mock
    private DeliveryService deliveryServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Delivery delivery;

    private Delivery insertedDelivery;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private FileObjectRepository fileObjectRepository;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Delivery createEntity() {
        return new Delivery().note(DEFAULT_NOTE).deliveredAt(DEFAULT_DELIVERED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Delivery createUpdatedEntity() {
        return new Delivery().note(UPDATED_NOTE).deliveredAt(UPDATED_DELIVERED_AT);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Delivery.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        delivery = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedDelivery != null) {
            deliveryRepository.delete(insertedDelivery).block();
            insertedDelivery = null;
        }
        deleteEntities(em);
    }

    @Test
    void createDelivery() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Delivery
        DeliveryDTO deliveryDTO = deliveryMapper.toDto(delivery);
        var returnedDeliveryDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(deliveryDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(DeliveryDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Delivery in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedDelivery = deliveryMapper.toEntity(returnedDeliveryDTO);
        assertDeliveryUpdatableFieldsEquals(returnedDelivery, getPersistedDelivery(returnedDelivery));

        insertedDelivery = returnedDelivery;
    }

    @Test
    void createDeliveryWithExistingId() throws Exception {
        // Create the Delivery with an existing ID
        delivery.setId(1L);
        DeliveryDTO deliveryDTO = deliveryMapper.toDto(delivery);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(deliveryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Delivery in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkDeliveredAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        delivery.setDeliveredAt(null);

        // Create the Delivery, which fails.
        DeliveryDTO deliveryDTO = deliveryMapper.toDto(delivery);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(deliveryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllDeliveries() {
        // Initialize the database
        insertedDelivery = deliveryRepository.save(delivery).block();

        // Get all the deliveryList
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
            .value(hasItem(delivery.getId().intValue()))
            .jsonPath("$.[*].note")
            .value(hasItem(DEFAULT_NOTE))
            .jsonPath("$.[*].deliveredAt")
            .value(hasItem(DEFAULT_DELIVERED_AT.toString()));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllDeliveriesWithEagerRelationshipsIsEnabled() {
        when(deliveryServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(deliveryServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllDeliveriesWithEagerRelationshipsIsNotEnabled() {
        when(deliveryServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(deliveryRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getDelivery() {
        // Initialize the database
        insertedDelivery = deliveryRepository.save(delivery).block();

        // Get the delivery
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, delivery.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(delivery.getId().intValue()))
            .jsonPath("$.note")
            .value(is(DEFAULT_NOTE))
            .jsonPath("$.deliveredAt")
            .value(is(DEFAULT_DELIVERED_AT.toString()));
    }

    @Test
    void getDeliveriesByIdFiltering() {
        // Initialize the database
        insertedDelivery = deliveryRepository.save(delivery).block();

        Long id = delivery.getId();

        defaultDeliveryFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultDeliveryFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultDeliveryFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllDeliveriesByNoteIsEqualToSomething() {
        // Initialize the database
        insertedDelivery = deliveryRepository.save(delivery).block();

        // Get all the deliveryList where note equals to
        defaultDeliveryFiltering("note.equals=" + DEFAULT_NOTE, "note.equals=" + UPDATED_NOTE);
    }

    @Test
    void getAllDeliveriesByNoteIsInShouldWork() {
        // Initialize the database
        insertedDelivery = deliveryRepository.save(delivery).block();

        // Get all the deliveryList where note in
        defaultDeliveryFiltering("note.in=" + DEFAULT_NOTE + "," + UPDATED_NOTE, "note.in=" + UPDATED_NOTE);
    }

    @Test
    void getAllDeliveriesByNoteIsNullOrNotNull() {
        // Initialize the database
        insertedDelivery = deliveryRepository.save(delivery).block();

        // Get all the deliveryList where note is not null
        defaultDeliveryFiltering("note.specified=true", "note.specified=false");
    }

    @Test
    void getAllDeliveriesByNoteContainsSomething() {
        // Initialize the database
        insertedDelivery = deliveryRepository.save(delivery).block();

        // Get all the deliveryList where note contains
        defaultDeliveryFiltering("note.contains=" + DEFAULT_NOTE, "note.contains=" + UPDATED_NOTE);
    }

    @Test
    void getAllDeliveriesByNoteNotContainsSomething() {
        // Initialize the database
        insertedDelivery = deliveryRepository.save(delivery).block();

        // Get all the deliveryList where note does not contain
        defaultDeliveryFiltering("note.doesNotContain=" + UPDATED_NOTE, "note.doesNotContain=" + DEFAULT_NOTE);
    }

    @Test
    void getAllDeliveriesByDeliveredAtIsEqualToSomething() {
        // Initialize the database
        insertedDelivery = deliveryRepository.save(delivery).block();

        // Get all the deliveryList where deliveredAt equals to
        defaultDeliveryFiltering("deliveredAt.equals=" + DEFAULT_DELIVERED_AT, "deliveredAt.equals=" + UPDATED_DELIVERED_AT);
    }

    @Test
    void getAllDeliveriesByDeliveredAtIsInShouldWork() {
        // Initialize the database
        insertedDelivery = deliveryRepository.save(delivery).block();

        // Get all the deliveryList where deliveredAt in
        defaultDeliveryFiltering(
            "deliveredAt.in=" + DEFAULT_DELIVERED_AT + "," + UPDATED_DELIVERED_AT,
            "deliveredAt.in=" + UPDATED_DELIVERED_AT
        );
    }

    @Test
    void getAllDeliveriesByDeliveredAtIsNullOrNotNull() {
        // Initialize the database
        insertedDelivery = deliveryRepository.save(delivery).block();

        // Get all the deliveryList where deliveredAt is not null
        defaultDeliveryFiltering("deliveredAt.specified=true", "deliveredAt.specified=false");
    }

    @Test
    void getAllDeliveriesByOrderIsEqualToSomething() {
        Order order = OrderResourceIT.createEntity();
        orderRepository.save(order).block();
        Long orderId = order.getId();
        delivery.setOrderId(orderId);
        insertedDelivery = deliveryRepository.save(delivery).block();
        // Get all the deliveryList where order equals to orderId
        defaultDeliveryShouldBeFound("orderId.equals=" + orderId);

        // Get all the deliveryList where order equals to (orderId + 1)
        defaultDeliveryShouldNotBeFound("orderId.equals=" + (orderId + 1));
    }

    @Test
    void getAllDeliveriesByFileIsEqualToSomething() {
        FileObject file = FileObjectResourceIT.createEntity();
        fileObjectRepository.save(file).block();
        Long fileId = file.getId();
        delivery.setFileId(fileId);
        insertedDelivery = deliveryRepository.save(delivery).block();
        // Get all the deliveryList where file equals to fileId
        defaultDeliveryShouldBeFound("fileId.equals=" + fileId);

        // Get all the deliveryList where file equals to (fileId + 1)
        defaultDeliveryShouldNotBeFound("fileId.equals=" + (fileId + 1));
    }

    private void defaultDeliveryFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultDeliveryShouldBeFound(shouldBeFound);
        defaultDeliveryShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultDeliveryShouldBeFound(String filter) {
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
            .value(hasItem(delivery.getId().intValue()))
            .jsonPath("$.[*].note")
            .value(hasItem(DEFAULT_NOTE))
            .jsonPath("$.[*].deliveredAt")
            .value(hasItem(DEFAULT_DELIVERED_AT.toString()));

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
    private void defaultDeliveryShouldNotBeFound(String filter) {
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
    void getNonExistingDelivery() {
        // Get the delivery
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingDelivery() throws Exception {
        // Initialize the database
        insertedDelivery = deliveryRepository.save(delivery).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the delivery
        Delivery updatedDelivery = deliveryRepository.findById(delivery.getId()).block();
        updatedDelivery.note(UPDATED_NOTE).deliveredAt(UPDATED_DELIVERED_AT);
        DeliveryDTO deliveryDTO = deliveryMapper.toDto(updatedDelivery);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, deliveryDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(deliveryDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Delivery in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedDeliveryToMatchAllProperties(updatedDelivery);
    }

    @Test
    void putNonExistingDelivery() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        delivery.setId(longCount.incrementAndGet());

        // Create the Delivery
        DeliveryDTO deliveryDTO = deliveryMapper.toDto(delivery);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, deliveryDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(deliveryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Delivery in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchDelivery() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        delivery.setId(longCount.incrementAndGet());

        // Create the Delivery
        DeliveryDTO deliveryDTO = deliveryMapper.toDto(delivery);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(deliveryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Delivery in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamDelivery() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        delivery.setId(longCount.incrementAndGet());

        // Create the Delivery
        DeliveryDTO deliveryDTO = deliveryMapper.toDto(delivery);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(deliveryDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Delivery in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateDeliveryWithPatch() throws Exception {
        // Initialize the database
        insertedDelivery = deliveryRepository.save(delivery).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the delivery using partial update
        Delivery partialUpdatedDelivery = new Delivery();
        partialUpdatedDelivery.setId(delivery.getId());

        partialUpdatedDelivery.deliveredAt(UPDATED_DELIVERED_AT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedDelivery.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedDelivery))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Delivery in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDeliveryUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedDelivery, delivery), getPersistedDelivery(delivery));
    }

    @Test
    void fullUpdateDeliveryWithPatch() throws Exception {
        // Initialize the database
        insertedDelivery = deliveryRepository.save(delivery).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the delivery using partial update
        Delivery partialUpdatedDelivery = new Delivery();
        partialUpdatedDelivery.setId(delivery.getId());

        partialUpdatedDelivery.note(UPDATED_NOTE).deliveredAt(UPDATED_DELIVERED_AT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedDelivery.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedDelivery))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Delivery in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDeliveryUpdatableFieldsEquals(partialUpdatedDelivery, getPersistedDelivery(partialUpdatedDelivery));
    }

    @Test
    void patchNonExistingDelivery() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        delivery.setId(longCount.incrementAndGet());

        // Create the Delivery
        DeliveryDTO deliveryDTO = deliveryMapper.toDto(delivery);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, deliveryDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(deliveryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Delivery in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchDelivery() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        delivery.setId(longCount.incrementAndGet());

        // Create the Delivery
        DeliveryDTO deliveryDTO = deliveryMapper.toDto(delivery);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(deliveryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Delivery in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamDelivery() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        delivery.setId(longCount.incrementAndGet());

        // Create the Delivery
        DeliveryDTO deliveryDTO = deliveryMapper.toDto(delivery);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(deliveryDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Delivery in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteDelivery() {
        // Initialize the database
        insertedDelivery = deliveryRepository.save(delivery).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the delivery
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, delivery.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return deliveryRepository.count().block();
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

    protected Delivery getPersistedDelivery(Delivery delivery) {
        return deliveryRepository.findById(delivery.getId()).block();
    }

    protected void assertPersistedDeliveryToMatchAllProperties(Delivery expectedDelivery) {
        // Test fails because reactive api returns an empty object instead of null
        // assertDeliveryAllPropertiesEquals(expectedDelivery, getPersistedDelivery(expectedDelivery));
        assertDeliveryUpdatableFieldsEquals(expectedDelivery, getPersistedDelivery(expectedDelivery));
    }

    protected void assertPersistedDeliveryToMatchUpdatableProperties(Delivery expectedDelivery) {
        // Test fails because reactive api returns an empty object instead of null
        // assertDeliveryAllUpdatablePropertiesEquals(expectedDelivery, getPersistedDelivery(expectedDelivery));
        assertDeliveryUpdatableFieldsEquals(expectedDelivery, getPersistedDelivery(expectedDelivery));
    }
}
