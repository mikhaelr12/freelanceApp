package com.freelance.app.web.rest;

import static com.freelance.app.domain.FavoriteOfferAsserts.*;
import static com.freelance.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.freelance.app.IntegrationTest;
import com.freelance.app.domain.FavoriteOffer;
import com.freelance.app.domain.Offer;
import com.freelance.app.domain.Profile;
import com.freelance.app.repository.EntityManager;
import com.freelance.app.repository.FavoriteOfferRepository;
import com.freelance.app.repository.OfferRepository;
import com.freelance.app.repository.ProfileRepository;
import com.freelance.app.service.dto.FavoriteOfferDTO;
import com.freelance.app.service.mapper.FavoriteOfferMapper;
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
 * Integration tests for the {@link FavoriteOfferResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class FavoriteOfferResourceIT {

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/favorite-offers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private FavoriteOfferRepository favoriteOfferRepository;

    @Autowired
    private FavoriteOfferMapper favoriteOfferMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private FavoriteOffer favoriteOffer;

    private FavoriteOffer insertedFavoriteOffer;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private OfferRepository offerRepository;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FavoriteOffer createEntity() {
        return new FavoriteOffer().createdAt(DEFAULT_CREATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FavoriteOffer createUpdatedEntity() {
        return new FavoriteOffer().createdAt(UPDATED_CREATED_AT);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(FavoriteOffer.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        favoriteOffer = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedFavoriteOffer != null) {
            favoriteOfferRepository.delete(insertedFavoriteOffer).block();
            insertedFavoriteOffer = null;
        }
        deleteEntities(em);
    }

    @Test
    void createFavoriteOffer() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the FavoriteOffer
        FavoriteOfferDTO favoriteOfferDTO = favoriteOfferMapper.toDto(favoriteOffer);
        var returnedFavoriteOfferDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(favoriteOfferDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(FavoriteOfferDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the FavoriteOffer in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedFavoriteOffer = favoriteOfferMapper.toEntity(returnedFavoriteOfferDTO);
        assertFavoriteOfferUpdatableFieldsEquals(returnedFavoriteOffer, getPersistedFavoriteOffer(returnedFavoriteOffer));

        insertedFavoriteOffer = returnedFavoriteOffer;
    }

    @Test
    void createFavoriteOfferWithExistingId() throws Exception {
        // Create the FavoriteOffer with an existing ID
        favoriteOffer.setId(1L);
        FavoriteOfferDTO favoriteOfferDTO = favoriteOfferMapper.toDto(favoriteOffer);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(favoriteOfferDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the FavoriteOffer in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        favoriteOffer.setCreatedAt(null);

        // Create the FavoriteOffer, which fails.
        FavoriteOfferDTO favoriteOfferDTO = favoriteOfferMapper.toDto(favoriteOffer);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(favoriteOfferDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllFavoriteOffers() {
        // Initialize the database
        insertedFavoriteOffer = favoriteOfferRepository.save(favoriteOffer).block();

        // Get all the favoriteOfferList
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
            .value(hasItem(favoriteOffer.getId().intValue()))
            .jsonPath("$.[*].createdAt")
            .value(hasItem(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    void getFavoriteOffer() {
        // Initialize the database
        insertedFavoriteOffer = favoriteOfferRepository.save(favoriteOffer).block();

        // Get the favoriteOffer
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, favoriteOffer.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(favoriteOffer.getId().intValue()))
            .jsonPath("$.createdAt")
            .value(is(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    void getFavoriteOffersByIdFiltering() {
        // Initialize the database
        insertedFavoriteOffer = favoriteOfferRepository.save(favoriteOffer).block();

        Long id = favoriteOffer.getId();

        defaultFavoriteOfferFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultFavoriteOfferFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultFavoriteOfferFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllFavoriteOffersByCreatedAtIsEqualToSomething() {
        // Initialize the database
        insertedFavoriteOffer = favoriteOfferRepository.save(favoriteOffer).block();

        // Get all the favoriteOfferList where createdAt equals to
        defaultFavoriteOfferFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    void getAllFavoriteOffersByCreatedAtIsInShouldWork() {
        // Initialize the database
        insertedFavoriteOffer = favoriteOfferRepository.save(favoriteOffer).block();

        // Get all the favoriteOfferList where createdAt in
        defaultFavoriteOfferFiltering(
            "createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT,
            "createdAt.in=" + UPDATED_CREATED_AT
        );
    }

    @Test
    void getAllFavoriteOffersByCreatedAtIsNullOrNotNull() {
        // Initialize the database
        insertedFavoriteOffer = favoriteOfferRepository.save(favoriteOffer).block();

        // Get all the favoriteOfferList where createdAt is not null
        defaultFavoriteOfferFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    void getAllFavoriteOffersByProfileIsEqualToSomething() {
        Profile profile = ProfileResourceIT.createEntity();
        profileRepository.save(profile).block();
        Long profileId = profile.getId();
        favoriteOffer.setProfileId(profileId);
        insertedFavoriteOffer = favoriteOfferRepository.save(favoriteOffer).block();
        // Get all the favoriteOfferList where profile equals to profileId
        defaultFavoriteOfferShouldBeFound("profileId.equals=" + profileId);

        // Get all the favoriteOfferList where profile equals to (profileId + 1)
        defaultFavoriteOfferShouldNotBeFound("profileId.equals=" + (profileId + 1));
    }

    @Test
    void getAllFavoriteOffersByOfferIsEqualToSomething() {
        Offer offer = OfferResourceIT.createEntity();
        offerRepository.save(offer).block();
        Long offerId = offer.getId();
        favoriteOffer.setOfferId(offerId);
        insertedFavoriteOffer = favoriteOfferRepository.save(favoriteOffer).block();
        // Get all the favoriteOfferList where offer equals to offerId
        defaultFavoriteOfferShouldBeFound("offerId.equals=" + offerId);

        // Get all the favoriteOfferList where offer equals to (offerId + 1)
        defaultFavoriteOfferShouldNotBeFound("offerId.equals=" + (offerId + 1));
    }

    private void defaultFavoriteOfferFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultFavoriteOfferShouldBeFound(shouldBeFound);
        defaultFavoriteOfferShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultFavoriteOfferShouldBeFound(String filter) {
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
            .value(hasItem(favoriteOffer.getId().intValue()))
            .jsonPath("$.[*].createdAt")
            .value(hasItem(DEFAULT_CREATED_AT.toString()));

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
    private void defaultFavoriteOfferShouldNotBeFound(String filter) {
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
    void getNonExistingFavoriteOffer() {
        // Get the favoriteOffer
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingFavoriteOffer() throws Exception {
        // Initialize the database
        insertedFavoriteOffer = favoriteOfferRepository.save(favoriteOffer).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the favoriteOffer
        FavoriteOffer updatedFavoriteOffer = favoriteOfferRepository.findById(favoriteOffer.getId()).block();
        updatedFavoriteOffer.createdAt(UPDATED_CREATED_AT);
        FavoriteOfferDTO favoriteOfferDTO = favoriteOfferMapper.toDto(updatedFavoriteOffer);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, favoriteOfferDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(favoriteOfferDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the FavoriteOffer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedFavoriteOfferToMatchAllProperties(updatedFavoriteOffer);
    }

    @Test
    void putNonExistingFavoriteOffer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        favoriteOffer.setId(longCount.incrementAndGet());

        // Create the FavoriteOffer
        FavoriteOfferDTO favoriteOfferDTO = favoriteOfferMapper.toDto(favoriteOffer);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, favoriteOfferDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(favoriteOfferDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the FavoriteOffer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchFavoriteOffer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        favoriteOffer.setId(longCount.incrementAndGet());

        // Create the FavoriteOffer
        FavoriteOfferDTO favoriteOfferDTO = favoriteOfferMapper.toDto(favoriteOffer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(favoriteOfferDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the FavoriteOffer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamFavoriteOffer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        favoriteOffer.setId(longCount.incrementAndGet());

        // Create the FavoriteOffer
        FavoriteOfferDTO favoriteOfferDTO = favoriteOfferMapper.toDto(favoriteOffer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(favoriteOfferDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the FavoriteOffer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateFavoriteOfferWithPatch() throws Exception {
        // Initialize the database
        insertedFavoriteOffer = favoriteOfferRepository.save(favoriteOffer).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the favoriteOffer using partial update
        FavoriteOffer partialUpdatedFavoriteOffer = new FavoriteOffer();
        partialUpdatedFavoriteOffer.setId(favoriteOffer.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedFavoriteOffer.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedFavoriteOffer))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the FavoriteOffer in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFavoriteOfferUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedFavoriteOffer, favoriteOffer),
            getPersistedFavoriteOffer(favoriteOffer)
        );
    }

    @Test
    void fullUpdateFavoriteOfferWithPatch() throws Exception {
        // Initialize the database
        insertedFavoriteOffer = favoriteOfferRepository.save(favoriteOffer).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the favoriteOffer using partial update
        FavoriteOffer partialUpdatedFavoriteOffer = new FavoriteOffer();
        partialUpdatedFavoriteOffer.setId(favoriteOffer.getId());

        partialUpdatedFavoriteOffer.createdAt(UPDATED_CREATED_AT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedFavoriteOffer.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedFavoriteOffer))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the FavoriteOffer in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFavoriteOfferUpdatableFieldsEquals(partialUpdatedFavoriteOffer, getPersistedFavoriteOffer(partialUpdatedFavoriteOffer));
    }

    @Test
    void patchNonExistingFavoriteOffer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        favoriteOffer.setId(longCount.incrementAndGet());

        // Create the FavoriteOffer
        FavoriteOfferDTO favoriteOfferDTO = favoriteOfferMapper.toDto(favoriteOffer);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, favoriteOfferDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(favoriteOfferDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the FavoriteOffer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchFavoriteOffer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        favoriteOffer.setId(longCount.incrementAndGet());

        // Create the FavoriteOffer
        FavoriteOfferDTO favoriteOfferDTO = favoriteOfferMapper.toDto(favoriteOffer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(favoriteOfferDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the FavoriteOffer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamFavoriteOffer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        favoriteOffer.setId(longCount.incrementAndGet());

        // Create the FavoriteOffer
        FavoriteOfferDTO favoriteOfferDTO = favoriteOfferMapper.toDto(favoriteOffer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(favoriteOfferDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the FavoriteOffer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteFavoriteOffer() {
        // Initialize the database
        insertedFavoriteOffer = favoriteOfferRepository.save(favoriteOffer).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the favoriteOffer
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, favoriteOffer.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return favoriteOfferRepository.count().block();
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

    protected FavoriteOffer getPersistedFavoriteOffer(FavoriteOffer favoriteOffer) {
        return favoriteOfferRepository.findById(favoriteOffer.getId()).block();
    }

    protected void assertPersistedFavoriteOfferToMatchAllProperties(FavoriteOffer expectedFavoriteOffer) {
        // Test fails because reactive api returns an empty object instead of null
        // assertFavoriteOfferAllPropertiesEquals(expectedFavoriteOffer, getPersistedFavoriteOffer(expectedFavoriteOffer));
        assertFavoriteOfferUpdatableFieldsEquals(expectedFavoriteOffer, getPersistedFavoriteOffer(expectedFavoriteOffer));
    }

    protected void assertPersistedFavoriteOfferToMatchUpdatableProperties(FavoriteOffer expectedFavoriteOffer) {
        // Test fails because reactive api returns an empty object instead of null
        // assertFavoriteOfferAllUpdatablePropertiesEquals(expectedFavoriteOffer, getPersistedFavoriteOffer(expectedFavoriteOffer));
        assertFavoriteOfferUpdatableFieldsEquals(expectedFavoriteOffer, getPersistedFavoriteOffer(expectedFavoriteOffer));
    }
}
