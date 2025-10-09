package com.freelance.app.web.rest;

import static com.freelance.app.domain.OfferPackageAsserts.*;
import static com.freelance.app.web.rest.TestUtil.createUpdateProxyForBean;
import static com.freelance.app.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.freelance.app.IntegrationTest;
import com.freelance.app.domain.Offer;
import com.freelance.app.domain.OfferPackage;
import com.freelance.app.domain.enumeration.PackageTier;
import com.freelance.app.repository.EntityManager;
import com.freelance.app.repository.OfferPackageRepository;
import com.freelance.app.repository.OfferRepository;
import com.freelance.app.service.OfferPackageService;
import com.freelance.app.service.dto.OfferPackageDTO;
import java.math.BigDecimal;
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
 * Integration tests for the {@link OfferPackageResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class OfferPackageResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_PRICE = new BigDecimal(1);
    private static final BigDecimal UPDATED_PRICE = new BigDecimal(2);
    private static final BigDecimal SMALLER_PRICE = new BigDecimal(1 - 1);

    private static final String DEFAULT_CURRENCY = "AAA";
    private static final String UPDATED_CURRENCY = "BBB";

    private static final Integer DEFAULT_DELIVERY_DAYS = 1;
    private static final Integer UPDATED_DELIVERY_DAYS = 2;
    private static final Integer SMALLER_DELIVERY_DAYS = 1 - 1;

    private static final PackageTier DEFAULT_PACKAGE_TIER = PackageTier.BASIC;
    private static final PackageTier UPDATED_PACKAGE_TIER = PackageTier.PREMIUM;

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final Instant DEFAULT_CREATED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_LAST_MODIFIED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_MODIFIED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_MODIFIED_BY = "AAAAAAAAAA";
    private static final String UPDATED_LAST_MODIFIED_BY = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/offer-packages";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private OfferPackageRepository offerPackageRepository;

    @Mock
    private OfferPackageRepository offerPackageRepositoryMock;

    @Autowired
    private OfferPackageMapper offerPackageMapper;

    @Mock
    private OfferPackageService offerPackageServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private OfferPackage offerPackage;

    private OfferPackage insertedOfferPackage;

    @Autowired
    private OfferRepository offerRepository;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OfferPackage createEntity() {
        return new OfferPackage()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .price(DEFAULT_PRICE)
            .currency(DEFAULT_CURRENCY)
            .deliveryDays(DEFAULT_DELIVERY_DAYS)
            .packageTier(DEFAULT_PACKAGE_TIER)
            .active(DEFAULT_ACTIVE)
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
    public static OfferPackage createUpdatedEntity() {
        return new OfferPackage()
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .price(UPDATED_PRICE)
            .currency(UPDATED_CURRENCY)
            .deliveryDays(UPDATED_DELIVERY_DAYS)
            .packageTier(UPDATED_PACKAGE_TIER)
            .active(UPDATED_ACTIVE)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .createdBy(UPDATED_CREATED_BY)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(OfferPackage.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        offerPackage = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedOfferPackage != null) {
            offerPackageRepository.delete(insertedOfferPackage).block();
            insertedOfferPackage = null;
        }
        deleteEntities(em);
    }

    @Test
    void createOfferPackage() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the OfferPackage
        OfferPackageDTO offerPackageDTO = offerPackageMapper.toDto(offerPackage);
        var returnedOfferPackageDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(offerPackageDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(OfferPackageDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the OfferPackage in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedOfferPackage = offerPackageMapper.toEntity(returnedOfferPackageDTO);
        assertOfferPackageUpdatableFieldsEquals(returnedOfferPackage, getPersistedOfferPackage(returnedOfferPackage));

        insertedOfferPackage = returnedOfferPackage;
    }

    @Test
    void createOfferPackageWithExistingId() throws Exception {
        // Create the OfferPackage with an existing ID
        offerPackage.setId(1L);
        OfferPackageDTO offerPackageDTO = offerPackageMapper.toDto(offerPackage);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(offerPackageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the OfferPackage in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        offerPackage.setName(null);

        // Create the OfferPackage, which fails.
        OfferPackageDTO offerPackageDTO = offerPackageMapper.toDto(offerPackage);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(offerPackageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkDescriptionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        offerPackage.setDescription(null);

        // Create the OfferPackage, which fails.
        OfferPackageDTO offerPackageDTO = offerPackageMapper.toDto(offerPackage);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(offerPackageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkPriceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        offerPackage.setPrice(null);

        // Create the OfferPackage, which fails.
        OfferPackageDTO offerPackageDTO = offerPackageMapper.toDto(offerPackage);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(offerPackageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkCurrencyIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        offerPackage.setCurrency(null);

        // Create the OfferPackage, which fails.
        OfferPackageDTO offerPackageDTO = offerPackageMapper.toDto(offerPackage);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(offerPackageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkDeliveryDaysIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        offerPackage.setDeliveryDays(null);

        // Create the OfferPackage, which fails.
        OfferPackageDTO offerPackageDTO = offerPackageMapper.toDto(offerPackage);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(offerPackageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkPackageTierIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        offerPackage.setPackageTier(null);

        // Create the OfferPackage, which fails.
        OfferPackageDTO offerPackageDTO = offerPackageMapper.toDto(offerPackage);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(offerPackageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkActiveIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        offerPackage.setActive(null);

        // Create the OfferPackage, which fails.
        OfferPackageDTO offerPackageDTO = offerPackageMapper.toDto(offerPackage);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(offerPackageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkCreatedDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        offerPackage.setCreatedDate(null);

        // Create the OfferPackage, which fails.
        OfferPackageDTO offerPackageDTO = offerPackageMapper.toDto(offerPackage);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(offerPackageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllOfferPackages() {
        // Initialize the database
        insertedOfferPackage = offerPackageRepository.save(offerPackage).block();

        // Get all the offerPackageList
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
            .value(hasItem(offerPackage.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].price")
            .value(hasItem(sameNumber(DEFAULT_PRICE)))
            .jsonPath("$.[*].currency")
            .value(hasItem(DEFAULT_CURRENCY))
            .jsonPath("$.[*].deliveryDays")
            .value(hasItem(DEFAULT_DELIVERY_DAYS))
            .jsonPath("$.[*].packageTier")
            .value(hasItem(DEFAULT_PACKAGE_TIER.toString()))
            .jsonPath("$.[*].active")
            .value(hasItem(DEFAULT_ACTIVE))
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
    void getAllOfferPackagesWithEagerRelationshipsIsEnabled() {
        when(offerPackageServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(offerPackageServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllOfferPackagesWithEagerRelationshipsIsNotEnabled() {
        when(offerPackageServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(offerPackageRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getOfferPackage() {
        // Initialize the database
        insertedOfferPackage = offerPackageRepository.save(offerPackage).block();

        // Get the offerPackage
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, offerPackage.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(offerPackage.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION))
            .jsonPath("$.price")
            .value(is(sameNumber(DEFAULT_PRICE)))
            .jsonPath("$.currency")
            .value(is(DEFAULT_CURRENCY))
            .jsonPath("$.deliveryDays")
            .value(is(DEFAULT_DELIVERY_DAYS))
            .jsonPath("$.packageTier")
            .value(is(DEFAULT_PACKAGE_TIER.toString()))
            .jsonPath("$.active")
            .value(is(DEFAULT_ACTIVE))
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
    void getOfferPackagesByIdFiltering() {
        // Initialize the database
        insertedOfferPackage = offerPackageRepository.save(offerPackage).block();

        Long id = offerPackage.getId();

        defaultOfferPackageFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultOfferPackageFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultOfferPackageFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllOfferPackagesByNameIsEqualToSomething() {
        // Initialize the database
        insertedOfferPackage = offerPackageRepository.save(offerPackage).block();

        // Get all the offerPackageList where name equals to
        defaultOfferPackageFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    void getAllOfferPackagesByNameIsInShouldWork() {
        // Initialize the database
        insertedOfferPackage = offerPackageRepository.save(offerPackage).block();

        // Get all the offerPackageList where name in
        defaultOfferPackageFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    void getAllOfferPackagesByNameIsNullOrNotNull() {
        // Initialize the database
        insertedOfferPackage = offerPackageRepository.save(offerPackage).block();

        // Get all the offerPackageList where name is not null
        defaultOfferPackageFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    void getAllOfferPackagesByNameContainsSomething() {
        // Initialize the database
        insertedOfferPackage = offerPackageRepository.save(offerPackage).block();

        // Get all the offerPackageList where name contains
        defaultOfferPackageFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    void getAllOfferPackagesByNameNotContainsSomething() {
        // Initialize the database
        insertedOfferPackage = offerPackageRepository.save(offerPackage).block();

        // Get all the offerPackageList where name does not contain
        defaultOfferPackageFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    void getAllOfferPackagesByDescriptionIsEqualToSomething() {
        // Initialize the database
        insertedOfferPackage = offerPackageRepository.save(offerPackage).block();

        // Get all the offerPackageList where description equals to
        defaultOfferPackageFiltering("description.equals=" + DEFAULT_DESCRIPTION, "description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    void getAllOfferPackagesByDescriptionIsInShouldWork() {
        // Initialize the database
        insertedOfferPackage = offerPackageRepository.save(offerPackage).block();

        // Get all the offerPackageList where description in
        defaultOfferPackageFiltering(
            "description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION,
            "description.in=" + UPDATED_DESCRIPTION
        );
    }

    @Test
    void getAllOfferPackagesByDescriptionIsNullOrNotNull() {
        // Initialize the database
        insertedOfferPackage = offerPackageRepository.save(offerPackage).block();

        // Get all the offerPackageList where description is not null
        defaultOfferPackageFiltering("description.specified=true", "description.specified=false");
    }

    @Test
    void getAllOfferPackagesByDescriptionContainsSomething() {
        // Initialize the database
        insertedOfferPackage = offerPackageRepository.save(offerPackage).block();

        // Get all the offerPackageList where description contains
        defaultOfferPackageFiltering("description.contains=" + DEFAULT_DESCRIPTION, "description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    void getAllOfferPackagesByDescriptionNotContainsSomething() {
        // Initialize the database
        insertedOfferPackage = offerPackageRepository.save(offerPackage).block();

        // Get all the offerPackageList where description does not contain
        defaultOfferPackageFiltering(
            "description.doesNotContain=" + UPDATED_DESCRIPTION,
            "description.doesNotContain=" + DEFAULT_DESCRIPTION
        );
    }

    @Test
    void getAllOfferPackagesByPriceIsEqualToSomething() {
        // Initialize the database
        insertedOfferPackage = offerPackageRepository.save(offerPackage).block();

        // Get all the offerPackageList where price equals to
        defaultOfferPackageFiltering("price.equals=" + DEFAULT_PRICE, "price.equals=" + UPDATED_PRICE);
    }

    @Test
    void getAllOfferPackagesByPriceIsInShouldWork() {
        // Initialize the database
        insertedOfferPackage = offerPackageRepository.save(offerPackage).block();

        // Get all the offerPackageList where price in
        defaultOfferPackageFiltering("price.in=" + DEFAULT_PRICE + "," + UPDATED_PRICE, "price.in=" + UPDATED_PRICE);
    }

    @Test
    void getAllOfferPackagesByPriceIsNullOrNotNull() {
        // Initialize the database
        insertedOfferPackage = offerPackageRepository.save(offerPackage).block();

        // Get all the offerPackageList where price is not null
        defaultOfferPackageFiltering("price.specified=true", "price.specified=false");
    }

    @Test
    void getAllOfferPackagesByPriceIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedOfferPackage = offerPackageRepository.save(offerPackage).block();

        // Get all the offerPackageList where price is greater than or equal to
        defaultOfferPackageFiltering("price.greaterThanOrEqual=" + DEFAULT_PRICE, "price.greaterThanOrEqual=" + UPDATED_PRICE);
    }

    @Test
    void getAllOfferPackagesByPriceIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedOfferPackage = offerPackageRepository.save(offerPackage).block();

        // Get all the offerPackageList where price is less than or equal to
        defaultOfferPackageFiltering("price.lessThanOrEqual=" + DEFAULT_PRICE, "price.lessThanOrEqual=" + SMALLER_PRICE);
    }

    @Test
    void getAllOfferPackagesByPriceIsLessThanSomething() {
        // Initialize the database
        insertedOfferPackage = offerPackageRepository.save(offerPackage).block();

        // Get all the offerPackageList where price is less than
        defaultOfferPackageFiltering("price.lessThan=" + UPDATED_PRICE, "price.lessThan=" + DEFAULT_PRICE);
    }

    @Test
    void getAllOfferPackagesByPriceIsGreaterThanSomething() {
        // Initialize the database
        insertedOfferPackage = offerPackageRepository.save(offerPackage).block();

        // Get all the offerPackageList where price is greater than
        defaultOfferPackageFiltering("price.greaterThan=" + SMALLER_PRICE, "price.greaterThan=" + DEFAULT_PRICE);
    }

    @Test
    void getAllOfferPackagesByCurrencyIsEqualToSomething() {
        // Initialize the database
        insertedOfferPackage = offerPackageRepository.save(offerPackage).block();

        // Get all the offerPackageList where currency equals to
        defaultOfferPackageFiltering("currency.equals=" + DEFAULT_CURRENCY, "currency.equals=" + UPDATED_CURRENCY);
    }

    @Test
    void getAllOfferPackagesByCurrencyIsInShouldWork() {
        // Initialize the database
        insertedOfferPackage = offerPackageRepository.save(offerPackage).block();

        // Get all the offerPackageList where currency in
        defaultOfferPackageFiltering("currency.in=" + DEFAULT_CURRENCY + "," + UPDATED_CURRENCY, "currency.in=" + UPDATED_CURRENCY);
    }

    @Test
    void getAllOfferPackagesByCurrencyIsNullOrNotNull() {
        // Initialize the database
        insertedOfferPackage = offerPackageRepository.save(offerPackage).block();

        // Get all the offerPackageList where currency is not null
        defaultOfferPackageFiltering("currency.specified=true", "currency.specified=false");
    }

    @Test
    void getAllOfferPackagesByCurrencyContainsSomething() {
        // Initialize the database
        insertedOfferPackage = offerPackageRepository.save(offerPackage).block();

        // Get all the offerPackageList where currency contains
        defaultOfferPackageFiltering("currency.contains=" + DEFAULT_CURRENCY, "currency.contains=" + UPDATED_CURRENCY);
    }

    @Test
    void getAllOfferPackagesByCurrencyNotContainsSomething() {
        // Initialize the database
        insertedOfferPackage = offerPackageRepository.save(offerPackage).block();

        // Get all the offerPackageList where currency does not contain
        defaultOfferPackageFiltering("currency.doesNotContain=" + UPDATED_CURRENCY, "currency.doesNotContain=" + DEFAULT_CURRENCY);
    }

    @Test
    void getAllOfferPackagesByDeliveryDaysIsEqualToSomething() {
        // Initialize the database
        insertedOfferPackage = offerPackageRepository.save(offerPackage).block();

        // Get all the offerPackageList where deliveryDays equals to
        defaultOfferPackageFiltering("deliveryDays.equals=" + DEFAULT_DELIVERY_DAYS, "deliveryDays.equals=" + UPDATED_DELIVERY_DAYS);
    }

    @Test
    void getAllOfferPackagesByDeliveryDaysIsInShouldWork() {
        // Initialize the database
        insertedOfferPackage = offerPackageRepository.save(offerPackage).block();

        // Get all the offerPackageList where deliveryDays in
        defaultOfferPackageFiltering(
            "deliveryDays.in=" + DEFAULT_DELIVERY_DAYS + "," + UPDATED_DELIVERY_DAYS,
            "deliveryDays.in=" + UPDATED_DELIVERY_DAYS
        );
    }

    @Test
    void getAllOfferPackagesByDeliveryDaysIsNullOrNotNull() {
        // Initialize the database
        insertedOfferPackage = offerPackageRepository.save(offerPackage).block();

        // Get all the offerPackageList where deliveryDays is not null
        defaultOfferPackageFiltering("deliveryDays.specified=true", "deliveryDays.specified=false");
    }

    @Test
    void getAllOfferPackagesByDeliveryDaysIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedOfferPackage = offerPackageRepository.save(offerPackage).block();

        // Get all the offerPackageList where deliveryDays is greater than or equal to
        defaultOfferPackageFiltering(
            "deliveryDays.greaterThanOrEqual=" + DEFAULT_DELIVERY_DAYS,
            "deliveryDays.greaterThanOrEqual=" + UPDATED_DELIVERY_DAYS
        );
    }

    @Test
    void getAllOfferPackagesByDeliveryDaysIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedOfferPackage = offerPackageRepository.save(offerPackage).block();

        // Get all the offerPackageList where deliveryDays is less than or equal to
        defaultOfferPackageFiltering(
            "deliveryDays.lessThanOrEqual=" + DEFAULT_DELIVERY_DAYS,
            "deliveryDays.lessThanOrEqual=" + SMALLER_DELIVERY_DAYS
        );
    }

    @Test
    void getAllOfferPackagesByDeliveryDaysIsLessThanSomething() {
        // Initialize the database
        insertedOfferPackage = offerPackageRepository.save(offerPackage).block();

        // Get all the offerPackageList where deliveryDays is less than
        defaultOfferPackageFiltering("deliveryDays.lessThan=" + UPDATED_DELIVERY_DAYS, "deliveryDays.lessThan=" + DEFAULT_DELIVERY_DAYS);
    }

    @Test
    void getAllOfferPackagesByDeliveryDaysIsGreaterThanSomething() {
        // Initialize the database
        insertedOfferPackage = offerPackageRepository.save(offerPackage).block();

        // Get all the offerPackageList where deliveryDays is greater than
        defaultOfferPackageFiltering(
            "deliveryDays.greaterThan=" + SMALLER_DELIVERY_DAYS,
            "deliveryDays.greaterThan=" + DEFAULT_DELIVERY_DAYS
        );
    }

    @Test
    void getAllOfferPackagesByPackageTierIsEqualToSomething() {
        // Initialize the database
        insertedOfferPackage = offerPackageRepository.save(offerPackage).block();

        // Get all the offerPackageList where packageTier equals to
        defaultOfferPackageFiltering("packageTier.equals=" + DEFAULT_PACKAGE_TIER, "packageTier.equals=" + UPDATED_PACKAGE_TIER);
    }

    @Test
    void getAllOfferPackagesByPackageTierIsInShouldWork() {
        // Initialize the database
        insertedOfferPackage = offerPackageRepository.save(offerPackage).block();

        // Get all the offerPackageList where packageTier in
        defaultOfferPackageFiltering(
            "packageTier.in=" + DEFAULT_PACKAGE_TIER + "," + UPDATED_PACKAGE_TIER,
            "packageTier.in=" + UPDATED_PACKAGE_TIER
        );
    }

    @Test
    void getAllOfferPackagesByPackageTierIsNullOrNotNull() {
        // Initialize the database
        insertedOfferPackage = offerPackageRepository.save(offerPackage).block();

        // Get all the offerPackageList where packageTier is not null
        defaultOfferPackageFiltering("packageTier.specified=true", "packageTier.specified=false");
    }

    @Test
    void getAllOfferPackagesByActiveIsEqualToSomething() {
        // Initialize the database
        insertedOfferPackage = offerPackageRepository.save(offerPackage).block();

        // Get all the offerPackageList where active equals to
        defaultOfferPackageFiltering("active.equals=" + DEFAULT_ACTIVE, "active.equals=" + UPDATED_ACTIVE);
    }

    @Test
    void getAllOfferPackagesByActiveIsInShouldWork() {
        // Initialize the database
        insertedOfferPackage = offerPackageRepository.save(offerPackage).block();

        // Get all the offerPackageList where active in
        defaultOfferPackageFiltering("active.in=" + DEFAULT_ACTIVE + "," + UPDATED_ACTIVE, "active.in=" + UPDATED_ACTIVE);
    }

    @Test
    void getAllOfferPackagesByActiveIsNullOrNotNull() {
        // Initialize the database
        insertedOfferPackage = offerPackageRepository.save(offerPackage).block();

        // Get all the offerPackageList where active is not null
        defaultOfferPackageFiltering("active.specified=true", "active.specified=false");
    }

    @Test
    void getAllOfferPackagesByCreatedDateIsEqualToSomething() {
        // Initialize the database
        insertedOfferPackage = offerPackageRepository.save(offerPackage).block();

        // Get all the offerPackageList where createdDate equals to
        defaultOfferPackageFiltering("createdDate.equals=" + DEFAULT_CREATED_DATE, "createdDate.equals=" + UPDATED_CREATED_DATE);
    }

    @Test
    void getAllOfferPackagesByCreatedDateIsInShouldWork() {
        // Initialize the database
        insertedOfferPackage = offerPackageRepository.save(offerPackage).block();

        // Get all the offerPackageList where createdDate in
        defaultOfferPackageFiltering(
            "createdDate.in=" + DEFAULT_CREATED_DATE + "," + UPDATED_CREATED_DATE,
            "createdDate.in=" + UPDATED_CREATED_DATE
        );
    }

    @Test
    void getAllOfferPackagesByCreatedDateIsNullOrNotNull() {
        // Initialize the database
        insertedOfferPackage = offerPackageRepository.save(offerPackage).block();

        // Get all the offerPackageList where createdDate is not null
        defaultOfferPackageFiltering("createdDate.specified=true", "createdDate.specified=false");
    }

    @Test
    void getAllOfferPackagesByLastModifiedDateIsEqualToSomething() {
        // Initialize the database
        insertedOfferPackage = offerPackageRepository.save(offerPackage).block();

        // Get all the offerPackageList where lastModifiedDate equals to
        defaultOfferPackageFiltering(
            "lastModifiedDate.equals=" + DEFAULT_LAST_MODIFIED_DATE,
            "lastModifiedDate.equals=" + UPDATED_LAST_MODIFIED_DATE
        );
    }

    @Test
    void getAllOfferPackagesByLastModifiedDateIsInShouldWork() {
        // Initialize the database
        insertedOfferPackage = offerPackageRepository.save(offerPackage).block();

        // Get all the offerPackageList where lastModifiedDate in
        defaultOfferPackageFiltering(
            "lastModifiedDate.in=" + DEFAULT_LAST_MODIFIED_DATE + "," + UPDATED_LAST_MODIFIED_DATE,
            "lastModifiedDate.in=" + UPDATED_LAST_MODIFIED_DATE
        );
    }

    @Test
    void getAllOfferPackagesByLastModifiedDateIsNullOrNotNull() {
        // Initialize the database
        insertedOfferPackage = offerPackageRepository.save(offerPackage).block();

        // Get all the offerPackageList where lastModifiedDate is not null
        defaultOfferPackageFiltering("lastModifiedDate.specified=true", "lastModifiedDate.specified=false");
    }

    @Test
    void getAllOfferPackagesByCreatedByIsEqualToSomething() {
        // Initialize the database
        insertedOfferPackage = offerPackageRepository.save(offerPackage).block();

        // Get all the offerPackageList where createdBy equals to
        defaultOfferPackageFiltering("createdBy.equals=" + DEFAULT_CREATED_BY, "createdBy.equals=" + UPDATED_CREATED_BY);
    }

    @Test
    void getAllOfferPackagesByCreatedByIsInShouldWork() {
        // Initialize the database
        insertedOfferPackage = offerPackageRepository.save(offerPackage).block();

        // Get all the offerPackageList where createdBy in
        defaultOfferPackageFiltering("createdBy.in=" + DEFAULT_CREATED_BY + "," + UPDATED_CREATED_BY, "createdBy.in=" + UPDATED_CREATED_BY);
    }

    @Test
    void getAllOfferPackagesByCreatedByIsNullOrNotNull() {
        // Initialize the database
        insertedOfferPackage = offerPackageRepository.save(offerPackage).block();

        // Get all the offerPackageList where createdBy is not null
        defaultOfferPackageFiltering("createdBy.specified=true", "createdBy.specified=false");
    }

    @Test
    void getAllOfferPackagesByCreatedByContainsSomething() {
        // Initialize the database
        insertedOfferPackage = offerPackageRepository.save(offerPackage).block();

        // Get all the offerPackageList where createdBy contains
        defaultOfferPackageFiltering("createdBy.contains=" + DEFAULT_CREATED_BY, "createdBy.contains=" + UPDATED_CREATED_BY);
    }

    @Test
    void getAllOfferPackagesByCreatedByNotContainsSomething() {
        // Initialize the database
        insertedOfferPackage = offerPackageRepository.save(offerPackage).block();

        // Get all the offerPackageList where createdBy does not contain
        defaultOfferPackageFiltering("createdBy.doesNotContain=" + UPDATED_CREATED_BY, "createdBy.doesNotContain=" + DEFAULT_CREATED_BY);
    }

    @Test
    void getAllOfferPackagesByLastModifiedByIsEqualToSomething() {
        // Initialize the database
        insertedOfferPackage = offerPackageRepository.save(offerPackage).block();

        // Get all the offerPackageList where lastModifiedBy equals to
        defaultOfferPackageFiltering(
            "lastModifiedBy.equals=" + DEFAULT_LAST_MODIFIED_BY,
            "lastModifiedBy.equals=" + UPDATED_LAST_MODIFIED_BY
        );
    }

    @Test
    void getAllOfferPackagesByLastModifiedByIsInShouldWork() {
        // Initialize the database
        insertedOfferPackage = offerPackageRepository.save(offerPackage).block();

        // Get all the offerPackageList where lastModifiedBy in
        defaultOfferPackageFiltering(
            "lastModifiedBy.in=" + DEFAULT_LAST_MODIFIED_BY + "," + UPDATED_LAST_MODIFIED_BY,
            "lastModifiedBy.in=" + UPDATED_LAST_MODIFIED_BY
        );
    }

    @Test
    void getAllOfferPackagesByLastModifiedByIsNullOrNotNull() {
        // Initialize the database
        insertedOfferPackage = offerPackageRepository.save(offerPackage).block();

        // Get all the offerPackageList where lastModifiedBy is not null
        defaultOfferPackageFiltering("lastModifiedBy.specified=true", "lastModifiedBy.specified=false");
    }

    @Test
    void getAllOfferPackagesByLastModifiedByContainsSomething() {
        // Initialize the database
        insertedOfferPackage = offerPackageRepository.save(offerPackage).block();

        // Get all the offerPackageList where lastModifiedBy contains
        defaultOfferPackageFiltering(
            "lastModifiedBy.contains=" + DEFAULT_LAST_MODIFIED_BY,
            "lastModifiedBy.contains=" + UPDATED_LAST_MODIFIED_BY
        );
    }

    @Test
    void getAllOfferPackagesByLastModifiedByNotContainsSomething() {
        // Initialize the database
        insertedOfferPackage = offerPackageRepository.save(offerPackage).block();

        // Get all the offerPackageList where lastModifiedBy does not contain
        defaultOfferPackageFiltering(
            "lastModifiedBy.doesNotContain=" + UPDATED_LAST_MODIFIED_BY,
            "lastModifiedBy.doesNotContain=" + DEFAULT_LAST_MODIFIED_BY
        );
    }

    @Test
    void getAllOfferPackagesByOfferIsEqualToSomething() {
        Offer offer = OfferResourceIT.createEntity();
        offerRepository.save(offer).block();
        Long offerId = offer.getId();
        offerPackage.setOfferId(offerId);
        insertedOfferPackage = offerPackageRepository.save(offerPackage).block();
        // Get all the offerPackageList where offer equals to offerId
        defaultOfferPackageShouldBeFound("offerId.equals=" + offerId);

        // Get all the offerPackageList where offer equals to (offerId + 1)
        defaultOfferPackageShouldNotBeFound("offerId.equals=" + (offerId + 1));
    }

    private void defaultOfferPackageFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultOfferPackageShouldBeFound(shouldBeFound);
        defaultOfferPackageShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultOfferPackageShouldBeFound(String filter) {
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
            .value(hasItem(offerPackage.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].price")
            .value(hasItem(sameNumber(DEFAULT_PRICE)))
            .jsonPath("$.[*].currency")
            .value(hasItem(DEFAULT_CURRENCY))
            .jsonPath("$.[*].deliveryDays")
            .value(hasItem(DEFAULT_DELIVERY_DAYS))
            .jsonPath("$.[*].packageTier")
            .value(hasItem(DEFAULT_PACKAGE_TIER.toString()))
            .jsonPath("$.[*].active")
            .value(hasItem(DEFAULT_ACTIVE))
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
    private void defaultOfferPackageShouldNotBeFound(String filter) {
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
    void getNonExistingOfferPackage() {
        // Get the offerPackage
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingOfferPackage() throws Exception {
        // Initialize the database
        insertedOfferPackage = offerPackageRepository.save(offerPackage).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the offerPackage
        OfferPackage updatedOfferPackage = offerPackageRepository.findById(offerPackage.getId()).block();
        updatedOfferPackage
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .price(UPDATED_PRICE)
            .currency(UPDATED_CURRENCY)
            .deliveryDays(UPDATED_DELIVERY_DAYS)
            .packageTier(UPDATED_PACKAGE_TIER)
            .active(UPDATED_ACTIVE)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .createdBy(UPDATED_CREATED_BY)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY);
        OfferPackageDTO offerPackageDTO = offerPackageMapper.toDto(updatedOfferPackage);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, offerPackageDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(offerPackageDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the OfferPackage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedOfferPackageToMatchAllProperties(updatedOfferPackage);
    }

    @Test
    void putNonExistingOfferPackage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        offerPackage.setId(longCount.incrementAndGet());

        // Create the OfferPackage
        OfferPackageDTO offerPackageDTO = offerPackageMapper.toDto(offerPackage);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, offerPackageDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(offerPackageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the OfferPackage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchOfferPackage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        offerPackage.setId(longCount.incrementAndGet());

        // Create the OfferPackage
        OfferPackageDTO offerPackageDTO = offerPackageMapper.toDto(offerPackage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(offerPackageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the OfferPackage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamOfferPackage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        offerPackage.setId(longCount.incrementAndGet());

        // Create the OfferPackage
        OfferPackageDTO offerPackageDTO = offerPackageMapper.toDto(offerPackage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(offerPackageDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the OfferPackage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateOfferPackageWithPatch() throws Exception {
        // Initialize the database
        insertedOfferPackage = offerPackageRepository.save(offerPackage).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the offerPackage using partial update
        OfferPackage partialUpdatedOfferPackage = new OfferPackage();
        partialUpdatedOfferPackage.setId(offerPackage.getId());

        partialUpdatedOfferPackage
            .description(UPDATED_DESCRIPTION)
            .currency(UPDATED_CURRENCY)
            .packageTier(UPDATED_PACKAGE_TIER)
            .createdBy(UPDATED_CREATED_BY)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedOfferPackage.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedOfferPackage))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the OfferPackage in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertOfferPackageUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedOfferPackage, offerPackage),
            getPersistedOfferPackage(offerPackage)
        );
    }

    @Test
    void fullUpdateOfferPackageWithPatch() throws Exception {
        // Initialize the database
        insertedOfferPackage = offerPackageRepository.save(offerPackage).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the offerPackage using partial update
        OfferPackage partialUpdatedOfferPackage = new OfferPackage();
        partialUpdatedOfferPackage.setId(offerPackage.getId());

        partialUpdatedOfferPackage
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .price(UPDATED_PRICE)
            .currency(UPDATED_CURRENCY)
            .deliveryDays(UPDATED_DELIVERY_DAYS)
            .packageTier(UPDATED_PACKAGE_TIER)
            .active(UPDATED_ACTIVE)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .createdBy(UPDATED_CREATED_BY)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedOfferPackage.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedOfferPackage))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the OfferPackage in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertOfferPackageUpdatableFieldsEquals(partialUpdatedOfferPackage, getPersistedOfferPackage(partialUpdatedOfferPackage));
    }

    @Test
    void patchNonExistingOfferPackage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        offerPackage.setId(longCount.incrementAndGet());

        // Create the OfferPackage
        OfferPackageDTO offerPackageDTO = offerPackageMapper.toDto(offerPackage);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, offerPackageDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(offerPackageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the OfferPackage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchOfferPackage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        offerPackage.setId(longCount.incrementAndGet());

        // Create the OfferPackage
        OfferPackageDTO offerPackageDTO = offerPackageMapper.toDto(offerPackage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(offerPackageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the OfferPackage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamOfferPackage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        offerPackage.setId(longCount.incrementAndGet());

        // Create the OfferPackage
        OfferPackageDTO offerPackageDTO = offerPackageMapper.toDto(offerPackage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(offerPackageDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the OfferPackage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteOfferPackage() {
        // Initialize the database
        insertedOfferPackage = offerPackageRepository.save(offerPackage).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the offerPackage
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, offerPackage.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return offerPackageRepository.count().block();
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

    protected OfferPackage getPersistedOfferPackage(OfferPackage offerPackage) {
        return offerPackageRepository.findById(offerPackage.getId()).block();
    }

    protected void assertPersistedOfferPackageToMatchAllProperties(OfferPackage expectedOfferPackage) {
        // Test fails because reactive api returns an empty object instead of null
        // assertOfferPackageAllPropertiesEquals(expectedOfferPackage, getPersistedOfferPackage(expectedOfferPackage));
        assertOfferPackageUpdatableFieldsEquals(expectedOfferPackage, getPersistedOfferPackage(expectedOfferPackage));
    }

    protected void assertPersistedOfferPackageToMatchUpdatableProperties(OfferPackage expectedOfferPackage) {
        // Test fails because reactive api returns an empty object instead of null
        // assertOfferPackageAllUpdatablePropertiesEquals(expectedOfferPackage, getPersistedOfferPackage(expectedOfferPackage));
        assertOfferPackageUpdatableFieldsEquals(expectedOfferPackage, getPersistedOfferPackage(expectedOfferPackage));
    }
}
