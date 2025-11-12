//package com.freelance.app.web.rest;
//
//import static com.freelance.app.domain.ProfileReviewAsserts.*;
//import static com.freelance.app.web.rest.TestUtil.createUpdateProxyForBean;
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.hamcrest.Matchers.hasItem;
//import static org.hamcrest.Matchers.is;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.freelance.app.IntegrationTest;
//import com.freelance.app.domain.Profile;
//import com.freelance.app.domain.ProfileReview;
//import com.freelance.app.repository.EntityManager;
//import com.freelance.app.repository.ProfileRepository;
//import com.freelance.app.repository.ProfileRepository;
//import com.freelance.app.repository.ProfileReviewRepository;
//import com.freelance.app.service.dto.ProfileReviewDTO;
//import com.freelance.app.service.mapper.ProfileReviewMapper;
//import java.time.Instant;
//import java.time.temporal.ChronoUnit;
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
// * Integration tests for the {@link ProfileReviewResource} REST controller.
// */
//@IntegrationTest
//@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
//@WithMockUser
//class ProfileReviewResourceIT {
//
//    private static final String DEFAULT_TEXT = "AAAAAAAAAA";
//    private static final String UPDATED_TEXT = "BBBBBBBBBB";
//
//    private static final Double DEFAULT_RATING = 1D;
//    private static final Double UPDATED_RATING = 2D;
//    private static final Double SMALLER_RATING = 1D - 1D;
//
//    private static final Instant DEFAULT_CREATED_DATE = Instant.ofEpochMilli(0L);
//    private static final Instant UPDATED_CREATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);
//
//    private static final Instant DEFAULT_LAST_MODIFIED_DATE = Instant.ofEpochMilli(0L);
//    private static final Instant UPDATED_LAST_MODIFIED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);
//
//    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
//    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";
//
//    private static final String DEFAULT_LAST_MODIFIED_BY = "AAAAAAAAAA";
//    private static final String UPDATED_LAST_MODIFIED_BY = "BBBBBBBBBB";
//
//    private static final String ENTITY_API_URL = "/api/profile-reviews";
//    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
//
//    private static Random random = new Random();
//    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
//
//    @Autowired
//    private ObjectMapper om;
//
//    @Autowired
//    private ProfileReviewRepository profileReviewRepository;
//
//    @Autowired
//    private ProfileReviewMapper profileReviewMapper;
//
//    @Autowired
//    private EntityManager em;
//
//    @Autowired
//    private WebTestClient webTestClient;
//
//    private ProfileReview profileReview;
//
//    private ProfileReview insertedProfileReview;
//
//    @Autowired
//    private ProfileRepository profileRepository;
//
//    /**
//     * Create an entity for this test.
//     *
//     * This is a static method, as tests for other entities might also need it,
//     * if they test an entity which requires the current entity.
//     */
//    public static ProfileReview createEntity() {
//        return new ProfileReview()
//            .text(DEFAULT_TEXT)
//            .rating(DEFAULT_RATING)
//            .createdDate(DEFAULT_CREATED_DATE)
//            .lastModifiedDate(DEFAULT_LAST_MODIFIED_DATE)
//            .createdBy(DEFAULT_CREATED_BY)
//            .lastModifiedBy(DEFAULT_LAST_MODIFIED_BY);
//    }
//
//    /**
//     * Create an updated entity for this test.
//     *
//     * This is a static method, as tests for other entities might also need it,
//     * if they test an entity which requires the current entity.
//     */
//    public static ProfileReview createUpdatedEntity() {
//        return new ProfileReview()
//            .text(UPDATED_TEXT)
//            .rating(UPDATED_RATING)
//            .createdDate(UPDATED_CREATED_DATE)
//            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
//            .createdBy(UPDATED_CREATED_BY)
//            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY);
//    }
//
//    public static void deleteEntities(EntityManager em) {
//        try {
//            em.deleteAll(ProfileReview.class).block();
//        } catch (Exception e) {
//            // It can fail, if other entities are still referring this - it will be removed later.
//        }
//    }
//
//    @BeforeEach
//    void initTest() {
//        profileReview = createEntity();
//    }
//
//    @AfterEach
//    void cleanup() {
//        if (insertedProfileReview != null) {
//            profileReviewRepository.delete(insertedProfileReview).block();
//            insertedProfileReview = null;
//        }
//        deleteEntities(em);
//    }
//
//    @Test
//    void createProfileReview() throws Exception {
//        long databaseSizeBeforeCreate = getRepositoryCount();
//        // Create the ProfileReview
//        ProfileReviewDTO profileReviewDTO = profileReviewMapper.toDto(profileReview);
//        var returnedProfileReviewDTO = webTestClient
//            .post()
//            .uri(ENTITY_API_URL)
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(om.writeValueAsBytes(profileReviewDTO))
//            .exchange()
//            .expectStatus()
//            .isCreated()
//            .expectBody(ProfileReviewDTO.class)
//            .returnResult()
//            .getResponseBody();
//
//        // Validate the ProfileReview in the database
//        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
//        var returnedProfileReview = profileReviewMapper.toEntity(returnedProfileReviewDTO);
//        assertProfileReviewUpdatableFieldsEquals(returnedProfileReview, getPersistedProfileReview(returnedProfileReview));
//
//        insertedProfileReview = returnedProfileReview;
//    }
//
//    @Test
//    void createProfileReviewWithExistingId() throws Exception {
//        // Create the ProfileReview with an existing ID
//        profileReview.setId(1L);
//        ProfileReviewDTO profileReviewDTO = profileReviewMapper.toDto(profileReview);
//
//        long databaseSizeBeforeCreate = getRepositoryCount();
//
//        // An entity with an existing ID cannot be created, so this API call must fail
//        webTestClient
//            .post()
//            .uri(ENTITY_API_URL)
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(om.writeValueAsBytes(profileReviewDTO))
//            .exchange()
//            .expectStatus()
//            .isBadRequest();
//
//        // Validate the ProfileReview in the database
//        assertSameRepositoryCount(databaseSizeBeforeCreate);
//    }
//
//    @Test
//    void checkRatingIsRequired() throws Exception {
//        long databaseSizeBeforeTest = getRepositoryCount();
//        // set the field null
//        profileReview.setRating(null);
//
//        // Create the ProfileReview, which fails.
//        ProfileReviewDTO profileReviewDTO = profileReviewMapper.toDto(profileReview);
//
//        webTestClient
//            .post()
//            .uri(ENTITY_API_URL)
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(om.writeValueAsBytes(profileReviewDTO))
//            .exchange()
//            .expectStatus()
//            .isBadRequest();
//
//        assertSameRepositoryCount(databaseSizeBeforeTest);
//    }
//
//    @Test
//    void checkCreatedDateIsRequired() throws Exception {
//        long databaseSizeBeforeTest = getRepositoryCount();
//        // set the field null
//        profileReview.setCreatedDate(null);
//
//        // Create the ProfileReview, which fails.
//        ProfileReviewDTO profileReviewDTO = profileReviewMapper.toDto(profileReview);
//
//        webTestClient
//            .post()
//            .uri(ENTITY_API_URL)
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(om.writeValueAsBytes(profileReviewDTO))
//            .exchange()
//            .expectStatus()
//            .isBadRequest();
//
//        assertSameRepositoryCount(databaseSizeBeforeTest);
//    }
//
//    @Test
//    void getAllProfileReviews() {
//        // Initialize the database
//        insertedProfileReview = profileReviewRepository.save(profileReview).block();
//
//        // Get all the profileReviewList
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
//            .value(hasItem(profileReview.getId().intValue()))
//            .jsonPath("$.[*].text")
//            .value(hasItem(DEFAULT_TEXT))
//            .jsonPath("$.[*].rating")
//            .value(hasItem(DEFAULT_RATING))
//            .jsonPath("$.[*].createdDate")
//            .value(hasItem(DEFAULT_CREATED_DATE.toString()))
//            .jsonPath("$.[*].lastModifiedDate")
//            .value(hasItem(DEFAULT_LAST_MODIFIED_DATE.toString()))
//            .jsonPath("$.[*].createdBy")
//            .value(hasItem(DEFAULT_CREATED_BY))
//            .jsonPath("$.[*].lastModifiedBy")
//            .value(hasItem(DEFAULT_LAST_MODIFIED_BY));
//    }
//
//    @Test
//    void getProfileReview() {
//        // Initialize the database
//        insertedProfileReview = profileReviewRepository.save(profileReview).block();
//
//        // Get the profileReview
//        webTestClient
//            .get()
//            .uri(ENTITY_API_URL_ID, profileReview.getId())
//            .accept(MediaType.APPLICATION_JSON)
//            .exchange()
//            .expectStatus()
//            .isOk()
//            .expectHeader()
//            .contentType(MediaType.APPLICATION_JSON)
//            .expectBody()
//            .jsonPath("$.id")
//            .value(is(profileReview.getId().intValue()))
//            .jsonPath("$.text")
//            .value(is(DEFAULT_TEXT))
//            .jsonPath("$.rating")
//            .value(is(DEFAULT_RATING))
//            .jsonPath("$.createdDate")
//            .value(is(DEFAULT_CREATED_DATE.toString()))
//            .jsonPath("$.lastModifiedDate")
//            .value(is(DEFAULT_LAST_MODIFIED_DATE.toString()))
//            .jsonPath("$.createdBy")
//            .value(is(DEFAULT_CREATED_BY))
//            .jsonPath("$.lastModifiedBy")
//            .value(is(DEFAULT_LAST_MODIFIED_BY));
//    }
//
//    @Test
//    void getProfileReviewsByIdFiltering() {
//        // Initialize the database
//        insertedProfileReview = profileReviewRepository.save(profileReview).block();
//
//        Long id = profileReview.getId();
//
//        defaultProfileReviewFiltering("id.equals=" + id, "id.notEquals=" + id);
//
//        defaultProfileReviewFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);
//
//        defaultProfileReviewFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
//    }
//
//    @Test
//    void getAllProfileReviewsByTextIsEqualToSomething() {
//        // Initialize the database
//        insertedProfileReview = profileReviewRepository.save(profileReview).block();
//
//        // Get all the profileReviewList where text equals to
//        defaultProfileReviewFiltering("text.equals=" + DEFAULT_TEXT, "text.equals=" + UPDATED_TEXT);
//    }
//
//    @Test
//    void getAllProfileReviewsByTextIsInShouldWork() {
//        // Initialize the database
//        insertedProfileReview = profileReviewRepository.save(profileReview).block();
//
//        // Get all the profileReviewList where text in
//        defaultProfileReviewFiltering("text.in=" + DEFAULT_TEXT + "," + UPDATED_TEXT, "text.in=" + UPDATED_TEXT);
//    }
//
//    @Test
//    void getAllProfileReviewsByTextIsNullOrNotNull() {
//        // Initialize the database
//        insertedProfileReview = profileReviewRepository.save(profileReview).block();
//
//        // Get all the profileReviewList where text is not null
//        defaultProfileReviewFiltering("text.specified=true", "text.specified=false");
//    }
//
//    @Test
//    void getAllProfileReviewsByTextContainsSomething() {
//        // Initialize the database
//        insertedProfileReview = profileReviewRepository.save(profileReview).block();
//
//        // Get all the profileReviewList where text contains
//        defaultProfileReviewFiltering("text.contains=" + DEFAULT_TEXT, "text.contains=" + UPDATED_TEXT);
//    }
//
//    @Test
//    void getAllProfileReviewsByTextNotContainsSomething() {
//        // Initialize the database
//        insertedProfileReview = profileReviewRepository.save(profileReview).block();
//
//        // Get all the profileReviewList where text does not contain
//        defaultProfileReviewFiltering("text.doesNotContain=" + UPDATED_TEXT, "text.doesNotContain=" + DEFAULT_TEXT);
//    }
//
//    @Test
//    void getAllProfileReviewsByRatingIsEqualToSomething() {
//        // Initialize the database
//        insertedProfileReview = profileReviewRepository.save(profileReview).block();
//
//        // Get all the profileReviewList where rating equals to
//        defaultProfileReviewFiltering("rating.equals=" + DEFAULT_RATING, "rating.equals=" + UPDATED_RATING);
//    }
//
//    @Test
//    void getAllProfileReviewsByRatingIsInShouldWork() {
//        // Initialize the database
//        insertedProfileReview = profileReviewRepository.save(profileReview).block();
//
//        // Get all the profileReviewList where rating in
//        defaultProfileReviewFiltering("rating.in=" + DEFAULT_RATING + "," + UPDATED_RATING, "rating.in=" + UPDATED_RATING);
//    }
//
//    @Test
//    void getAllProfileReviewsByRatingIsNullOrNotNull() {
//        // Initialize the database
//        insertedProfileReview = profileReviewRepository.save(profileReview).block();
//
//        // Get all the profileReviewList where rating is not null
//        defaultProfileReviewFiltering("rating.specified=true", "rating.specified=false");
//    }
//
//    @Test
//    void getAllProfileReviewsByRatingIsGreaterThanOrEqualToSomething() {
//        // Initialize the database
//        insertedProfileReview = profileReviewRepository.save(profileReview).block();
//
//        // Get all the profileReviewList where rating is greater than or equal to
//        defaultProfileReviewFiltering("rating.greaterThanOrEqual=" + DEFAULT_RATING, "rating.greaterThanOrEqual=" + (DEFAULT_RATING + 1));
//    }
//
//    @Test
//    void getAllProfileReviewsByRatingIsLessThanOrEqualToSomething() {
//        // Initialize the database
//        insertedProfileReview = profileReviewRepository.save(profileReview).block();
//
//        // Get all the profileReviewList where rating is less than or equal to
//        defaultProfileReviewFiltering("rating.lessThanOrEqual=" + DEFAULT_RATING, "rating.lessThanOrEqual=" + SMALLER_RATING);
//    }
//
//    @Test
//    void getAllProfileReviewsByRatingIsLessThanSomething() {
//        // Initialize the database
//        insertedProfileReview = profileReviewRepository.save(profileReview).block();
//
//        // Get all the profileReviewList where rating is less than
//        defaultProfileReviewFiltering("rating.lessThan=" + (DEFAULT_RATING + 1), "rating.lessThan=" + DEFAULT_RATING);
//    }
//
//    @Test
//    void getAllProfileReviewsByRatingIsGreaterThanSomething() {
//        // Initialize the database
//        insertedProfileReview = profileReviewRepository.save(profileReview).block();
//
//        // Get all the profileReviewList where rating is greater than
//        defaultProfileReviewFiltering("rating.greaterThan=" + SMALLER_RATING, "rating.greaterThan=" + DEFAULT_RATING);
//    }
//
//    @Test
//    void getAllProfileReviewsByCreatedDateIsEqualToSomething() {
//        // Initialize the database
//        insertedProfileReview = profileReviewRepository.save(profileReview).block();
//
//        // Get all the profileReviewList where createdDate equals to
//        defaultProfileReviewFiltering("createdDate.equals=" + DEFAULT_CREATED_DATE, "createdDate.equals=" + UPDATED_CREATED_DATE);
//    }
//
//    @Test
//    void getAllProfileReviewsByCreatedDateIsInShouldWork() {
//        // Initialize the database
//        insertedProfileReview = profileReviewRepository.save(profileReview).block();
//
//        // Get all the profileReviewList where createdDate in
//        defaultProfileReviewFiltering(
//            "createdDate.in=" + DEFAULT_CREATED_DATE + "," + UPDATED_CREATED_DATE,
//            "createdDate.in=" + UPDATED_CREATED_DATE
//        );
//    }
//
//    @Test
//    void getAllProfileReviewsByCreatedDateIsNullOrNotNull() {
//        // Initialize the database
//        insertedProfileReview = profileReviewRepository.save(profileReview).block();
//
//        // Get all the profileReviewList where createdDate is not null
//        defaultProfileReviewFiltering("createdDate.specified=true", "createdDate.specified=false");
//    }
//
//    @Test
//    void getAllProfileReviewsByLastModifiedDateIsEqualToSomething() {
//        // Initialize the database
//        insertedProfileReview = profileReviewRepository.save(profileReview).block();
//
//        // Get all the profileReviewList where lastModifiedDate equals to
//        defaultProfileReviewFiltering(
//            "lastModifiedDate.equals=" + DEFAULT_LAST_MODIFIED_DATE,
//            "lastModifiedDate.equals=" + UPDATED_LAST_MODIFIED_DATE
//        );
//    }
//
//    @Test
//    void getAllProfileReviewsByLastModifiedDateIsInShouldWork() {
//        // Initialize the database
//        insertedProfileReview = profileReviewRepository.save(profileReview).block();
//
//        // Get all the profileReviewList where lastModifiedDate in
//        defaultProfileReviewFiltering(
//            "lastModifiedDate.in=" + DEFAULT_LAST_MODIFIED_DATE + "," + UPDATED_LAST_MODIFIED_DATE,
//            "lastModifiedDate.in=" + UPDATED_LAST_MODIFIED_DATE
//        );
//    }
//
//    @Test
//    void getAllProfileReviewsByLastModifiedDateIsNullOrNotNull() {
//        // Initialize the database
//        insertedProfileReview = profileReviewRepository.save(profileReview).block();
//
//        // Get all the profileReviewList where lastModifiedDate is not null
//        defaultProfileReviewFiltering("lastModifiedDate.specified=true", "lastModifiedDate.specified=false");
//    }
//
//    @Test
//    void getAllProfileReviewsByCreatedByIsEqualToSomething() {
//        // Initialize the database
//        insertedProfileReview = profileReviewRepository.save(profileReview).block();
//
//        // Get all the profileReviewList where createdBy equals to
//        defaultProfileReviewFiltering("createdBy.equals=" + DEFAULT_CREATED_BY, "createdBy.equals=" + UPDATED_CREATED_BY);
//    }
//
//    @Test
//    void getAllProfileReviewsByCreatedByIsInShouldWork() {
//        // Initialize the database
//        insertedProfileReview = profileReviewRepository.save(profileReview).block();
//
//        // Get all the profileReviewList where createdBy in
//        defaultProfileReviewFiltering(
//            "createdBy.in=" + DEFAULT_CREATED_BY + "," + UPDATED_CREATED_BY,
//            "createdBy.in=" + UPDATED_CREATED_BY
//        );
//    }
//
//    @Test
//    void getAllProfileReviewsByCreatedByIsNullOrNotNull() {
//        // Initialize the database
//        insertedProfileReview = profileReviewRepository.save(profileReview).block();
//
//        // Get all the profileReviewList where createdBy is not null
//        defaultProfileReviewFiltering("createdBy.specified=true", "createdBy.specified=false");
//    }
//
//    @Test
//    void getAllProfileReviewsByCreatedByContainsSomething() {
//        // Initialize the database
//        insertedProfileReview = profileReviewRepository.save(profileReview).block();
//
//        // Get all the profileReviewList where createdBy contains
//        defaultProfileReviewFiltering("createdBy.contains=" + DEFAULT_CREATED_BY, "createdBy.contains=" + UPDATED_CREATED_BY);
//    }
//
//    @Test
//    void getAllProfileReviewsByCreatedByNotContainsSomething() {
//        // Initialize the database
//        insertedProfileReview = profileReviewRepository.save(profileReview).block();
//
//        // Get all the profileReviewList where createdBy does not contain
//        defaultProfileReviewFiltering("createdBy.doesNotContain=" + UPDATED_CREATED_BY, "createdBy.doesNotContain=" + DEFAULT_CREATED_BY);
//    }
//
//    @Test
//    void getAllProfileReviewsByLastModifiedByIsEqualToSomething() {
//        // Initialize the database
//        insertedProfileReview = profileReviewRepository.save(profileReview).block();
//
//        // Get all the profileReviewList where lastModifiedBy equals to
//        defaultProfileReviewFiltering(
//            "lastModifiedBy.equals=" + DEFAULT_LAST_MODIFIED_BY,
//            "lastModifiedBy.equals=" + UPDATED_LAST_MODIFIED_BY
//        );
//    }
//
//    @Test
//    void getAllProfileReviewsByLastModifiedByIsInShouldWork() {
//        // Initialize the database
//        insertedProfileReview = profileReviewRepository.save(profileReview).block();
//
//        // Get all the profileReviewList where lastModifiedBy in
//        defaultProfileReviewFiltering(
//            "lastModifiedBy.in=" + DEFAULT_LAST_MODIFIED_BY + "," + UPDATED_LAST_MODIFIED_BY,
//            "lastModifiedBy.in=" + UPDATED_LAST_MODIFIED_BY
//        );
//    }
//
//    @Test
//    void getAllProfileReviewsByLastModifiedByIsNullOrNotNull() {
//        // Initialize the database
//        insertedProfileReview = profileReviewRepository.save(profileReview).block();
//
//        // Get all the profileReviewList where lastModifiedBy is not null
//        defaultProfileReviewFiltering("lastModifiedBy.specified=true", "lastModifiedBy.specified=false");
//    }
//
//    @Test
//    void getAllProfileReviewsByLastModifiedByContainsSomething() {
//        // Initialize the database
//        insertedProfileReview = profileReviewRepository.save(profileReview).block();
//
//        // Get all the profileReviewList where lastModifiedBy contains
//        defaultProfileReviewFiltering(
//            "lastModifiedBy.contains=" + DEFAULT_LAST_MODIFIED_BY,
//            "lastModifiedBy.contains=" + UPDATED_LAST_MODIFIED_BY
//        );
//    }
//
//    @Test
//    void getAllProfileReviewsByLastModifiedByNotContainsSomething() {
//        // Initialize the database
//        insertedProfileReview = profileReviewRepository.save(profileReview).block();
//
//        // Get all the profileReviewList where lastModifiedBy does not contain
//        defaultProfileReviewFiltering(
//            "lastModifiedBy.doesNotContain=" + UPDATED_LAST_MODIFIED_BY,
//            "lastModifiedBy.doesNotContain=" + DEFAULT_LAST_MODIFIED_BY
//        );
//    }
//
//    @Test
//    void getAllProfileReviewsByReviewerIsEqualToSomething() {
//        Profile reviewer = ProfileResourceIT.createEntity();
//        profileRepository.save(reviewer).block();
//        Long reviewerId = reviewer.getId();
//        profileReview.setReviewerId(reviewerId);
//        insertedProfileReview = profileReviewRepository.save(profileReview).block();
//        // Get all the profileReviewList where reviewer equals to reviewerId
//        defaultProfileReviewShouldBeFound("reviewerId.equals=" + reviewerId);
//
//        // Get all the profileReviewList where reviewer equals to (reviewerId + 1)
//        defaultProfileReviewShouldNotBeFound("reviewerId.equals=" + (reviewerId + 1));
//    }
//
//    @Test
//    void getAllProfileReviewsByRevieweeIsEqualToSomething() {
//        Profile reviewee = ProfileResourceIT.createEntity();
//        profileRepository.save(reviewee).block();
//        Long revieweeId = reviewee.getId();
//        profileReview.setRevieweeId(revieweeId);
//        insertedProfileReview = profileReviewRepository.save(profileReview).block();
//        // Get all the profileReviewList where reviewee equals to revieweeId
//        defaultProfileReviewShouldBeFound("revieweeId.equals=" + revieweeId);
//
//        // Get all the profileReviewList where reviewee equals to (revieweeId + 1)
//        defaultProfileReviewShouldNotBeFound("revieweeId.equals=" + (revieweeId + 1));
//    }
//
//    private void defaultProfileReviewFiltering(String shouldBeFound, String shouldNotBeFound) {
//        defaultProfileReviewShouldBeFound(shouldBeFound);
//        defaultProfileReviewShouldNotBeFound(shouldNotBeFound);
//    }
//
//    /**
//     * Executes the search, and checks that the default entity is returned.
//     */
//    private void defaultProfileReviewShouldBeFound(String filter) {
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
//            .value(hasItem(profileReview.getId().intValue()))
//            .jsonPath("$.[*].text")
//            .value(hasItem(DEFAULT_TEXT))
//            .jsonPath("$.[*].rating")
//            .value(hasItem(DEFAULT_RATING))
//            .jsonPath("$.[*].createdDate")
//            .value(hasItem(DEFAULT_CREATED_DATE.toString()))
//            .jsonPath("$.[*].lastModifiedDate")
//            .value(hasItem(DEFAULT_LAST_MODIFIED_DATE.toString()))
//            .jsonPath("$.[*].createdBy")
//            .value(hasItem(DEFAULT_CREATED_BY))
//            .jsonPath("$.[*].lastModifiedBy")
//            .value(hasItem(DEFAULT_LAST_MODIFIED_BY));
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
//    private void defaultProfileReviewShouldNotBeFound(String filter) {
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
//    void getNonExistingProfileReview() {
//        // Get the profileReview
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
//    void putExistingProfileReview() throws Exception {
//        // Initialize the database
//        insertedProfileReview = profileReviewRepository.save(profileReview).block();
//
//        long databaseSizeBeforeUpdate = getRepositoryCount();
//
//        // Update the profileReview
//        ProfileReview updatedProfileReview = profileReviewRepository.findById(profileReview.getId()).block();
//        updatedProfileReview
//            .text(UPDATED_TEXT)
//            .rating(UPDATED_RATING)
//            .createdDate(UPDATED_CREATED_DATE)
//            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
//            .createdBy(UPDATED_CREATED_BY)
//            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY);
//        ProfileReviewDTO profileReviewDTO = profileReviewMapper.toDto(updatedProfileReview);
//
//        webTestClient
//            .put()
//            .uri(ENTITY_API_URL_ID, profileReviewDTO.getId())
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(om.writeValueAsBytes(profileReviewDTO))
//            .exchange()
//            .expectStatus()
//            .isOk();
//
//        // Validate the ProfileReview in the database
//        assertSameRepositoryCount(databaseSizeBeforeUpdate);
//        assertPersistedProfileReviewToMatchAllProperties(updatedProfileReview);
//    }
//
//    @Test
//    void putNonExistingProfileReview() throws Exception {
//        long databaseSizeBeforeUpdate = getRepositoryCount();
//        profileReview.setId(longCount.incrementAndGet());
//
//        // Create the ProfileReview
//        ProfileReviewDTO profileReviewDTO = profileReviewMapper.toDto(profileReview);
//
//        // If the entity doesn't have an ID, it will throw BadRequestAlertException
//        webTestClient
//            .put()
//            .uri(ENTITY_API_URL_ID, profileReviewDTO.getId())
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(om.writeValueAsBytes(profileReviewDTO))
//            .exchange()
//            .expectStatus()
//            .isBadRequest();
//
//        // Validate the ProfileReview in the database
//        assertSameRepositoryCount(databaseSizeBeforeUpdate);
//    }
//
//    @Test
//    void putWithIdMismatchProfileReview() throws Exception {
//        long databaseSizeBeforeUpdate = getRepositoryCount();
//        profileReview.setId(longCount.incrementAndGet());
//
//        // Create the ProfileReview
//        ProfileReviewDTO profileReviewDTO = profileReviewMapper.toDto(profileReview);
//
//        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
//        webTestClient
//            .put()
//            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(om.writeValueAsBytes(profileReviewDTO))
//            .exchange()
//            .expectStatus()
//            .isBadRequest();
//
//        // Validate the ProfileReview in the database
//        assertSameRepositoryCount(databaseSizeBeforeUpdate);
//    }
//
//    @Test
//    void putWithMissingIdPathParamProfileReview() throws Exception {
//        long databaseSizeBeforeUpdate = getRepositoryCount();
//        profileReview.setId(longCount.incrementAndGet());
//
//        // Create the ProfileReview
//        ProfileReviewDTO profileReviewDTO = profileReviewMapper.toDto(profileReview);
//
//        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
//        webTestClient
//            .put()
//            .uri(ENTITY_API_URL)
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(om.writeValueAsBytes(profileReviewDTO))
//            .exchange()
//            .expectStatus()
//            .isEqualTo(405);
//
//        // Validate the ProfileReview in the database
//        assertSameRepositoryCount(databaseSizeBeforeUpdate);
//    }
//
//    @Test
//    void partialUpdateProfileReviewWithPatch() throws Exception {
//        // Initialize the database
//        insertedProfileReview = profileReviewRepository.save(profileReview).block();
//
//        long databaseSizeBeforeUpdate = getRepositoryCount();
//
//        // Update the profileReview using partial update
//        ProfileReview partialUpdatedProfileReview = new ProfileReview();
//        partialUpdatedProfileReview.setId(profileReview.getId());
//
//        partialUpdatedProfileReview.rating(UPDATED_RATING).lastModifiedDate(UPDATED_LAST_MODIFIED_DATE);
//
//        webTestClient
//            .patch()
//            .uri(ENTITY_API_URL_ID, partialUpdatedProfileReview.getId())
//            .contentType(MediaType.valueOf("application/merge-patch+json"))
//            .bodyValue(om.writeValueAsBytes(partialUpdatedProfileReview))
//            .exchange()
//            .expectStatus()
//            .isOk();
//
//        // Validate the ProfileReview in the database
//
//        assertSameRepositoryCount(databaseSizeBeforeUpdate);
//        assertProfileReviewUpdatableFieldsEquals(
//            createUpdateProxyForBean(partialUpdatedProfileReview, profileReview),
//            getPersistedProfileReview(profileReview)
//        );
//    }
//
//    @Test
//    void fullUpdateProfileReviewWithPatch() throws Exception {
//        // Initialize the database
//        insertedProfileReview = profileReviewRepository.save(profileReview).block();
//
//        long databaseSizeBeforeUpdate = getRepositoryCount();
//
//        // Update the profileReview using partial update
//        ProfileReview partialUpdatedProfileReview = new ProfileReview();
//        partialUpdatedProfileReview.setId(profileReview.getId());
//
//        partialUpdatedProfileReview
//            .text(UPDATED_TEXT)
//            .rating(UPDATED_RATING)
//            .createdDate(UPDATED_CREATED_DATE)
//            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
//            .createdBy(UPDATED_CREATED_BY)
//            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY);
//
//        webTestClient
//            .patch()
//            .uri(ENTITY_API_URL_ID, partialUpdatedProfileReview.getId())
//            .contentType(MediaType.valueOf("application/merge-patch+json"))
//            .bodyValue(om.writeValueAsBytes(partialUpdatedProfileReview))
//            .exchange()
//            .expectStatus()
//            .isOk();
//
//        // Validate the ProfileReview in the database
//
//        assertSameRepositoryCount(databaseSizeBeforeUpdate);
//        assertProfileReviewUpdatableFieldsEquals(partialUpdatedProfileReview, getPersistedProfileReview(partialUpdatedProfileReview));
//    }
//
//    @Test
//    void patchNonExistingProfileReview() throws Exception {
//        long databaseSizeBeforeUpdate = getRepositoryCount();
//        profileReview.setId(longCount.incrementAndGet());
//
//        // Create the ProfileReview
//        ProfileReviewDTO profileReviewDTO = profileReviewMapper.toDto(profileReview);
//
//        // If the entity doesn't have an ID, it will throw BadRequestAlertException
//        webTestClient
//            .patch()
//            .uri(ENTITY_API_URL_ID, profileReviewDTO.getId())
//            .contentType(MediaType.valueOf("application/merge-patch+json"))
//            .bodyValue(om.writeValueAsBytes(profileReviewDTO))
//            .exchange()
//            .expectStatus()
//            .isBadRequest();
//
//        // Validate the ProfileReview in the database
//        assertSameRepositoryCount(databaseSizeBeforeUpdate);
//    }
//
//    @Test
//    void patchWithIdMismatchProfileReview() throws Exception {
//        long databaseSizeBeforeUpdate = getRepositoryCount();
//        profileReview.setId(longCount.incrementAndGet());
//
//        // Create the ProfileReview
//        ProfileReviewDTO profileReviewDTO = profileReviewMapper.toDto(profileReview);
//
//        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
//        webTestClient
//            .patch()
//            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
//            .contentType(MediaType.valueOf("application/merge-patch+json"))
//            .bodyValue(om.writeValueAsBytes(profileReviewDTO))
//            .exchange()
//            .expectStatus()
//            .isBadRequest();
//
//        // Validate the ProfileReview in the database
//        assertSameRepositoryCount(databaseSizeBeforeUpdate);
//    }
//
//    @Test
//    void patchWithMissingIdPathParamProfileReview() throws Exception {
//        long databaseSizeBeforeUpdate = getRepositoryCount();
//        profileReview.setId(longCount.incrementAndGet());
//
//        // Create the ProfileReview
//        ProfileReviewDTO profileReviewDTO = profileReviewMapper.toDto(profileReview);
//
//        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
//        webTestClient
//            .patch()
//            .uri(ENTITY_API_URL)
//            .contentType(MediaType.valueOf("application/merge-patch+json"))
//            .bodyValue(om.writeValueAsBytes(profileReviewDTO))
//            .exchange()
//            .expectStatus()
//            .isEqualTo(405);
//
//        // Validate the ProfileReview in the database
//        assertSameRepositoryCount(databaseSizeBeforeUpdate);
//    }
//
//    @Test
//    void deleteProfileReview() {
//        // Initialize the database
//        insertedProfileReview = profileReviewRepository.save(profileReview).block();
//
//        long databaseSizeBeforeDelete = getRepositoryCount();
//
//        // Delete the profileReview
//        webTestClient
//            .delete()
//            .uri(ENTITY_API_URL_ID, profileReview.getId())
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
//        return profileReviewRepository.count().block();
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
//    protected ProfileReview getPersistedProfileReview(ProfileReview profileReview) {
//        return profileReviewRepository.findById(profileReview.getId()).block();
//    }
//
//    protected void assertPersistedProfileReviewToMatchAllProperties(ProfileReview expectedProfileReview) {
//        // Test fails because reactive api returns an empty object instead of null
//        // assertProfileReviewAllPropertiesEquals(expectedProfileReview, getPersistedProfileReview(expectedProfileReview));
//        assertProfileReviewUpdatableFieldsEquals(expectedProfileReview, getPersistedProfileReview(expectedProfileReview));
//    }
//
//    protected void assertPersistedProfileReviewToMatchUpdatableProperties(ProfileReview expectedProfileReview) {
//        // Test fails because reactive api returns an empty object instead of null
//        // assertProfileReviewAllUpdatablePropertiesEquals(expectedProfileReview, getPersistedProfileReview(expectedProfileReview));
//        assertProfileReviewUpdatableFieldsEquals(expectedProfileReview, getPersistedProfileReview(expectedProfileReview));
//    }
//}
