//package com.freelance.app.web.rest;
//
//import static com.freelance.app.domain.VerificationRequestAsserts.*;
//import static com.freelance.app.web.rest.TestUtil.createUpdateProxyForBean;
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.hamcrest.Matchers.hasItem;
//import static org.hamcrest.Matchers.is;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.freelance.app.IntegrationTest;
//import com.freelance.app.domain.FileObject;
//import com.freelance.app.domain.Profile;
//import com.freelance.app.domain.VerificationRequest;
//import com.freelance.app.repository.EntityManager;
//import com.freelance.app.repository.FileObjectRepository;
//import com.freelance.app.repository.ProfileRepository;
//import com.freelance.app.repository.VerificationRequestRepository;
//import java.util.Random;
//import java.util.concurrent.atomic.AtomicLong;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
//import org.springframework.http.MediaType;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.web.reactive.server.WebTestClient;
//
///**
// * Integration tests for the {@link VerificationRequestResource} REST controller.
// */
//@IntegrationTest
//@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
//@WithMockUser
//class VerificationRequestResourceIT {
//
//    private static final String ENTITY_API_URL = "/api/verification-requests";
//    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
//
//    private static Random random = new Random();
//    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
//
//    @Autowired
//    private ObjectMapper om;
//
//    @Autowired
//    private VerificationRequestRepository verificationRequestRepository;
//
//    @Autowired
//    private EntityManager em;
//
//    @Autowired
//    private WebTestClient webTestClient;
//
//    private VerificationRequest verificationRequest;
//
//    private VerificationRequest insertedVerificationRequest;
//
//    @Autowired
//    private ProfileRepository profileRepository;
//
//    @Autowired
//    private FileObjectRepository fileObjectRepository;
//
//    /**
//     * Create an entity for this test.
//     *
//     * This is a static method, as tests for other entities might also need it,
//     * if they test an entity which requires the current entity.
//     */
//    public static VerificationRequest createEntity() {
//        return new VerificationRequest();
//    }
//
//    /**
//     * Create an updated entity for this test.
//     *
//     * This is a static method, as tests for other entities might also need it,
//     * if they test an entity which requires the current entity.
//     */
//    public static VerificationRequest createUpdatedEntity() {
//        return new VerificationRequest();
//    }
//
//    public static void deleteEntities(EntityManager em) {
//        try {
//            em.deleteAll(VerificationRequest.class).block();
//        } catch (Exception e) {
//            // It can fail, if other entities are still referring this - it will be removed later.
//        }
//    }
//
//    @BeforeEach
//    void initTest() {
//        verificationRequest = createEntity();
//    }
//
//    @AfterEach
//    void cleanup() {
//        if (insertedVerificationRequest != null) {
//            verificationRequestRepository.delete(insertedVerificationRequest).block();
//            insertedVerificationRequest = null;
//        }
//        deleteEntities(em);
//    }
//
//    @Test
//    void createVerificationRequest() throws Exception {
//        long databaseSizeBeforeCreate = getRepositoryCount();
//        // Create the VerificationRequest
//        var returnedVerificationRequest = webTestClient
//            .post()
//            .uri(ENTITY_API_URL)
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(om.writeValueAsBytes(verificationRequest))
//            .exchange()
//            .expectStatus()
//            .isCreated()
//            .expectBody(VerificationRequest.class)
//            .returnResult()
//            .getResponseBody();
//
//        // Validate the VerificationRequest in the database
//        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
//        assertVerificationRequestUpdatableFieldsEquals(
//            returnedVerificationRequest,
//            getPersistedVerificationRequest(returnedVerificationRequest)
//        );
//
//        insertedVerificationRequest = returnedVerificationRequest;
//    }
//
//    @Test
//    void createVerificationRequestWithExistingId() throws Exception {
//        // Create the VerificationRequest with an existing ID
//        verificationRequest.setId(1L);
//
//        long databaseSizeBeforeCreate = getRepositoryCount();
//
//        // An entity with an existing ID cannot be created, so this API call must fail
//        webTestClient
//            .post()
//            .uri(ENTITY_API_URL)
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(om.writeValueAsBytes(verificationRequest))
//            .exchange()
//            .expectStatus()
//            .isBadRequest();
//
//        // Validate the VerificationRequest in the database
//        assertSameRepositoryCount(databaseSizeBeforeCreate);
//    }
//
//    @Test
//    void getAllVerificationRequests() {
//        // Initialize the database
//        insertedVerificationRequest = verificationRequestRepository.save(verificationRequest).block();
//
//        // Get all the verificationRequestList
//        webTestClient
//            .get()
//            .uri(ENTITY_API_URL + "?sort=id,desc")
//            .accept(MediaType.APPLICATION_JSON)
//            .exchange()
//            .expectStatus()
//            .isOk()
//            .expectHeader()
//            .contentType(MediaType.APPLICATION_JSON)
//            .expectBody()
//            .jsonPath("$.[*].id")
//            .value(hasItem(verificationRequest.getId().intValue()));
//    }
//
//    @Test
//    void getVerificationRequest() {
//        // Initialize the database
//        insertedVerificationRequest = verificationRequestRepository.save(verificationRequest).block();
//
//        // Get the verificationRequest
//        webTestClient
//            .get()
//            .uri(ENTITY_API_URL_ID, verificationRequest.getId())
//            .accept(MediaType.APPLICATION_JSON)
//            .exchange()
//            .expectStatus()
//            .isOk()
//            .expectHeader()
//            .contentType(MediaType.APPLICATION_JSON)
//            .expectBody()
//            .jsonPath("$.id")
//            .value(is(verificationRequest.getId().intValue()));
//    }
//
//    @Test
//    void getVerificationRequestsByIdFiltering() {
//        // Initialize the database
//        insertedVerificationRequest = verificationRequestRepository.save(verificationRequest).block();
//
//        Long id = verificationRequest.getId();
//
//        defaultVerificationRequestFiltering("id.equals=" + id, "id.notEquals=" + id);
//
//        defaultVerificationRequestFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);
//
//        defaultVerificationRequestFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
//    }
//
//    @Test
//    void getAllVerificationRequestsByProfileIsEqualToSomething() {
//        Profile profile = ProfileResourceIT.createEntity();
//        profileRepository.save(profile).block();
//        Long profileId = profile.getId();
//        verificationRequest.setProfileId(profileId);
//        insertedVerificationRequest = verificationRequestRepository.save(verificationRequest).block();
//        // Get all the verificationRequestList where profile equals to profileId
//        defaultVerificationRequestShouldBeFound("profileId.equals=" + profileId);
//
//        // Get all the verificationRequestList where profile equals to (profileId + 1)
//        defaultVerificationRequestShouldNotBeFound("profileId.equals=" + (profileId + 1));
//    }
//
//    @Test
//    void getAllVerificationRequestsByFileObjectIsEqualToSomething() {
//        FileObject fileObject = FileObjectResourceIT.createEntity();
//        fileObjectRepository.save(fileObject).block();
//        Long fileObjectId = fileObject.getId();
//        verificationRequest.setFileObjectId(fileObjectId);
//        insertedVerificationRequest = verificationRequestRepository.save(verificationRequest).block();
//        // Get all the verificationRequestList where fileObject equals to fileObjectId
//        defaultVerificationRequestShouldBeFound("fileObjectId.equals=" + fileObjectId);
//
//        // Get all the verificationRequestList where fileObject equals to (fileObjectId + 1)
//        defaultVerificationRequestShouldNotBeFound("fileObjectId.equals=" + (fileObjectId + 1));
//    }
//
//    private void defaultVerificationRequestFiltering(String shouldBeFound, String shouldNotBeFound) {
//        defaultVerificationRequestShouldBeFound(shouldBeFound);
//        defaultVerificationRequestShouldNotBeFound(shouldNotBeFound);
//    }
//
//    /**
//     * Executes the search, and checks that the default entity is returned.
//     */
//    private void defaultVerificationRequestShouldBeFound(String filter) {
//        webTestClient
//            .get()
//            .uri(ENTITY_API_URL + "?sort=id,desc&" + filter)
//            .accept(MediaType.APPLICATION_JSON)
//            .exchange()
//            .expectStatus()
//            .isOk()
//            .expectHeader()
//            .contentType(MediaType.APPLICATION_JSON)
//            .expectBody()
//            .jsonPath("$.[*].id")
//            .value(hasItem(verificationRequest.getId().intValue()));
//
//        // Check, that the count call also returns 1
//        webTestClient
//            .get()
//            .uri(ENTITY_API_URL + "/count?sort=id,desc&" + filter)
//            .accept(MediaType.APPLICATION_JSON)
//            .exchange()
//            .expectStatus()
//            .isOk()
//            .expectHeader()
//            .contentType(MediaType.APPLICATION_JSON)
//            .expectBody()
//            .jsonPath("$")
//            .value(is(1));
//    }
//
//    /**
//     * Executes the search, and checks that the default entity is not returned.
//     */
//    private void defaultVerificationRequestShouldNotBeFound(String filter) {
//        webTestClient
//            .get()
//            .uri(ENTITY_API_URL + "?sort=id,desc&" + filter)
//            .accept(MediaType.APPLICATION_JSON)
//            .exchange()
//            .expectStatus()
//            .isOk()
//            .expectHeader()
//            .contentType(MediaType.APPLICATION_JSON)
//            .expectBody()
//            .jsonPath("$")
//            .isArray()
//            .jsonPath("$")
//            .isEmpty();
//
//        // Check, that the count call also returns 0
//        webTestClient
//            .get()
//            .uri(ENTITY_API_URL + "/count?sort=id,desc&" + filter)
//            .accept(MediaType.APPLICATION_JSON)
//            .exchange()
//            .expectStatus()
//            .isOk()
//            .expectHeader()
//            .contentType(MediaType.APPLICATION_JSON)
//            .expectBody()
//            .jsonPath("$")
//            .value(is(0));
//    }
//
//    @Test
//    void getNonExistingVerificationRequest() {
//        // Get the verificationRequest
//        webTestClient
//            .get()
//            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
//            .accept(MediaType.APPLICATION_PROBLEM_JSON)
//            .exchange()
//            .expectStatus()
//            .isNotFound();
//    }
//
//    @Test
//    void putExistingVerificationRequest() throws Exception {
//        // Initialize the database
//        insertedVerificationRequest = verificationRequestRepository.save(verificationRequest).block();
//
//        long databaseSizeBeforeUpdate = getRepositoryCount();
//
//        // Update the verificationRequest
//        VerificationRequest updatedVerificationRequest = verificationRequestRepository.findById(verificationRequest.getId()).block();
//
//        webTestClient
//            .put()
//            .uri(ENTITY_API_URL_ID, updatedVerificationRequest.getId())
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(om.writeValueAsBytes(updatedVerificationRequest))
//            .exchange()
//            .expectStatus()
//            .isOk();
//
//        // Validate the VerificationRequest in the database
//        assertSameRepositoryCount(databaseSizeBeforeUpdate);
//        assertPersistedVerificationRequestToMatchAllProperties(updatedVerificationRequest);
//    }
//
//    @Test
//    void putNonExistingVerificationRequest() throws Exception {
//        long databaseSizeBeforeUpdate = getRepositoryCount();
//        verificationRequest.setId(longCount.incrementAndGet());
//
//        // If the entity doesn't have an ID, it will throw BadRequestAlertException
//        webTestClient
//            .put()
//            .uri(ENTITY_API_URL_ID, verificationRequest.getId())
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(om.writeValueAsBytes(verificationRequest))
//            .exchange()
//            .expectStatus()
//            .isBadRequest();
//
//        // Validate the VerificationRequest in the database
//        assertSameRepositoryCount(databaseSizeBeforeUpdate);
//    }
//
//    @Test
//    void putWithIdMismatchVerificationRequest() throws Exception {
//        long databaseSizeBeforeUpdate = getRepositoryCount();
//        verificationRequest.setId(longCount.incrementAndGet());
//
//        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
//        webTestClient
//            .put()
//            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(om.writeValueAsBytes(verificationRequest))
//            .exchange()
//            .expectStatus()
//            .isBadRequest();
//
//        // Validate the VerificationRequest in the database
//        assertSameRepositoryCount(databaseSizeBeforeUpdate);
//    }
//
//    @Test
//    void putWithMissingIdPathParamVerificationRequest() throws Exception {
//        long databaseSizeBeforeUpdate = getRepositoryCount();
//        verificationRequest.setId(longCount.incrementAndGet());
//
//        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
//        webTestClient
//            .put()
//            .uri(ENTITY_API_URL)
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(om.writeValueAsBytes(verificationRequest))
//            .exchange()
//            .expectStatus()
//            .isEqualTo(405);
//
//        // Validate the VerificationRequest in the database
//        assertSameRepositoryCount(databaseSizeBeforeUpdate);
//    }
//
//    @Test
//    void partialUpdateVerificationRequestWithPatch() throws Exception {
//        // Initialize the database
//        insertedVerificationRequest = verificationRequestRepository.save(verificationRequest).block();
//
//        long databaseSizeBeforeUpdate = getRepositoryCount();
//
//        // Update the verificationRequest using partial update
//        VerificationRequest partialUpdatedVerificationRequest = new VerificationRequest();
//        partialUpdatedVerificationRequest.setId(verificationRequest.getId());
//
//        webTestClient
//            .patch()
//            .uri(ENTITY_API_URL_ID, partialUpdatedVerificationRequest.getId())
//            .contentType(MediaType.valueOf("application/merge-patch+json"))
//            .bodyValue(om.writeValueAsBytes(partialUpdatedVerificationRequest))
//            .exchange()
//            .expectStatus()
//            .isOk();
//
//        // Validate the VerificationRequest in the database
//
//        assertSameRepositoryCount(databaseSizeBeforeUpdate);
//        assertVerificationRequestUpdatableFieldsEquals(
//            createUpdateProxyForBean(partialUpdatedVerificationRequest, verificationRequest),
//            getPersistedVerificationRequest(verificationRequest)
//        );
//    }
//
//    @Test
//    void fullUpdateVerificationRequestWithPatch() throws Exception {
//        // Initialize the database
//        insertedVerificationRequest = verificationRequestRepository.save(verificationRequest).block();
//
//        long databaseSizeBeforeUpdate = getRepositoryCount();
//
//        // Update the verificationRequest using partial update
//        VerificationRequest partialUpdatedVerificationRequest = new VerificationRequest();
//        partialUpdatedVerificationRequest.setId(verificationRequest.getId());
//
//        webTestClient
//            .patch()
//            .uri(ENTITY_API_URL_ID, partialUpdatedVerificationRequest.getId())
//            .contentType(MediaType.valueOf("application/merge-patch+json"))
//            .bodyValue(om.writeValueAsBytes(partialUpdatedVerificationRequest))
//            .exchange()
//            .expectStatus()
//            .isOk();
//
//        // Validate the VerificationRequest in the database
//
//        assertSameRepositoryCount(databaseSizeBeforeUpdate);
//        assertVerificationRequestUpdatableFieldsEquals(
//            partialUpdatedVerificationRequest,
//            getPersistedVerificationRequest(partialUpdatedVerificationRequest)
//        );
//    }
//
//    @Test
//    void patchNonExistingVerificationRequest() throws Exception {
//        long databaseSizeBeforeUpdate = getRepositoryCount();
//        verificationRequest.setId(longCount.incrementAndGet());
//
//        // If the entity doesn't have an ID, it will throw BadRequestAlertException
//        webTestClient
//            .patch()
//            .uri(ENTITY_API_URL_ID, verificationRequest.getId())
//            .contentType(MediaType.valueOf("application/merge-patch+json"))
//            .bodyValue(om.writeValueAsBytes(verificationRequest))
//            .exchange()
//            .expectStatus()
//            .isBadRequest();
//
//        // Validate the VerificationRequest in the database
//        assertSameRepositoryCount(databaseSizeBeforeUpdate);
//    }
//
//    @Test
//    void patchWithIdMismatchVerificationRequest() throws Exception {
//        long databaseSizeBeforeUpdate = getRepositoryCount();
//        verificationRequest.setId(longCount.incrementAndGet());
//
//        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
//        webTestClient
//            .patch()
//            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
//            .contentType(MediaType.valueOf("application/merge-patch+json"))
//            .bodyValue(om.writeValueAsBytes(verificationRequest))
//            .exchange()
//            .expectStatus()
//            .isBadRequest();
//
//        // Validate the VerificationRequest in the database
//        assertSameRepositoryCount(databaseSizeBeforeUpdate);
//    }
//
//    @Test
//    void patchWithMissingIdPathParamVerificationRequest() throws Exception {
//        long databaseSizeBeforeUpdate = getRepositoryCount();
//        verificationRequest.setId(longCount.incrementAndGet());
//
//        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
//        webTestClient
//            .patch()
//            .uri(ENTITY_API_URL)
//            .contentType(MediaType.valueOf("application/merge-patch+json"))
//            .bodyValue(om.writeValueAsBytes(verificationRequest))
//            .exchange()
//            .expectStatus()
//            .isEqualTo(405);
//
//        // Validate the VerificationRequest in the database
//        assertSameRepositoryCount(databaseSizeBeforeUpdate);
//    }
//
//    @Test
//    void deleteVerificationRequest() {
//        // Initialize the database
//        insertedVerificationRequest = verificationRequestRepository.save(verificationRequest).block();
//
//        long databaseSizeBeforeDelete = getRepositoryCount();
//
//        // Delete the verificationRequest
//        webTestClient
//            .delete()
//            .uri(ENTITY_API_URL_ID, verificationRequest.getId())
//            .accept(MediaType.APPLICATION_JSON)
//            .exchange()
//            .expectStatus()
//            .isNoContent();
//
//        // Validate the database contains one less item
//        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
//    }
//
//    protected long getRepositoryCount() {
//        return verificationRequestRepository.count().block();
//    }
//
//    protected void assertIncrementedRepositoryCount(long countBefore) {
//        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
//    }
//
//    protected void assertDecrementedRepositoryCount(long countBefore) {
//        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
//    }
//
//    protected void assertSameRepositoryCount(long countBefore) {
//        assertThat(countBefore).isEqualTo(getRepositoryCount());
//    }
//
//    protected VerificationRequest getPersistedVerificationRequest(VerificationRequest verificationRequest) {
//        return verificationRequestRepository.findById(verificationRequest.getId()).block();
//    }
//
//    protected void assertPersistedVerificationRequestToMatchAllProperties(VerificationRequest expectedVerificationRequest) {
//        // Test fails because reactive api returns an empty object instead of null
//        // assertVerificationRequestAllPropertiesEquals(expectedVerificationRequest, getPersistedVerificationRequest(expectedVerificationRequest));
//        assertVerificationRequestUpdatableFieldsEquals(
//            expectedVerificationRequest,
//            getPersistedVerificationRequest(expectedVerificationRequest)
//        );
//    }
//
//    protected void assertPersistedVerificationRequestToMatchUpdatableProperties(VerificationRequest expectedVerificationRequest) {
//        // Test fails because reactive api returns an empty object instead of null
//        // assertVerificationRequestAllUpdatablePropertiesEquals(expectedVerificationRequest, getPersistedVerificationRequest(expectedVerificationRequest));
//        assertVerificationRequestUpdatableFieldsEquals(
//            expectedVerificationRequest,
//            getPersistedVerificationRequest(expectedVerificationRequest)
//        );
//    }
//}
