package com.freelance.app.web.rest;

import static com.freelance.app.domain.OfferReviewAsserts.*;
import static com.freelance.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.freelance.app.IntegrationTest;
import com.freelance.app.domain.Offer;
import com.freelance.app.domain.OfferReview;
import com.freelance.app.domain.Profile;
import com.freelance.app.repository.EntityManager;
import com.freelance.app.repository.OfferRepository;
import com.freelance.app.repository.OfferReviewRepository;
import com.freelance.app.repository.ProfileRepository;
import com.freelance.app.service.OfferReviewService;
import com.freelance.app.service.dto.OfferReviewDTO;
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
 * Integration tests for the {@link OfferReviewResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class OfferReviewResourceIT {

    private static final String DEFAULT_TEXT = "AAAAAAAAAA";
    private static final String UPDATED_TEXT = "BBBBBBBBBB";

    private static final Double DEFAULT_RATING = 1D;
    private static final Double UPDATED_RATING = 2D;
    private static final Double SMALLER_RATING = 1D - 1D;

    private static final Instant DEFAULT_CREATED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_LAST_MODIFIED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_MODIFIED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_MODIFIED_BY = "AAAAAAAAAA";
    private static final String UPDATED_LAST_MODIFIED_BY = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/offer-reviews";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private OfferReviewRepository offerReviewRepository;

    @Mock
    private OfferReviewRepository offerReviewRepositoryMock;

    @Autowired
    private OfferReviewMapper offerReviewMapper;

    @Mock
    private OfferReviewService offerReviewServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private OfferReview offerReview;

    private OfferReview insertedOfferReview;

    @Autowired
    private OfferRepository offerRepository;

    @Autowired
    private ProfileRepository profileRepository;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OfferReview createEntity() {
        return new OfferReview()
            .text(DEFAULT_TEXT)
            .rating(DEFAULT_RATING)
            .createdDate(DEFAULT_CREATED_DATE)
            .lastModifiedDate(DEFAULT_LAST_MODIFIED_DATE)
            .createdBy(DEFAULT_CREATED_BY)
            .lastModifiedBy(DEFAULT_LAST_MODIFIED_BY);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OfferReview createUpdatedEntity() {
        return new OfferReview()
            .text(UPDATED_TEXT)
            .rating(UPDATED_RATING)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .createdBy(UPDATED_CREATED_BY)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(OfferReview.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        offerReview = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedOfferReview != null) {
            offerReviewRepository.delete(insertedOfferReview).block();
            insertedOfferReview = null;
        }
        deleteEntities(em);
    }

    @Test
    void createOfferReview() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the OfferReview
        OfferReviewDTO offerReviewDTO = offerReviewMapper.toDto(offerReview);
        var returnedOfferReviewDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(offerReviewDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(OfferReviewDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the OfferReview in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedOfferReview = offerReviewMapper.toEntity(returnedOfferReviewDTO);
        assertOfferReviewUpdatableFieldsEquals(returnedOfferReview, getPersistedOfferReview(returnedOfferReview));

        insertedOfferReview = returnedOfferReview;
    }

    @Test
    void createOfferReviewWithExistingId() throws Exception {
        // Create the OfferReview with an existing ID
        offerReview.setId(1L);
        OfferReviewDTO offerReviewDTO = offerReviewMapper.toDto(offerReview);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(offerReviewDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the OfferReview in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkRatingIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        offerReview.setRating(null);

        // Create the OfferReview, which fails.
        OfferReviewDTO offerReviewDTO = offerReviewMapper.toDto(offerReview);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(offerReviewDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkCreatedDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        offerReview.setCreatedDate(null);

        // Create the OfferReview, which fails.
        OfferReviewDTO offerReviewDTO = offerReviewMapper.toDto(offerReview);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(offerReviewDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllOfferReviews() {
        // Initialize the database
        insertedOfferReview = offerReviewRepository.save(offerReview).block();

        // Get all the offerReviewList
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
            .value(hasItem(offerReview.getId().intValue()))
            .jsonPath("$.[*].text")
            .value(hasItem(DEFAULT_TEXT))
            .jsonPath("$.[*].rating")
            .value(hasItem(DEFAULT_RATING))
            .jsonPath("$.[*].createdDate")
            .value(hasItem(DEFAULT_CREATED_DATE.toString()))
            .jsonPath("$.[*].lastModifiedDate")
            .value(hasItem(DEFAULT_LAST_MODIFIED_DATE.toString()))
            .jsonPath("$.[*].createdBy")
            .value(hasItem(DEFAULT_CREATED_BY))
            .jsonPath("$.[*].lastModifiedBy")
            .value(hasItem(DEFAULT_LAST_MODIFIED_BY));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllOfferReviewsWithEagerRelationshipsIsEnabled() {
        when(offerReviewServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(offerReviewServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllOfferReviewsWithEagerRelationshipsIsNotEnabled() {
        when(offerReviewServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(offerReviewRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getOfferReview() {
        // Initialize the database
        insertedOfferReview = offerReviewRepository.save(offerReview).block();

        // Get the offerReview
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, offerReview.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(offerReview.getId().intValue()))
            .jsonPath("$.text")
            .value(is(DEFAULT_TEXT))
            .jsonPath("$.rating")
            .value(is(DEFAULT_RATING))
            .jsonPath("$.createdDate")
            .value(is(DEFAULT_CREATED_DATE.toString()))
            .jsonPath("$.lastModifiedDate")
            .value(is(DEFAULT_LAST_MODIFIED_DATE.toString()))
            .jsonPath("$.createdBy")
            .value(is(DEFAULT_CREATED_BY))
            .jsonPath("$.lastModifiedBy")
            .value(is(DEFAULT_LAST_MODIFIED_BY));
    }

    @Test
    void getOfferReviewsByIdFiltering() {
        // Initialize the database
        insertedOfferReview = offerReviewRepository.save(offerReview).block();

        Long id = offerReview.getId();

        defaultOfferReviewFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultOfferReviewFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultOfferReviewFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllOfferReviewsByTextIsEqualToSomething() {
        // Initialize the database
        insertedOfferReview = offerReviewRepository.save(offerReview).block();

        // Get all the offerReviewList where text equals to
        defaultOfferReviewFiltering("text.equals=" + DEFAULT_TEXT, "text.equals=" + UPDATED_TEXT);
    }

    @Test
    void getAllOfferReviewsByTextIsInShouldWork() {
        // Initialize the database
        insertedOfferReview = offerReviewRepository.save(offerReview).block();

        // Get all the offerReviewList where text in
        defaultOfferReviewFiltering("text.in=" + DEFAULT_TEXT + "," + UPDATED_TEXT, "text.in=" + UPDATED_TEXT);
    }

    @Test
    void getAllOfferReviewsByTextIsNullOrNotNull() {
        // Initialize the database
        insertedOfferReview = offerReviewRepository.save(offerReview).block();

        // Get all the offerReviewList where text is not null
        defaultOfferReviewFiltering("text.specified=true", "text.specified=false");
    }

    @Test
    void getAllOfferReviewsByTextContainsSomething() {
        // Initialize the database
        insertedOfferReview = offerReviewRepository.save(offerReview).block();

        // Get all the offerReviewList where text contains
        defaultOfferReviewFiltering("text.contains=" + DEFAULT_TEXT, "text.contains=" + UPDATED_TEXT);
    }

    @Test
    void getAllOfferReviewsByTextNotContainsSomething() {
        // Initialize the database
        insertedOfferReview = offerReviewRepository.save(offerReview).block();

        // Get all the offerReviewList where text does not contain
        defaultOfferReviewFiltering("text.doesNotContain=" + UPDATED_TEXT, "text.doesNotContain=" + DEFAULT_TEXT);
    }

    @Test
    void getAllOfferReviewsByRatingIsEqualToSomething() {
        // Initialize the database
        insertedOfferReview = offerReviewRepository.save(offerReview).block();

        // Get all the offerReviewList where rating equals to
        defaultOfferReviewFiltering("rating.equals=" + DEFAULT_RATING, "rating.equals=" + UPDATED_RATING);
    }

    @Test
    void getAllOfferReviewsByRatingIsInShouldWork() {
        // Initialize the database
        insertedOfferReview = offerReviewRepository.save(offerReview).block();

        // Get all the offerReviewList where rating in
        defaultOfferReviewFiltering("rating.in=" + DEFAULT_RATING + "," + UPDATED_RATING, "rating.in=" + UPDATED_RATING);
    }

    @Test
    void getAllOfferReviewsByRatingIsNullOrNotNull() {
        // Initialize the database
        insertedOfferReview = offerReviewRepository.save(offerReview).block();

        // Get all the offerReviewList where rating is not null
        defaultOfferReviewFiltering("rating.specified=true", "rating.specified=false");
    }

    @Test
    void getAllOfferReviewsByRatingIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedOfferReview = offerReviewRepository.save(offerReview).block();

        // Get all the offerReviewList where rating is greater than or equal to
        defaultOfferReviewFiltering("rating.greaterThanOrEqual=" + DEFAULT_RATING, "rating.greaterThanOrEqual=" + (DEFAULT_RATING + 1));
    }

    @Test
    void getAllOfferReviewsByRatingIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedOfferReview = offerReviewRepository.save(offerReview).block();

        // Get all the offerReviewList where rating is less than or equal to
        defaultOfferReviewFiltering("rating.lessThanOrEqual=" + DEFAULT_RATING, "rating.lessThanOrEqual=" + SMALLER_RATING);
    }

    @Test
    void getAllOfferReviewsByRatingIsLessThanSomething() {
        // Initialize the database
        insertedOfferReview = offerReviewRepository.save(offerReview).block();

        // Get all the offerReviewList where rating is less than
        defaultOfferReviewFiltering("rating.lessThan=" + (DEFAULT_RATING + 1), "rating.lessThan=" + DEFAULT_RATING);
    }

    @Test
    void getAllOfferReviewsByRatingIsGreaterThanSomething() {
        // Initialize the database
        insertedOfferReview = offerReviewRepository.save(offerReview).block();

        // Get all the offerReviewList where rating is greater than
        defaultOfferReviewFiltering("rating.greaterThan=" + SMALLER_RATING, "rating.greaterThan=" + DEFAULT_RATING);
    }

    @Test
    void getAllOfferReviewsByCreatedDateIsEqualToSomething() {
        // Initialize the database
        insertedOfferReview = offerReviewRepository.save(offerReview).block();

        // Get all the offerReviewList where createdDate equals to
        defaultOfferReviewFiltering("createdDate.equals=" + DEFAULT_CREATED_DATE, "createdDate.equals=" + UPDATED_CREATED_DATE);
    }

    @Test
    void getAllOfferReviewsByCreatedDateIsInShouldWork() {
        // Initialize the database
        insertedOfferReview = offerReviewRepository.save(offerReview).block();

        // Get all the offerReviewList where createdDate in
        defaultOfferReviewFiltering(
            "createdDate.in=" + DEFAULT_CREATED_DATE + "," + UPDATED_CREATED_DATE,
            "createdDate.in=" + UPDATED_CREATED_DATE
        );
    }

    @Test
    void getAllOfferReviewsByCreatedDateIsNullOrNotNull() {
        // Initialize the database
        insertedOfferReview = offerReviewRepository.save(offerReview).block();

        // Get all the offerReviewList where createdDate is not null
        defaultOfferReviewFiltering("createdDate.specified=true", "createdDate.specified=false");
    }

    @Test
    void getAllOfferReviewsByLastModifiedDateIsEqualToSomething() {
        // Initialize the database
        insertedOfferReview = offerReviewRepository.save(offerReview).block();

        // Get all the offerReviewList where lastModifiedDate equals to
        defaultOfferReviewFiltering(
            "lastModifiedDate.equals=" + DEFAULT_LAST_MODIFIED_DATE,
            "lastModifiedDate.equals=" + UPDATED_LAST_MODIFIED_DATE
        );
    }

    @Test
    void getAllOfferReviewsByLastModifiedDateIsInShouldWork() {
        // Initialize the database
        insertedOfferReview = offerReviewRepository.save(offerReview).block();

        // Get all the offerReviewList where lastModifiedDate in
        defaultOfferReviewFiltering(
            "lastModifiedDate.in=" + DEFAULT_LAST_MODIFIED_DATE + "," + UPDATED_LAST_MODIFIED_DATE,
            "lastModifiedDate.in=" + UPDATED_LAST_MODIFIED_DATE
        );
    }

    @Test
    void getAllOfferReviewsByLastModifiedDateIsNullOrNotNull() {
        // Initialize the database
        insertedOfferReview = offerReviewRepository.save(offerReview).block();

        // Get all the offerReviewList where lastModifiedDate is not null
        defaultOfferReviewFiltering("lastModifiedDate.specified=true", "lastModifiedDate.specified=false");
    }

    @Test
    void getAllOfferReviewsByCreatedByIsEqualToSomething() {
        // Initialize the database
        insertedOfferReview = offerReviewRepository.save(offerReview).block();

        // Get all the offerReviewList where createdBy equals to
        defaultOfferReviewFiltering("createdBy.equals=" + DEFAULT_CREATED_BY, "createdBy.equals=" + UPDATED_CREATED_BY);
    }

    @Test
    void getAllOfferReviewsByCreatedByIsInShouldWork() {
        // Initialize the database
        insertedOfferReview = offerReviewRepository.save(offerReview).block();

        // Get all the offerReviewList where createdBy in
        defaultOfferReviewFiltering("createdBy.in=" + DEFAULT_CREATED_BY + "," + UPDATED_CREATED_BY, "createdBy.in=" + UPDATED_CREATED_BY);
    }

    @Test
    void getAllOfferReviewsByCreatedByIsNullOrNotNull() {
        // Initialize the database
        insertedOfferReview = offerReviewRepository.save(offerReview).block();

        // Get all the offerReviewList where createdBy is not null
        defaultOfferReviewFiltering("createdBy.specified=true", "createdBy.specified=false");
    }

    @Test
    void getAllOfferReviewsByCreatedByContainsSomething() {
        // Initialize the database
        insertedOfferReview = offerReviewRepository.save(offerReview).block();

        // Get all the offerReviewList where createdBy contains
        defaultOfferReviewFiltering("createdBy.contains=" + DEFAULT_CREATED_BY, "createdBy.contains=" + UPDATED_CREATED_BY);
    }

    @Test
    void getAllOfferReviewsByCreatedByNotContainsSomething() {
        // Initialize the database
        insertedOfferReview = offerReviewRepository.save(offerReview).block();

        // Get all the offerReviewList where createdBy does not contain
        defaultOfferReviewFiltering("createdBy.doesNotContain=" + UPDATED_CREATED_BY, "createdBy.doesNotContain=" + DEFAULT_CREATED_BY);
    }

    @Test
    void getAllOfferReviewsByLastModifiedByIsEqualToSomething() {
        // Initialize the database
        insertedOfferReview = offerReviewRepository.save(offerReview).block();

        // Get all the offerReviewList where lastModifiedBy equals to
        defaultOfferReviewFiltering(
            "lastModifiedBy.equals=" + DEFAULT_LAST_MODIFIED_BY,
            "lastModifiedBy.equals=" + UPDATED_LAST_MODIFIED_BY
        );
    }

    @Test
    void getAllOfferReviewsByLastModifiedByIsInShouldWork() {
        // Initialize the database
        insertedOfferReview = offerReviewRepository.save(offerReview).block();

        // Get all the offerReviewList where lastModifiedBy in
        defaultOfferReviewFiltering(
            "lastModifiedBy.in=" + DEFAULT_LAST_MODIFIED_BY + "," + UPDATED_LAST_MODIFIED_BY,
            "lastModifiedBy.in=" + UPDATED_LAST_MODIFIED_BY
        );
    }

    @Test
    void getAllOfferReviewsByLastModifiedByIsNullOrNotNull() {
        // Initialize the database
        insertedOfferReview = offerReviewRepository.save(offerReview).block();

        // Get all the offerReviewList where lastModifiedBy is not null
        defaultOfferReviewFiltering("lastModifiedBy.specified=true", "lastModifiedBy.specified=false");
    }

    @Test
    void getAllOfferReviewsByLastModifiedByContainsSomething() {
        // Initialize the database
        insertedOfferReview = offerReviewRepository.save(offerReview).block();

        // Get all the offerReviewList where lastModifiedBy contains
        defaultOfferReviewFiltering(
            "lastModifiedBy.contains=" + DEFAULT_LAST_MODIFIED_BY,
            "lastModifiedBy.contains=" + UPDATED_LAST_MODIFIED_BY
        );
    }

    @Test
    void getAllOfferReviewsByLastModifiedByNotContainsSomething() {
        // Initialize the database
        insertedOfferReview = offerReviewRepository.save(offerReview).block();

        // Get all the offerReviewList where lastModifiedBy does not contain
        defaultOfferReviewFiltering(
            "lastModifiedBy.doesNotContain=" + UPDATED_LAST_MODIFIED_BY,
            "lastModifiedBy.doesNotContain=" + DEFAULT_LAST_MODIFIED_BY
        );
    }

    @Test
    void getAllOfferReviewsByOfferIsEqualToSomething() {
        Offer offer = OfferResourceIT.createEntity();
        offerRepository.save(offer).block();
        Long offerId = offer.getId();
        offerReview.setOfferId(offerId);
        insertedOfferReview = offerReviewRepository.save(offerReview).block();
        // Get all the offerReviewList where offer equals to offerId
        defaultOfferReviewShouldBeFound("offerId.equals=" + offerId);

        // Get all the offerReviewList where offer equals to (offerId + 1)
        defaultOfferReviewShouldNotBeFound("offerId.equals=" + (offerId + 1));
    }

    @Test
    void getAllOfferReviewsByReviewerIsEqualToSomething() {
        Profile reviewer = ProfileResourceIT.createEntity();
        profileRepository.save(reviewer).block();
        Long reviewerId = reviewer.getId();
        offerReview.setReviewerId(reviewerId);
        insertedOfferReview = offerReviewRepository.save(offerReview).block();
        // Get all the offerReviewList where reviewer equals to reviewerId
        defaultOfferReviewShouldBeFound("reviewerId.equals=" + reviewerId);

        // Get all the offerReviewList where reviewer equals to (reviewerId + 1)
        defaultOfferReviewShouldNotBeFound("reviewerId.equals=" + (reviewerId + 1));
    }

    private void defaultOfferReviewFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultOfferReviewShouldBeFound(shouldBeFound);
        defaultOfferReviewShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultOfferReviewShouldBeFound(String filter) {
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
            .value(hasItem(offerReview.getId().intValue()))
            .jsonPath("$.[*].text")
            .value(hasItem(DEFAULT_TEXT))
            .jsonPath("$.[*].rating")
            .value(hasItem(DEFAULT_RATING))
            .jsonPath("$.[*].createdDate")
            .value(hasItem(DEFAULT_CREATED_DATE.toString()))
            .jsonPath("$.[*].lastModifiedDate")
            .value(hasItem(DEFAULT_LAST_MODIFIED_DATE.toString()))
            .jsonPath("$.[*].createdBy")
            .value(hasItem(DEFAULT_CREATED_BY))
            .jsonPath("$.[*].lastModifiedBy")
            .value(hasItem(DEFAULT_LAST_MODIFIED_BY));

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
    private void defaultOfferReviewShouldNotBeFound(String filter) {
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
    void getNonExistingOfferReview() {
        // Get the offerReview
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingOfferReview() throws Exception {
        // Initialize the database
        insertedOfferReview = offerReviewRepository.save(offerReview).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the offerReview
        OfferReview updatedOfferReview = offerReviewRepository.findById(offerReview.getId()).block();
        updatedOfferReview
            .text(UPDATED_TEXT)
            .rating(UPDATED_RATING)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .createdBy(UPDATED_CREATED_BY)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY);
        OfferReviewDTO offerReviewDTO = offerReviewMapper.toDto(updatedOfferReview);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, offerReviewDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(offerReviewDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the OfferReview in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedOfferReviewToMatchAllProperties(updatedOfferReview);
    }

    @Test
    void putNonExistingOfferReview() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        offerReview.setId(longCount.incrementAndGet());

        // Create the OfferReview
        OfferReviewDTO offerReviewDTO = offerReviewMapper.toDto(offerReview);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, offerReviewDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(offerReviewDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the OfferReview in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchOfferReview() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        offerReview.setId(longCount.incrementAndGet());

        // Create the OfferReview
        OfferReviewDTO offerReviewDTO = offerReviewMapper.toDto(offerReview);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(offerReviewDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the OfferReview in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamOfferReview() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        offerReview.setId(longCount.incrementAndGet());

        // Create the OfferReview
        OfferReviewDTO offerReviewDTO = offerReviewMapper.toDto(offerReview);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(offerReviewDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the OfferReview in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateOfferReviewWithPatch() throws Exception {
        // Initialize the database
        insertedOfferReview = offerReviewRepository.save(offerReview).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the offerReview using partial update
        OfferReview partialUpdatedOfferReview = new OfferReview();
        partialUpdatedOfferReview.setId(offerReview.getId());

        partialUpdatedOfferReview.text(UPDATED_TEXT).lastModifiedBy(UPDATED_LAST_MODIFIED_BY);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedOfferReview.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedOfferReview))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the OfferReview in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertOfferReviewUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedOfferReview, offerReview),
            getPersistedOfferReview(offerReview)
        );
    }

    @Test
    void fullUpdateOfferReviewWithPatch() throws Exception {
        // Initialize the database
        insertedOfferReview = offerReviewRepository.save(offerReview).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the offerReview using partial update
        OfferReview partialUpdatedOfferReview = new OfferReview();
        partialUpdatedOfferReview.setId(offerReview.getId());

        partialUpdatedOfferReview
            .text(UPDATED_TEXT)
            .rating(UPDATED_RATING)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .createdBy(UPDATED_CREATED_BY)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedOfferReview.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedOfferReview))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the OfferReview in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertOfferReviewUpdatableFieldsEquals(partialUpdatedOfferReview, getPersistedOfferReview(partialUpdatedOfferReview));
    }

    @Test
    void patchNonExistingOfferReview() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        offerReview.setId(longCount.incrementAndGet());

        // Create the OfferReview
        OfferReviewDTO offerReviewDTO = offerReviewMapper.toDto(offerReview);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, offerReviewDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(offerReviewDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the OfferReview in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchOfferReview() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        offerReview.setId(longCount.incrementAndGet());

        // Create the OfferReview
        OfferReviewDTO offerReviewDTO = offerReviewMapper.toDto(offerReview);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(offerReviewDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the OfferReview in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamOfferReview() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        offerReview.setId(longCount.incrementAndGet());

        // Create the OfferReview
        OfferReviewDTO offerReviewDTO = offerReviewMapper.toDto(offerReview);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(offerReviewDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the OfferReview in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteOfferReview() {
        // Initialize the database
        insertedOfferReview = offerReviewRepository.save(offerReview).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the offerReview
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, offerReview.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return offerReviewRepository.count().block();
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

    protected OfferReview getPersistedOfferReview(OfferReview offerReview) {
        return offerReviewRepository.findById(offerReview.getId()).block();
    }

    protected void assertPersistedOfferReviewToMatchAllProperties(OfferReview expectedOfferReview) {
        // Test fails because reactive api returns an empty object instead of null
        // assertOfferReviewAllPropertiesEquals(expectedOfferReview, getPersistedOfferReview(expectedOfferReview));
        assertOfferReviewUpdatableFieldsEquals(expectedOfferReview, getPersistedOfferReview(expectedOfferReview));
    }

    protected void assertPersistedOfferReviewToMatchUpdatableProperties(OfferReview expectedOfferReview) {
        // Test fails because reactive api returns an empty object instead of null
        // assertOfferReviewAllUpdatablePropertiesEquals(expectedOfferReview, getPersistedOfferReview(expectedOfferReview));
        assertOfferReviewUpdatableFieldsEquals(expectedOfferReview, getPersistedOfferReview(expectedOfferReview));
    }
}
