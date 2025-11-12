//package com.freelance.app.web.rest;
//
//import static com.freelance.app.domain.OfferAsserts.*;
//import static com.freelance.app.web.rest.TestUtil.createUpdateProxyForBean;
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.hamcrest.Matchers.hasItem;
//import static org.hamcrest.Matchers.is;
//import static org.mockito.Mockito.*;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.freelance.app.IntegrationTest;
//import com.freelance.app.domain.Offer;
//import com.freelance.app.domain.OfferType;
//import com.freelance.app.domain.Profile;
//import com.freelance.app.domain.enumeration.OfferStatus;
//import com.freelance.app.repository.EntityManager;
//import com.freelance.app.repository.OfferRepository;
//import com.freelance.app.repository.OfferTypeRepository;
//import com.freelance.app.repository.ProfileRepository;
//import com.freelance.app.service.OfferService;
//import com.freelance.app.service.dto.OfferDTO;
//import com.freelance.app.service.mapper.OfferMapper;
//import java.time.Instant;
//import java.time.temporal.ChronoUnit;
//import java.util.Random;
//import java.util.concurrent.atomic.AtomicLong;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
//import org.springframework.http.MediaType;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.web.reactive.server.WebTestClient;
//import reactor.core.publisher.Flux;
//
///**
// * Integration tests for the {@link OfferResource} REST controller.
// */
//@IntegrationTest
//@ExtendWith(MockitoExtension.class)
//@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
//@WithMockUser
//class OfferResourceIT {
//
//    private static final String DEFAULT_NAME = "AAAAAAAAAA";
//    private static final String UPDATED_NAME = "BBBBBBBBBB";
//
//    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
//    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";
//
//    private static final Double DEFAULT_RATING = 0D;
//    private static final Double UPDATED_RATING = 1D;
//    private static final Double SMALLER_RATING = 0D - 1D;
//
//    private static final OfferStatus DEFAULT_STATUS = OfferStatus.ACTIVE;
//    private static final OfferStatus UPDATED_STATUS = OfferStatus.PAUSED;
//
//    private static final Boolean DEFAULT_VISIBILITY = false;
//    private static final Boolean UPDATED_VISIBILITY = true;
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
//    private static final String ENTITY_API_URL = "/api/offers";
//    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
//
//    private static Random random = new Random();
//    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
//
//    @Autowired
//    private ObjectMapper om;
//
//    @Autowired
//    private OfferRepository offerRepository;
//
//    @Mock
//    private OfferRepository offerRepositoryMock;
//
//    @Autowired
//    private OfferMapper offerMapper;
//
//    @Mock
//    private OfferService offerServiceMock;
//
//    @Autowired
//    private EntityManager em;
//
//    @Autowired
//    private WebTestClient webTestClient;
//
//    private Offer offer;
//
//    private Offer insertedOffer;
//
//    @Autowired
//    private ProfileRepository profileRepository;
//
//    @Autowired
//    private OfferTypeRepository offerTypeRepository;
//
//    /**
//     * Create an entity for this test.
//     *
//     * This is a static method, as tests for other entities might also need it,
//     * if they test an entity which requires the current entity.
//     */
//    public static Offer createEntity() {
//        return new Offer()
//            .name(DEFAULT_NAME)
//            .description(DEFAULT_DESCRIPTION)
//            .rating(DEFAULT_RATING)
//            .status(DEFAULT_STATUS)
//            .visibility(DEFAULT_VISIBILITY)
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
//    public static Offer createUpdatedEntity() {
//        return new Offer()
//            .name(UPDATED_NAME)
//            .description(UPDATED_DESCRIPTION)
//            .rating(UPDATED_RATING)
//            .status(UPDATED_STATUS)
//            .visibility(UPDATED_VISIBILITY)
//            .createdDate(UPDATED_CREATED_DATE)
//            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
//            .createdBy(UPDATED_CREATED_BY)
//            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY);
//    }
//
//    public static void deleteEntities(EntityManager em) {
//        try {
//            em.deleteAll("rel_offer__tag").block();
//            em.deleteAll(Offer.class).block();
//        } catch (Exception e) {
//            // It can fail, if other entities are still referring this - it will be removed later.
//        }
//    }
//
//    @BeforeEach
//    void initTest() {
//        offer = createEntity();
//    }
//
//    @AfterEach
//    void cleanup() {
//        if (insertedOffer != null) {
//            offerRepository.delete(insertedOffer).block();
//            insertedOffer = null;
//        }
//        deleteEntities(em);
//    }
//
//    @Test
//    void createOffer() throws Exception {
//        long databaseSizeBeforeCreate = getRepositoryCount();
//        // Create the Offer
//        OfferDTO offerDTO = offerMapper.toDto(offer);
//        var returnedOfferDTO = webTestClient
//            .post()
//            .uri(ENTITY_API_URL)
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(om.writeValueAsBytes(offerDTO))
//            .exchange()
//            .expectStatus()
//            .isCreated()
//            .expectBody(OfferDTO.class)
//            .returnResult()
//            .getResponseBody();
//
//        // Validate the Offer in the database
//        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
//        var returnedOffer = offerMapper.toEntity(returnedOfferDTO);
//        assertOfferUpdatableFieldsEquals(returnedOffer, getPersistedOffer(returnedOffer));
//
//        insertedOffer = returnedOffer;
//    }
//
//    @Test
//    void createOfferWithExistingId() throws Exception {
//        // Create the Offer with an existing ID
//        offer.setId(1L);
//        OfferDTO offerDTO = offerMapper.toDto(offer);
//
//        long databaseSizeBeforeCreate = getRepositoryCount();
//
//        // An entity with an existing ID cannot be created, so this API call must fail
//        webTestClient
//            .post()
//            .uri(ENTITY_API_URL)
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(om.writeValueAsBytes(offerDTO))
//            .exchange()
//            .expectStatus()
//            .isBadRequest();
//
//        // Validate the Offer in the database
//        assertSameRepositoryCount(databaseSizeBeforeCreate);
//    }
//
//    @Test
//    void checkNameIsRequired() throws Exception {
//        long databaseSizeBeforeTest = getRepositoryCount();
//        // set the field null
//        offer.setName(null);
//
//        // Create the Offer, which fails.
//        OfferDTO offerDTO = offerMapper.toDto(offer);
//
//        webTestClient
//            .post()
//            .uri(ENTITY_API_URL)
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(om.writeValueAsBytes(offerDTO))
//            .exchange()
//            .expectStatus()
//            .isBadRequest();
//
//        assertSameRepositoryCount(databaseSizeBeforeTest);
//    }
//
//    @Test
//    void checkDescriptionIsRequired() throws Exception {
//        long databaseSizeBeforeTest = getRepositoryCount();
//        // set the field null
//        offer.setDescription(null);
//
//        // Create the Offer, which fails.
//        OfferDTO offerDTO = offerMapper.toDto(offer);
//
//        webTestClient
//            .post()
//            .uri(ENTITY_API_URL)
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(om.writeValueAsBytes(offerDTO))
//            .exchange()
//            .expectStatus()
//            .isBadRequest();
//
//        assertSameRepositoryCount(databaseSizeBeforeTest);
//    }
//
//    @Test
//    void checkStatusIsRequired() throws Exception {
//        long databaseSizeBeforeTest = getRepositoryCount();
//        // set the field null
//        offer.setStatus(null);
//
//        // Create the Offer, which fails.
//        OfferDTO offerDTO = offerMapper.toDto(offer);
//
//        webTestClient
//            .post()
//            .uri(ENTITY_API_URL)
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(om.writeValueAsBytes(offerDTO))
//            .exchange()
//            .expectStatus()
//            .isBadRequest();
//
//        assertSameRepositoryCount(databaseSizeBeforeTest);
//    }
//
//    @Test
//    void checkVisibilityIsRequired() throws Exception {
//        long databaseSizeBeforeTest = getRepositoryCount();
//        // set the field null
//        offer.setVisibility(null);
//
//        // Create the Offer, which fails.
//        OfferDTO offerDTO = offerMapper.toDto(offer);
//
//        webTestClient
//            .post()
//            .uri(ENTITY_API_URL)
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(om.writeValueAsBytes(offerDTO))
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
//        offer.setCreatedDate(null);
//
//        // Create the Offer, which fails.
//        OfferDTO offerDTO = offerMapper.toDto(offer);
//
//        webTestClient
//            .post()
//            .uri(ENTITY_API_URL)
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(om.writeValueAsBytes(offerDTO))
//            .exchange()
//            .expectStatus()
//            .isBadRequest();
//
//        assertSameRepositoryCount(databaseSizeBeforeTest);
//    }
//
//    @Test
//    void getAllOffers() {
//        // Initialize the database
//        insertedOffer = offerRepository.save(offer).block();
//
//        // Get all the offerList
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
//            .value(hasItem(offer.getId().intValue()))
//            .jsonPath("$.[*].name")
//            .value(hasItem(DEFAULT_NAME))
//            .jsonPath("$.[*].description")
//            .value(hasItem(DEFAULT_DESCRIPTION))
//            .jsonPath("$.[*].rating")
//            .value(hasItem(DEFAULT_RATING))
//            .jsonPath("$.[*].status")
//            .value(hasItem(DEFAULT_STATUS.toString()))
//            .jsonPath("$.[*].visibility")
//            .value(hasItem(DEFAULT_VISIBILITY))
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
//    @SuppressWarnings({ "unchecked" })
//    void getAllOffersWithEagerRelationshipsIsEnabled() {
//        when(offerServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());
//
//        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();
//
//        verify(offerServiceMock, times(1)).findAllWithEagerRelationships(any());
//    }
//
//    @SuppressWarnings({ "unchecked" })
//    void getAllOffersWithEagerRelationshipsIsNotEnabled() {
//        when(offerServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());
//
//        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
//        verify(offerRepositoryMock, times(1)).findAllWithEagerRelationships(any());
//    }
//
//    @Test
//    void getOffer() {
//        // Initialize the database
//        insertedOffer = offerRepository.save(offer).block();
//
//        // Get the offer
//        webTestClient
//            .get()
//            .uri(ENTITY_API_URL_ID, offer.getId())
//            .accept(MediaType.APPLICATION_JSON)
//            .exchange()
//            .expectStatus()
//            .isOk()
//            .expectHeader()
//            .contentType(MediaType.APPLICATION_JSON)
//            .expectBody()
//            .jsonPath("$.id")
//            .value(is(offer.getId().intValue()))
//            .jsonPath("$.name")
//            .value(is(DEFAULT_NAME))
//            .jsonPath("$.description")
//            .value(is(DEFAULT_DESCRIPTION))
//            .jsonPath("$.rating")
//            .value(is(DEFAULT_RATING))
//            .jsonPath("$.status")
//            .value(is(DEFAULT_STATUS.toString()))
//            .jsonPath("$.visibility")
//            .value(is(DEFAULT_VISIBILITY))
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
//    void getOffersByIdFiltering() {
//        // Initialize the database
//        insertedOffer = offerRepository.save(offer).block();
//
//        Long id = offer.getId();
//
//        defaultOfferFiltering("id.equals=" + id, "id.notEquals=" + id);
//
//        defaultOfferFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);
//
//        defaultOfferFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
//    }
//
//    @Test
//    void getAllOffersByNameIsEqualToSomething() {
//        // Initialize the database
//        insertedOffer = offerRepository.save(offer).block();
//
//        // Get all the offerList where name equals to
//        defaultOfferFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
//    }
//
//    @Test
//    void getAllOffersByNameIsInShouldWork() {
//        // Initialize the database
//        insertedOffer = offerRepository.save(offer).block();
//
//        // Get all the offerList where name in
//        defaultOfferFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
//    }
//
//    @Test
//    void getAllOffersByNameIsNullOrNotNull() {
//        // Initialize the database
//        insertedOffer = offerRepository.save(offer).block();
//
//        // Get all the offerList where name is not null
//        defaultOfferFiltering("name.specified=true", "name.specified=false");
//    }
//
//    @Test
//    void getAllOffersByNameContainsSomething() {
//        // Initialize the database
//        insertedOffer = offerRepository.save(offer).block();
//
//        // Get all the offerList where name contains
//        defaultOfferFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
//    }
//
//    @Test
//    void getAllOffersByNameNotContainsSomething() {
//        // Initialize the database
//        insertedOffer = offerRepository.save(offer).block();
//
//        // Get all the offerList where name does not contain
//        defaultOfferFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
//    }
//
//    @Test
//    void getAllOffersByDescriptionIsEqualToSomething() {
//        // Initialize the database
//        insertedOffer = offerRepository.save(offer).block();
//
//        // Get all the offerList where description equals to
//        defaultOfferFiltering("description.equals=" + DEFAULT_DESCRIPTION, "description.equals=" + UPDATED_DESCRIPTION);
//    }
//
//    @Test
//    void getAllOffersByDescriptionIsInShouldWork() {
//        // Initialize the database
//        insertedOffer = offerRepository.save(offer).block();
//
//        // Get all the offerList where description in
//        defaultOfferFiltering("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION, "description.in=" + UPDATED_DESCRIPTION);
//    }
//
//    @Test
//    void getAllOffersByDescriptionIsNullOrNotNull() {
//        // Initialize the database
//        insertedOffer = offerRepository.save(offer).block();
//
//        // Get all the offerList where description is not null
//        defaultOfferFiltering("description.specified=true", "description.specified=false");
//    }
//
//    @Test
//    void getAllOffersByDescriptionContainsSomething() {
//        // Initialize the database
//        insertedOffer = offerRepository.save(offer).block();
//
//        // Get all the offerList where description contains
//        defaultOfferFiltering("description.contains=" + DEFAULT_DESCRIPTION, "description.contains=" + UPDATED_DESCRIPTION);
//    }
//
//    @Test
//    void getAllOffersByDescriptionNotContainsSomething() {
//        // Initialize the database
//        insertedOffer = offerRepository.save(offer).block();
//
//        // Get all the offerList where description does not contain
//        defaultOfferFiltering("description.doesNotContain=" + UPDATED_DESCRIPTION, "description.doesNotContain=" + DEFAULT_DESCRIPTION);
//    }
//
//    @Test
//    void getAllOffersByRatingIsEqualToSomething() {
//        // Initialize the database
//        insertedOffer = offerRepository.save(offer).block();
//
//        // Get all the offerList where rating equals to
//        defaultOfferFiltering("rating.equals=" + DEFAULT_RATING, "rating.equals=" + UPDATED_RATING);
//    }
//
//    @Test
//    void getAllOffersByRatingIsInShouldWork() {
//        // Initialize the database
//        insertedOffer = offerRepository.save(offer).block();
//
//        // Get all the offerList where rating in
//        defaultOfferFiltering("rating.in=" + DEFAULT_RATING + "," + UPDATED_RATING, "rating.in=" + UPDATED_RATING);
//    }
//
//    @Test
//    void getAllOffersByRatingIsNullOrNotNull() {
//        // Initialize the database
//        insertedOffer = offerRepository.save(offer).block();
//
//        // Get all the offerList where rating is not null
//        defaultOfferFiltering("rating.specified=true", "rating.specified=false");
//    }
//
//    @Test
//    void getAllOffersByRatingIsGreaterThanOrEqualToSomething() {
//        // Initialize the database
//        insertedOffer = offerRepository.save(offer).block();
//
//        // Get all the offerList where rating is greater than or equal to
//        defaultOfferFiltering("rating.greaterThanOrEqual=" + DEFAULT_RATING, "rating.greaterThanOrEqual=" + (DEFAULT_RATING + 1));
//    }
//
//    @Test
//    void getAllOffersByRatingIsLessThanOrEqualToSomething() {
//        // Initialize the database
//        insertedOffer = offerRepository.save(offer).block();
//
//        // Get all the offerList where rating is less than or equal to
//        defaultOfferFiltering("rating.lessThanOrEqual=" + DEFAULT_RATING, "rating.lessThanOrEqual=" + SMALLER_RATING);
//    }
//
//    @Test
//    void getAllOffersByRatingIsLessThanSomething() {
//        // Initialize the database
//        insertedOffer = offerRepository.save(offer).block();
//
//        // Get all the offerList where rating is less than
//        defaultOfferFiltering("rating.lessThan=" + (DEFAULT_RATING + 1), "rating.lessThan=" + DEFAULT_RATING);
//    }
//
//    @Test
//    void getAllOffersByRatingIsGreaterThanSomething() {
//        // Initialize the database
//        insertedOffer = offerRepository.save(offer).block();
//
//        // Get all the offerList where rating is greater than
//        defaultOfferFiltering("rating.greaterThan=" + SMALLER_RATING, "rating.greaterThan=" + DEFAULT_RATING);
//    }
//
//    @Test
//    void getAllOffersByStatusIsEqualToSomething() {
//        // Initialize the database
//        insertedOffer = offerRepository.save(offer).block();
//
//        // Get all the offerList where status equals to
//        defaultOfferFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
//    }
//
//    @Test
//    void getAllOffersByStatusIsInShouldWork() {
//        // Initialize the database
//        insertedOffer = offerRepository.save(offer).block();
//
//        // Get all the offerList where status in
//        defaultOfferFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
//    }
//
//    @Test
//    void getAllOffersByStatusIsNullOrNotNull() {
//        // Initialize the database
//        insertedOffer = offerRepository.save(offer).block();
//
//        // Get all the offerList where status is not null
//        defaultOfferFiltering("status.specified=true", "status.specified=false");
//    }
//
//    @Test
//    void getAllOffersByVisibilityIsEqualToSomething() {
//        // Initialize the database
//        insertedOffer = offerRepository.save(offer).block();
//
//        // Get all the offerList where visibility equals to
//        defaultOfferFiltering("visibility.equals=" + DEFAULT_VISIBILITY, "visibility.equals=" + UPDATED_VISIBILITY);
//    }
//
//    @Test
//    void getAllOffersByVisibilityIsInShouldWork() {
//        // Initialize the database
//        insertedOffer = offerRepository.save(offer).block();
//
//        // Get all the offerList where visibility in
//        defaultOfferFiltering("visibility.in=" + DEFAULT_VISIBILITY + "," + UPDATED_VISIBILITY, "visibility.in=" + UPDATED_VISIBILITY);
//    }
//
//    @Test
//    void getAllOffersByVisibilityIsNullOrNotNull() {
//        // Initialize the database
//        insertedOffer = offerRepository.save(offer).block();
//
//        // Get all the offerList where visibility is not null
//        defaultOfferFiltering("visibility.specified=true", "visibility.specified=false");
//    }
//
//    @Test
//    void getAllOffersByCreatedDateIsEqualToSomething() {
//        // Initialize the database
//        insertedOffer = offerRepository.save(offer).block();
//
//        // Get all the offerList where createdDate equals to
//        defaultOfferFiltering("createdDate.equals=" + DEFAULT_CREATED_DATE, "createdDate.equals=" + UPDATED_CREATED_DATE);
//    }
//
//    @Test
//    void getAllOffersByCreatedDateIsInShouldWork() {
//        // Initialize the database
//        insertedOffer = offerRepository.save(offer).block();
//
//        // Get all the offerList where createdDate in
//        defaultOfferFiltering(
//            "createdDate.in=" + DEFAULT_CREATED_DATE + "," + UPDATED_CREATED_DATE,
//            "createdDate.in=" + UPDATED_CREATED_DATE
//        );
//    }
//
//    @Test
//    void getAllOffersByCreatedDateIsNullOrNotNull() {
//        // Initialize the database
//        insertedOffer = offerRepository.save(offer).block();
//
//        // Get all the offerList where createdDate is not null
//        defaultOfferFiltering("createdDate.specified=true", "createdDate.specified=false");
//    }
//
//    @Test
//    void getAllOffersByLastModifiedDateIsEqualToSomething() {
//        // Initialize the database
//        insertedOffer = offerRepository.save(offer).block();
//
//        // Get all the offerList where lastModifiedDate equals to
//        defaultOfferFiltering(
//            "lastModifiedDate.equals=" + DEFAULT_LAST_MODIFIED_DATE,
//            "lastModifiedDate.equals=" + UPDATED_LAST_MODIFIED_DATE
//        );
//    }
//
//    @Test
//    void getAllOffersByLastModifiedDateIsInShouldWork() {
//        // Initialize the database
//        insertedOffer = offerRepository.save(offer).block();
//
//        // Get all the offerList where lastModifiedDate in
//        defaultOfferFiltering(
//            "lastModifiedDate.in=" + DEFAULT_LAST_MODIFIED_DATE + "," + UPDATED_LAST_MODIFIED_DATE,
//            "lastModifiedDate.in=" + UPDATED_LAST_MODIFIED_DATE
//        );
//    }
//
//    @Test
//    void getAllOffersByLastModifiedDateIsNullOrNotNull() {
//        // Initialize the database
//        insertedOffer = offerRepository.save(offer).block();
//
//        // Get all the offerList where lastModifiedDate is not null
//        defaultOfferFiltering("lastModifiedDate.specified=true", "lastModifiedDate.specified=false");
//    }
//
//    @Test
//    void getAllOffersByCreatedByIsEqualToSomething() {
//        // Initialize the database
//        insertedOffer = offerRepository.save(offer).block();
//
//        // Get all the offerList where createdBy equals to
//        defaultOfferFiltering("createdBy.equals=" + DEFAULT_CREATED_BY, "createdBy.equals=" + UPDATED_CREATED_BY);
//    }
//
//    @Test
//    void getAllOffersByCreatedByIsInShouldWork() {
//        // Initialize the database
//        insertedOffer = offerRepository.save(offer).block();
//
//        // Get all the offerList where createdBy in
//        defaultOfferFiltering("createdBy.in=" + DEFAULT_CREATED_BY + "," + UPDATED_CREATED_BY, "createdBy.in=" + UPDATED_CREATED_BY);
//    }
//
//    @Test
//    void getAllOffersByCreatedByIsNullOrNotNull() {
//        // Initialize the database
//        insertedOffer = offerRepository.save(offer).block();
//
//        // Get all the offerList where createdBy is not null
//        defaultOfferFiltering("createdBy.specified=true", "createdBy.specified=false");
//    }
//
//    @Test
//    void getAllOffersByCreatedByContainsSomething() {
//        // Initialize the database
//        insertedOffer = offerRepository.save(offer).block();
//
//        // Get all the offerList where createdBy contains
//        defaultOfferFiltering("createdBy.contains=" + DEFAULT_CREATED_BY, "createdBy.contains=" + UPDATED_CREATED_BY);
//    }
//
//    @Test
//    void getAllOffersByCreatedByNotContainsSomething() {
//        // Initialize the database
//        insertedOffer = offerRepository.save(offer).block();
//
//        // Get all the offerList where createdBy does not contain
//        defaultOfferFiltering("createdBy.doesNotContain=" + UPDATED_CREATED_BY, "createdBy.doesNotContain=" + DEFAULT_CREATED_BY);
//    }
//
//    @Test
//    void getAllOffersByLastModifiedByIsEqualToSomething() {
//        // Initialize the database
//        insertedOffer = offerRepository.save(offer).block();
//
//        // Get all the offerList where lastModifiedBy equals to
//        defaultOfferFiltering("lastModifiedBy.equals=" + DEFAULT_LAST_MODIFIED_BY, "lastModifiedBy.equals=" + UPDATED_LAST_MODIFIED_BY);
//    }
//
//    @Test
//    void getAllOffersByLastModifiedByIsInShouldWork() {
//        // Initialize the database
//        insertedOffer = offerRepository.save(offer).block();
//
//        // Get all the offerList where lastModifiedBy in
//        defaultOfferFiltering(
//            "lastModifiedBy.in=" + DEFAULT_LAST_MODIFIED_BY + "," + UPDATED_LAST_MODIFIED_BY,
//            "lastModifiedBy.in=" + UPDATED_LAST_MODIFIED_BY
//        );
//    }
//
//    @Test
//    void getAllOffersByLastModifiedByIsNullOrNotNull() {
//        // Initialize the database
//        insertedOffer = offerRepository.save(offer).block();
//
//        // Get all the offerList where lastModifiedBy is not null
//        defaultOfferFiltering("lastModifiedBy.specified=true", "lastModifiedBy.specified=false");
//    }
//
//    @Test
//    void getAllOffersByLastModifiedByContainsSomething() {
//        // Initialize the database
//        insertedOffer = offerRepository.save(offer).block();
//
//        // Get all the offerList where lastModifiedBy contains
//        defaultOfferFiltering("lastModifiedBy.contains=" + DEFAULT_LAST_MODIFIED_BY, "lastModifiedBy.contains=" + UPDATED_LAST_MODIFIED_BY);
//    }
//
//    @Test
//    void getAllOffersByLastModifiedByNotContainsSomething() {
//        // Initialize the database
//        insertedOffer = offerRepository.save(offer).block();
//
//        // Get all the offerList where lastModifiedBy does not contain
//        defaultOfferFiltering(
//            "lastModifiedBy.doesNotContain=" + UPDATED_LAST_MODIFIED_BY,
//            "lastModifiedBy.doesNotContain=" + DEFAULT_LAST_MODIFIED_BY
//        );
//    }
//
//    @Test
//    void getAllOffersByOwnerIsEqualToSomething() {
//        Profile owner = ProfileResourceIT.createEntity();
//        profileRepository.save(owner).block();
//        Long ownerId = owner.getId();
//        offer.setOwnerId(ownerId);
//        insertedOffer = offerRepository.save(offer).block();
//        // Get all the offerList where owner equals to ownerId
//        defaultOfferShouldBeFound("ownerId.equals=" + ownerId);
//
//        // Get all the offerList where owner equals to (ownerId + 1)
//        defaultOfferShouldNotBeFound("ownerId.equals=" + (ownerId + 1));
//    }
//
//    @Test
//    void getAllOffersByOffertypeIsEqualToSomething() {
//        OfferType offertype = OfferTypeResourceIT.createEntity();
//        offerTypeRepository.save(offertype).block();
//        Long offertypeId = offertype.getId();
//        offer.setOffertypeId(offertypeId);
//        insertedOffer = offerRepository.save(offer).block();
//        // Get all the offerList where offertype equals to offertypeId
//        defaultOfferShouldBeFound("offertypeId.equals=" + offertypeId);
//
//        // Get all the offerList where offertype equals to (offertypeId + 1)
//        defaultOfferShouldNotBeFound("offertypeId.equals=" + (offertypeId + 1));
//    }
//
//    private void defaultOfferFiltering(String shouldBeFound, String shouldNotBeFound) {
//        defaultOfferShouldBeFound(shouldBeFound);
//        defaultOfferShouldNotBeFound(shouldNotBeFound);
//    }
//
//    /**
//     * Executes the search, and checks that the default entity is returned.
//     */
//    private void defaultOfferShouldBeFound(String filter) {
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
//            .value(hasItem(offer.getId().intValue()))
//            .jsonPath("$.[*].name")
//            .value(hasItem(DEFAULT_NAME))
//            .jsonPath("$.[*].description")
//            .value(hasItem(DEFAULT_DESCRIPTION))
//            .jsonPath("$.[*].rating")
//            .value(hasItem(DEFAULT_RATING))
//            .jsonPath("$.[*].status")
//            .value(hasItem(DEFAULT_STATUS.toString()))
//            .jsonPath("$.[*].visibility")
//            .value(hasItem(DEFAULT_VISIBILITY))
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
//    private void defaultOfferShouldNotBeFound(String filter) {
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
//    void getNonExistingOffer() {
//        // Get the offer
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
//    void putExistingOffer() throws Exception {
//        // Initialize the database
//        insertedOffer = offerRepository.save(offer).block();
//
//        long databaseSizeBeforeUpdate = getRepositoryCount();
//
//        // Update the offer
//        Offer updatedOffer = offerRepository.findById(offer.getId()).block();
//        updatedOffer
//            .name(UPDATED_NAME)
//            .description(UPDATED_DESCRIPTION)
//            .rating(UPDATED_RATING)
//            .status(UPDATED_STATUS)
//            .visibility(UPDATED_VISIBILITY)
//            .createdDate(UPDATED_CREATED_DATE)
//            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
//            .createdBy(UPDATED_CREATED_BY)
//            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY);
//        OfferDTO offerDTO = offerMapper.toDto(updatedOffer);
//
//        webTestClient
//            .put()
//            .uri(ENTITY_API_URL_ID, offerDTO.getId())
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(om.writeValueAsBytes(offerDTO))
//            .exchange()
//            .expectStatus()
//            .isOk();
//
//        // Validate the Offer in the database
//        assertSameRepositoryCount(databaseSizeBeforeUpdate);
//        assertPersistedOfferToMatchAllProperties(updatedOffer);
//    }
//
//    @Test
//    void putNonExistingOffer() throws Exception {
//        long databaseSizeBeforeUpdate = getRepositoryCount();
//        offer.setId(longCount.incrementAndGet());
//
//        // Create the Offer
//        OfferDTO offerDTO = offerMapper.toDto(offer);
//
//        // If the entity doesn't have an ID, it will throw BadRequestAlertException
//        webTestClient
//            .put()
//            .uri(ENTITY_API_URL_ID, offerDTO.getId())
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(om.writeValueAsBytes(offerDTO))
//            .exchange()
//            .expectStatus()
//            .isBadRequest();
//
//        // Validate the Offer in the database
//        assertSameRepositoryCount(databaseSizeBeforeUpdate);
//    }
//
//    @Test
//    void putWithIdMismatchOffer() throws Exception {
//        long databaseSizeBeforeUpdate = getRepositoryCount();
//        offer.setId(longCount.incrementAndGet());
//
//        // Create the Offer
//        OfferDTO offerDTO = offerMapper.toDto(offer);
//
//        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
//        webTestClient
//            .put()
//            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(om.writeValueAsBytes(offerDTO))
//            .exchange()
//            .expectStatus()
//            .isBadRequest();
//
//        // Validate the Offer in the database
//        assertSameRepositoryCount(databaseSizeBeforeUpdate);
//    }
//
//    @Test
//    void putWithMissingIdPathParamOffer() throws Exception {
//        long databaseSizeBeforeUpdate = getRepositoryCount();
//        offer.setId(longCount.incrementAndGet());
//
//        // Create the Offer
//        OfferDTO offerDTO = offerMapper.toDto(offer);
//
//        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
//        webTestClient
//            .put()
//            .uri(ENTITY_API_URL)
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(om.writeValueAsBytes(offerDTO))
//            .exchange()
//            .expectStatus()
//            .isEqualTo(405);
//
//        // Validate the Offer in the database
//        assertSameRepositoryCount(databaseSizeBeforeUpdate);
//    }
//
//    @Test
//    void partialUpdateOfferWithPatch() throws Exception {
//        // Initialize the database
//        insertedOffer = offerRepository.save(offer).block();
//
//        long databaseSizeBeforeUpdate = getRepositoryCount();
//
//        // Update the offer using partial update
//        Offer partialUpdatedOffer = new Offer();
//        partialUpdatedOffer.setId(offer.getId());
//
//        partialUpdatedOffer.name(UPDATED_NAME).rating(UPDATED_RATING).createdDate(UPDATED_CREATED_DATE).createdBy(UPDATED_CREATED_BY);
//
//        webTestClient
//            .patch()
//            .uri(ENTITY_API_URL_ID, partialUpdatedOffer.getId())
//            .contentType(MediaType.valueOf("application/merge-patch+json"))
//            .bodyValue(om.writeValueAsBytes(partialUpdatedOffer))
//            .exchange()
//            .expectStatus()
//            .isOk();
//
//        // Validate the Offer in the database
//
//        assertSameRepositoryCount(databaseSizeBeforeUpdate);
//        assertOfferUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedOffer, offer), getPersistedOffer(offer));
//    }
//
//    @Test
//    void fullUpdateOfferWithPatch() throws Exception {
//        // Initialize the database
//        insertedOffer = offerRepository.save(offer).block();
//
//        long databaseSizeBeforeUpdate = getRepositoryCount();
//
//        // Update the offer using partial update
//        Offer partialUpdatedOffer = new Offer();
//        partialUpdatedOffer.setId(offer.getId());
//
//        partialUpdatedOffer
//            .name(UPDATED_NAME)
//            .description(UPDATED_DESCRIPTION)
//            .rating(UPDATED_RATING)
//            .status(UPDATED_STATUS)
//            .visibility(UPDATED_VISIBILITY)
//            .createdDate(UPDATED_CREATED_DATE)
//            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
//            .createdBy(UPDATED_CREATED_BY)
//            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY);
//
//        webTestClient
//            .patch()
//            .uri(ENTITY_API_URL_ID, partialUpdatedOffer.getId())
//            .contentType(MediaType.valueOf("application/merge-patch+json"))
//            .bodyValue(om.writeValueAsBytes(partialUpdatedOffer))
//            .exchange()
//            .expectStatus()
//            .isOk();
//
//        // Validate the Offer in the database
//
//        assertSameRepositoryCount(databaseSizeBeforeUpdate);
//        assertOfferUpdatableFieldsEquals(partialUpdatedOffer, getPersistedOffer(partialUpdatedOffer));
//    }
//
//    @Test
//    void patchNonExistingOffer() throws Exception {
//        long databaseSizeBeforeUpdate = getRepositoryCount();
//        offer.setId(longCount.incrementAndGet());
//
//        // Create the Offer
//        OfferDTO offerDTO = offerMapper.toDto(offer);
//
//        // If the entity doesn't have an ID, it will throw BadRequestAlertException
//        webTestClient
//            .patch()
//            .uri(ENTITY_API_URL_ID, offerDTO.getId())
//            .contentType(MediaType.valueOf("application/merge-patch+json"))
//            .bodyValue(om.writeValueAsBytes(offerDTO))
//            .exchange()
//            .expectStatus()
//            .isBadRequest();
//
//        // Validate the Offer in the database
//        assertSameRepositoryCount(databaseSizeBeforeUpdate);
//    }
//
//    @Test
//    void patchWithIdMismatchOffer() throws Exception {
//        long databaseSizeBeforeUpdate = getRepositoryCount();
//        offer.setId(longCount.incrementAndGet());
//
//        // Create the Offer
//        OfferDTO offerDTO = offerMapper.toDto(offer);
//
//        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
//        webTestClient
//            .patch()
//            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
//            .contentType(MediaType.valueOf("application/merge-patch+json"))
//            .bodyValue(om.writeValueAsBytes(offerDTO))
//            .exchange()
//            .expectStatus()
//            .isBadRequest();
//
//        // Validate the Offer in the database
//        assertSameRepositoryCount(databaseSizeBeforeUpdate);
//    }
//
//    @Test
//    void patchWithMissingIdPathParamOffer() throws Exception {
//        long databaseSizeBeforeUpdate = getRepositoryCount();
//        offer.setId(longCount.incrementAndGet());
//
//        // Create the Offer
//        OfferDTO offerDTO = offerMapper.toDto(offer);
//
//        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
//        webTestClient
//            .patch()
//            .uri(ENTITY_API_URL)
//            .contentType(MediaType.valueOf("application/merge-patch+json"))
//            .bodyValue(om.writeValueAsBytes(offerDTO))
//            .exchange()
//            .expectStatus()
//            .isEqualTo(405);
//
//        // Validate the Offer in the database
//        assertSameRepositoryCount(databaseSizeBeforeUpdate);
//    }
//
//    @Test
//    void deleteOffer() {
//        // Initialize the database
//        insertedOffer = offerRepository.save(offer).block();
//
//        long databaseSizeBeforeDelete = getRepositoryCount();
//
//        // Delete the offer
//        webTestClient
//            .delete()
//            .uri(ENTITY_API_URL_ID, offer.getId())
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
//        return offerRepository.count().block();
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
//    protected Offer getPersistedOffer(Offer offer) {
//        return offerRepository.findById(offer.getId()).block();
//    }
//
//    protected void assertPersistedOfferToMatchAllProperties(Offer expectedOffer) {
//        // Test fails because reactive api returns an empty object instead of null
//        // assertOfferAllPropertiesEquals(expectedOffer, getPersistedOffer(expectedOffer));
//        assertOfferUpdatableFieldsEquals(expectedOffer, getPersistedOffer(expectedOffer));
//    }
//
//    protected void assertPersistedOfferToMatchUpdatableProperties(Offer expectedOffer) {
//        // Test fails because reactive api returns an empty object instead of null
//        // assertOfferAllUpdatablePropertiesEquals(expectedOffer, getPersistedOffer(expectedOffer));
//        assertOfferUpdatableFieldsEquals(expectedOffer, getPersistedOffer(expectedOffer));
//    }
//}
