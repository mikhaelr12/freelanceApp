package com.freelance.app.web.rest;

import static com.freelance.app.domain.OfferMediaAsserts.*;
import static com.freelance.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.freelance.app.IntegrationTest;
import com.freelance.app.domain.FileObject;
import com.freelance.app.domain.Offer;
import com.freelance.app.domain.OfferMedia;
import com.freelance.app.domain.enumeration.MediaKind;
import com.freelance.app.repository.EntityManager;
import com.freelance.app.repository.FileObjectRepository;
import com.freelance.app.repository.OfferMediaRepository;
import com.freelance.app.repository.OfferRepository;
import com.freelance.app.service.OfferMediaService;
import com.freelance.app.service.dto.OfferMediaDTO;
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
 * Integration tests for the {@link OfferMediaResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class OfferMediaResourceIT {

    private static final MediaKind DEFAULT_MEDIA_KIND = MediaKind.IMAGE;
    private static final MediaKind UPDATED_MEDIA_KIND = MediaKind.VIDEO;

    private static final Boolean DEFAULT_IS_PRIMARY = false;
    private static final Boolean UPDATED_IS_PRIMARY = true;

    private static final String DEFAULT_CAPTION = "AAAAAAAAAA";
    private static final String UPDATED_CAPTION = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_LAST_MODIFIED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_MODIFIED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_MODIFIED_BY = "AAAAAAAAAA";
    private static final String UPDATED_LAST_MODIFIED_BY = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/offer-medias";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private OfferMediaRepository offerMediaRepository;

    @Mock
    private OfferMediaRepository offerMediaRepositoryMock;

    @Autowired
    private OfferMediaMapper offerMediaMapper;

    @Mock
    private OfferMediaService offerMediaServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private OfferMedia offerMedia;

    private OfferMedia insertedOfferMedia;

    @Autowired
    private OfferRepository offerRepository;

    @Autowired
    private FileObjectRepository fileObjectRepository;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OfferMedia createEntity() {
        return new OfferMedia()
            .mediaKind(DEFAULT_MEDIA_KIND)
            .isPrimary(DEFAULT_IS_PRIMARY)
            .caption(DEFAULT_CAPTION)
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
    public static OfferMedia createUpdatedEntity() {
        return new OfferMedia()
            .mediaKind(UPDATED_MEDIA_KIND)
            .isPrimary(UPDATED_IS_PRIMARY)
            .caption(UPDATED_CAPTION)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .createdBy(UPDATED_CREATED_BY)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(OfferMedia.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        offerMedia = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedOfferMedia != null) {
            offerMediaRepository.delete(insertedOfferMedia).block();
            insertedOfferMedia = null;
        }
        deleteEntities(em);
    }

    @Test
    void createOfferMedia() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the OfferMedia
        OfferMediaDTO offerMediaDTO = offerMediaMapper.toDto(offerMedia);
        var returnedOfferMediaDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(offerMediaDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(OfferMediaDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the OfferMedia in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedOfferMedia = offerMediaMapper.toEntity(returnedOfferMediaDTO);
        assertOfferMediaUpdatableFieldsEquals(returnedOfferMedia, getPersistedOfferMedia(returnedOfferMedia));

        insertedOfferMedia = returnedOfferMedia;
    }

    @Test
    void createOfferMediaWithExistingId() throws Exception {
        // Create the OfferMedia with an existing ID
        offerMedia.setId(1L);
        OfferMediaDTO offerMediaDTO = offerMediaMapper.toDto(offerMedia);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(offerMediaDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the OfferMedia in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkMediaKindIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        offerMedia.setMediaKind(null);

        // Create the OfferMedia, which fails.
        OfferMediaDTO offerMediaDTO = offerMediaMapper.toDto(offerMedia);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(offerMediaDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkIsPrimaryIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        offerMedia.setIsPrimary(null);

        // Create the OfferMedia, which fails.
        OfferMediaDTO offerMediaDTO = offerMediaMapper.toDto(offerMedia);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(offerMediaDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkCreatedDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        offerMedia.setCreatedDate(null);

        // Create the OfferMedia, which fails.
        OfferMediaDTO offerMediaDTO = offerMediaMapper.toDto(offerMedia);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(offerMediaDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllOfferMedias() {
        // Initialize the database
        insertedOfferMedia = offerMediaRepository.save(offerMedia).block();

        // Get all the offerMediaList
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
            .value(hasItem(offerMedia.getId().intValue()))
            .jsonPath("$.[*].mediaKind")
            .value(hasItem(DEFAULT_MEDIA_KIND.toString()))
            .jsonPath("$.[*].isPrimary")
            .value(hasItem(DEFAULT_IS_PRIMARY))
            .jsonPath("$.[*].caption")
            .value(hasItem(DEFAULT_CAPTION))
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
    void getAllOfferMediasWithEagerRelationshipsIsEnabled() {
        when(offerMediaServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(offerMediaServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllOfferMediasWithEagerRelationshipsIsNotEnabled() {
        when(offerMediaServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(offerMediaRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getOfferMedia() {
        // Initialize the database
        insertedOfferMedia = offerMediaRepository.save(offerMedia).block();

        // Get the offerMedia
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, offerMedia.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(offerMedia.getId().intValue()))
            .jsonPath("$.mediaKind")
            .value(is(DEFAULT_MEDIA_KIND.toString()))
            .jsonPath("$.isPrimary")
            .value(is(DEFAULT_IS_PRIMARY))
            .jsonPath("$.caption")
            .value(is(DEFAULT_CAPTION))
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
    void getOfferMediasByIdFiltering() {
        // Initialize the database
        insertedOfferMedia = offerMediaRepository.save(offerMedia).block();

        Long id = offerMedia.getId();

        defaultOfferMediaFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultOfferMediaFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultOfferMediaFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllOfferMediasByMediaKindIsEqualToSomething() {
        // Initialize the database
        insertedOfferMedia = offerMediaRepository.save(offerMedia).block();

        // Get all the offerMediaList where mediaKind equals to
        defaultOfferMediaFiltering("mediaKind.equals=" + DEFAULT_MEDIA_KIND, "mediaKind.equals=" + UPDATED_MEDIA_KIND);
    }

    @Test
    void getAllOfferMediasByMediaKindIsInShouldWork() {
        // Initialize the database
        insertedOfferMedia = offerMediaRepository.save(offerMedia).block();

        // Get all the offerMediaList where mediaKind in
        defaultOfferMediaFiltering("mediaKind.in=" + DEFAULT_MEDIA_KIND + "," + UPDATED_MEDIA_KIND, "mediaKind.in=" + UPDATED_MEDIA_KIND);
    }

    @Test
    void getAllOfferMediasByMediaKindIsNullOrNotNull() {
        // Initialize the database
        insertedOfferMedia = offerMediaRepository.save(offerMedia).block();

        // Get all the offerMediaList where mediaKind is not null
        defaultOfferMediaFiltering("mediaKind.specified=true", "mediaKind.specified=false");
    }

    @Test
    void getAllOfferMediasByIsPrimaryIsEqualToSomething() {
        // Initialize the database
        insertedOfferMedia = offerMediaRepository.save(offerMedia).block();

        // Get all the offerMediaList where isPrimary equals to
        defaultOfferMediaFiltering("isPrimary.equals=" + DEFAULT_IS_PRIMARY, "isPrimary.equals=" + UPDATED_IS_PRIMARY);
    }

    @Test
    void getAllOfferMediasByIsPrimaryIsInShouldWork() {
        // Initialize the database
        insertedOfferMedia = offerMediaRepository.save(offerMedia).block();

        // Get all the offerMediaList where isPrimary in
        defaultOfferMediaFiltering("isPrimary.in=" + DEFAULT_IS_PRIMARY + "," + UPDATED_IS_PRIMARY, "isPrimary.in=" + UPDATED_IS_PRIMARY);
    }

    @Test
    void getAllOfferMediasByIsPrimaryIsNullOrNotNull() {
        // Initialize the database
        insertedOfferMedia = offerMediaRepository.save(offerMedia).block();

        // Get all the offerMediaList where isPrimary is not null
        defaultOfferMediaFiltering("isPrimary.specified=true", "isPrimary.specified=false");
    }

    @Test
    void getAllOfferMediasByCaptionIsEqualToSomething() {
        // Initialize the database
        insertedOfferMedia = offerMediaRepository.save(offerMedia).block();

        // Get all the offerMediaList where caption equals to
        defaultOfferMediaFiltering("caption.equals=" + DEFAULT_CAPTION, "caption.equals=" + UPDATED_CAPTION);
    }

    @Test
    void getAllOfferMediasByCaptionIsInShouldWork() {
        // Initialize the database
        insertedOfferMedia = offerMediaRepository.save(offerMedia).block();

        // Get all the offerMediaList where caption in
        defaultOfferMediaFiltering("caption.in=" + DEFAULT_CAPTION + "," + UPDATED_CAPTION, "caption.in=" + UPDATED_CAPTION);
    }

    @Test
    void getAllOfferMediasByCaptionIsNullOrNotNull() {
        // Initialize the database
        insertedOfferMedia = offerMediaRepository.save(offerMedia).block();

        // Get all the offerMediaList where caption is not null
        defaultOfferMediaFiltering("caption.specified=true", "caption.specified=false");
    }

    @Test
    void getAllOfferMediasByCaptionContainsSomething() {
        // Initialize the database
        insertedOfferMedia = offerMediaRepository.save(offerMedia).block();

        // Get all the offerMediaList where caption contains
        defaultOfferMediaFiltering("caption.contains=" + DEFAULT_CAPTION, "caption.contains=" + UPDATED_CAPTION);
    }

    @Test
    void getAllOfferMediasByCaptionNotContainsSomething() {
        // Initialize the database
        insertedOfferMedia = offerMediaRepository.save(offerMedia).block();

        // Get all the offerMediaList where caption does not contain
        defaultOfferMediaFiltering("caption.doesNotContain=" + UPDATED_CAPTION, "caption.doesNotContain=" + DEFAULT_CAPTION);
    }

    @Test
    void getAllOfferMediasByCreatedDateIsEqualToSomething() {
        // Initialize the database
        insertedOfferMedia = offerMediaRepository.save(offerMedia).block();

        // Get all the offerMediaList where createdDate equals to
        defaultOfferMediaFiltering("createdDate.equals=" + DEFAULT_CREATED_DATE, "createdDate.equals=" + UPDATED_CREATED_DATE);
    }

    @Test
    void getAllOfferMediasByCreatedDateIsInShouldWork() {
        // Initialize the database
        insertedOfferMedia = offerMediaRepository.save(offerMedia).block();

        // Get all the offerMediaList where createdDate in
        defaultOfferMediaFiltering(
            "createdDate.in=" + DEFAULT_CREATED_DATE + "," + UPDATED_CREATED_DATE,
            "createdDate.in=" + UPDATED_CREATED_DATE
        );
    }

    @Test
    void getAllOfferMediasByCreatedDateIsNullOrNotNull() {
        // Initialize the database
        insertedOfferMedia = offerMediaRepository.save(offerMedia).block();

        // Get all the offerMediaList where createdDate is not null
        defaultOfferMediaFiltering("createdDate.specified=true", "createdDate.specified=false");
    }

    @Test
    void getAllOfferMediasByLastModifiedDateIsEqualToSomething() {
        // Initialize the database
        insertedOfferMedia = offerMediaRepository.save(offerMedia).block();

        // Get all the offerMediaList where lastModifiedDate equals to
        defaultOfferMediaFiltering(
            "lastModifiedDate.equals=" + DEFAULT_LAST_MODIFIED_DATE,
            "lastModifiedDate.equals=" + UPDATED_LAST_MODIFIED_DATE
        );
    }

    @Test
    void getAllOfferMediasByLastModifiedDateIsInShouldWork() {
        // Initialize the database
        insertedOfferMedia = offerMediaRepository.save(offerMedia).block();

        // Get all the offerMediaList where lastModifiedDate in
        defaultOfferMediaFiltering(
            "lastModifiedDate.in=" + DEFAULT_LAST_MODIFIED_DATE + "," + UPDATED_LAST_MODIFIED_DATE,
            "lastModifiedDate.in=" + UPDATED_LAST_MODIFIED_DATE
        );
    }

    @Test
    void getAllOfferMediasByLastModifiedDateIsNullOrNotNull() {
        // Initialize the database
        insertedOfferMedia = offerMediaRepository.save(offerMedia).block();

        // Get all the offerMediaList where lastModifiedDate is not null
        defaultOfferMediaFiltering("lastModifiedDate.specified=true", "lastModifiedDate.specified=false");
    }

    @Test
    void getAllOfferMediasByCreatedByIsEqualToSomething() {
        // Initialize the database
        insertedOfferMedia = offerMediaRepository.save(offerMedia).block();

        // Get all the offerMediaList where createdBy equals to
        defaultOfferMediaFiltering("createdBy.equals=" + DEFAULT_CREATED_BY, "createdBy.equals=" + UPDATED_CREATED_BY);
    }

    @Test
    void getAllOfferMediasByCreatedByIsInShouldWork() {
        // Initialize the database
        insertedOfferMedia = offerMediaRepository.save(offerMedia).block();

        // Get all the offerMediaList where createdBy in
        defaultOfferMediaFiltering("createdBy.in=" + DEFAULT_CREATED_BY + "," + UPDATED_CREATED_BY, "createdBy.in=" + UPDATED_CREATED_BY);
    }

    @Test
    void getAllOfferMediasByCreatedByIsNullOrNotNull() {
        // Initialize the database
        insertedOfferMedia = offerMediaRepository.save(offerMedia).block();

        // Get all the offerMediaList where createdBy is not null
        defaultOfferMediaFiltering("createdBy.specified=true", "createdBy.specified=false");
    }

    @Test
    void getAllOfferMediasByCreatedByContainsSomething() {
        // Initialize the database
        insertedOfferMedia = offerMediaRepository.save(offerMedia).block();

        // Get all the offerMediaList where createdBy contains
        defaultOfferMediaFiltering("createdBy.contains=" + DEFAULT_CREATED_BY, "createdBy.contains=" + UPDATED_CREATED_BY);
    }

    @Test
    void getAllOfferMediasByCreatedByNotContainsSomething() {
        // Initialize the database
        insertedOfferMedia = offerMediaRepository.save(offerMedia).block();

        // Get all the offerMediaList where createdBy does not contain
        defaultOfferMediaFiltering("createdBy.doesNotContain=" + UPDATED_CREATED_BY, "createdBy.doesNotContain=" + DEFAULT_CREATED_BY);
    }

    @Test
    void getAllOfferMediasByLastModifiedByIsEqualToSomething() {
        // Initialize the database
        insertedOfferMedia = offerMediaRepository.save(offerMedia).block();

        // Get all the offerMediaList where lastModifiedBy equals to
        defaultOfferMediaFiltering(
            "lastModifiedBy.equals=" + DEFAULT_LAST_MODIFIED_BY,
            "lastModifiedBy.equals=" + UPDATED_LAST_MODIFIED_BY
        );
    }

    @Test
    void getAllOfferMediasByLastModifiedByIsInShouldWork() {
        // Initialize the database
        insertedOfferMedia = offerMediaRepository.save(offerMedia).block();

        // Get all the offerMediaList where lastModifiedBy in
        defaultOfferMediaFiltering(
            "lastModifiedBy.in=" + DEFAULT_LAST_MODIFIED_BY + "," + UPDATED_LAST_MODIFIED_BY,
            "lastModifiedBy.in=" + UPDATED_LAST_MODIFIED_BY
        );
    }

    @Test
    void getAllOfferMediasByLastModifiedByIsNullOrNotNull() {
        // Initialize the database
        insertedOfferMedia = offerMediaRepository.save(offerMedia).block();

        // Get all the offerMediaList where lastModifiedBy is not null
        defaultOfferMediaFiltering("lastModifiedBy.specified=true", "lastModifiedBy.specified=false");
    }

    @Test
    void getAllOfferMediasByLastModifiedByContainsSomething() {
        // Initialize the database
        insertedOfferMedia = offerMediaRepository.save(offerMedia).block();

        // Get all the offerMediaList where lastModifiedBy contains
        defaultOfferMediaFiltering(
            "lastModifiedBy.contains=" + DEFAULT_LAST_MODIFIED_BY,
            "lastModifiedBy.contains=" + UPDATED_LAST_MODIFIED_BY
        );
    }

    @Test
    void getAllOfferMediasByLastModifiedByNotContainsSomething() {
        // Initialize the database
        insertedOfferMedia = offerMediaRepository.save(offerMedia).block();

        // Get all the offerMediaList where lastModifiedBy does not contain
        defaultOfferMediaFiltering(
            "lastModifiedBy.doesNotContain=" + UPDATED_LAST_MODIFIED_BY,
            "lastModifiedBy.doesNotContain=" + DEFAULT_LAST_MODIFIED_BY
        );
    }

    @Test
    void getAllOfferMediasByOfferIsEqualToSomething() {
        Offer offer = OfferResourceIT.createEntity();
        offerRepository.save(offer).block();
        Long offerId = offer.getId();
        offerMedia.setOfferId(offerId);
        insertedOfferMedia = offerMediaRepository.save(offerMedia).block();
        // Get all the offerMediaList where offer equals to offerId
        defaultOfferMediaShouldBeFound("offerId.equals=" + offerId);

        // Get all the offerMediaList where offer equals to (offerId + 1)
        defaultOfferMediaShouldNotBeFound("offerId.equals=" + (offerId + 1));
    }

    @Test
    void getAllOfferMediasByFileIsEqualToSomething() {
        FileObject file = FileObjectResourceIT.createEntity();
        fileObjectRepository.save(file).block();
        Long fileId = file.getId();
        offerMedia.setFileId(fileId);
        insertedOfferMedia = offerMediaRepository.save(offerMedia).block();
        // Get all the offerMediaList where file equals to fileId
        defaultOfferMediaShouldBeFound("fileId.equals=" + fileId);

        // Get all the offerMediaList where file equals to (fileId + 1)
        defaultOfferMediaShouldNotBeFound("fileId.equals=" + (fileId + 1));
    }

    private void defaultOfferMediaFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultOfferMediaShouldBeFound(shouldBeFound);
        defaultOfferMediaShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultOfferMediaShouldBeFound(String filter) {
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
            .value(hasItem(offerMedia.getId().intValue()))
            .jsonPath("$.[*].mediaKind")
            .value(hasItem(DEFAULT_MEDIA_KIND.toString()))
            .jsonPath("$.[*].isPrimary")
            .value(hasItem(DEFAULT_IS_PRIMARY))
            .jsonPath("$.[*].caption")
            .value(hasItem(DEFAULT_CAPTION))
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
    private void defaultOfferMediaShouldNotBeFound(String filter) {
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
    void getNonExistingOfferMedia() {
        // Get the offerMedia
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingOfferMedia() throws Exception {
        // Initialize the database
        insertedOfferMedia = offerMediaRepository.save(offerMedia).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the offerMedia
        OfferMedia updatedOfferMedia = offerMediaRepository.findById(offerMedia.getId()).block();
        updatedOfferMedia
            .mediaKind(UPDATED_MEDIA_KIND)
            .isPrimary(UPDATED_IS_PRIMARY)
            .caption(UPDATED_CAPTION)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .createdBy(UPDATED_CREATED_BY)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY);
        OfferMediaDTO offerMediaDTO = offerMediaMapper.toDto(updatedOfferMedia);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, offerMediaDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(offerMediaDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the OfferMedia in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedOfferMediaToMatchAllProperties(updatedOfferMedia);
    }

    @Test
    void putNonExistingOfferMedia() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        offerMedia.setId(longCount.incrementAndGet());

        // Create the OfferMedia
        OfferMediaDTO offerMediaDTO = offerMediaMapper.toDto(offerMedia);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, offerMediaDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(offerMediaDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the OfferMedia in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchOfferMedia() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        offerMedia.setId(longCount.incrementAndGet());

        // Create the OfferMedia
        OfferMediaDTO offerMediaDTO = offerMediaMapper.toDto(offerMedia);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(offerMediaDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the OfferMedia in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamOfferMedia() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        offerMedia.setId(longCount.incrementAndGet());

        // Create the OfferMedia
        OfferMediaDTO offerMediaDTO = offerMediaMapper.toDto(offerMedia);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(offerMediaDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the OfferMedia in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateOfferMediaWithPatch() throws Exception {
        // Initialize the database
        insertedOfferMedia = offerMediaRepository.save(offerMedia).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the offerMedia using partial update
        OfferMedia partialUpdatedOfferMedia = new OfferMedia();
        partialUpdatedOfferMedia.setId(offerMedia.getId());

        partialUpdatedOfferMedia
            .mediaKind(UPDATED_MEDIA_KIND)
            .isPrimary(UPDATED_IS_PRIMARY)
            .caption(UPDATED_CAPTION)
            .createdBy(UPDATED_CREATED_BY);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedOfferMedia.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedOfferMedia))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the OfferMedia in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertOfferMediaUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedOfferMedia, offerMedia),
            getPersistedOfferMedia(offerMedia)
        );
    }

    @Test
    void fullUpdateOfferMediaWithPatch() throws Exception {
        // Initialize the database
        insertedOfferMedia = offerMediaRepository.save(offerMedia).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the offerMedia using partial update
        OfferMedia partialUpdatedOfferMedia = new OfferMedia();
        partialUpdatedOfferMedia.setId(offerMedia.getId());

        partialUpdatedOfferMedia
            .mediaKind(UPDATED_MEDIA_KIND)
            .isPrimary(UPDATED_IS_PRIMARY)
            .caption(UPDATED_CAPTION)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .createdBy(UPDATED_CREATED_BY)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedOfferMedia.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedOfferMedia))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the OfferMedia in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertOfferMediaUpdatableFieldsEquals(partialUpdatedOfferMedia, getPersistedOfferMedia(partialUpdatedOfferMedia));
    }

    @Test
    void patchNonExistingOfferMedia() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        offerMedia.setId(longCount.incrementAndGet());

        // Create the OfferMedia
        OfferMediaDTO offerMediaDTO = offerMediaMapper.toDto(offerMedia);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, offerMediaDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(offerMediaDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the OfferMedia in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchOfferMedia() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        offerMedia.setId(longCount.incrementAndGet());

        // Create the OfferMedia
        OfferMediaDTO offerMediaDTO = offerMediaMapper.toDto(offerMedia);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(offerMediaDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the OfferMedia in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamOfferMedia() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        offerMedia.setId(longCount.incrementAndGet());

        // Create the OfferMedia
        OfferMediaDTO offerMediaDTO = offerMediaMapper.toDto(offerMedia);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(offerMediaDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the OfferMedia in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteOfferMedia() {
        // Initialize the database
        insertedOfferMedia = offerMediaRepository.save(offerMedia).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the offerMedia
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, offerMedia.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return offerMediaRepository.count().block();
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

    protected OfferMedia getPersistedOfferMedia(OfferMedia offerMedia) {
        return offerMediaRepository.findById(offerMedia.getId()).block();
    }

    protected void assertPersistedOfferMediaToMatchAllProperties(OfferMedia expectedOfferMedia) {
        // Test fails because reactive api returns an empty object instead of null
        // assertOfferMediaAllPropertiesEquals(expectedOfferMedia, getPersistedOfferMedia(expectedOfferMedia));
        assertOfferMediaUpdatableFieldsEquals(expectedOfferMedia, getPersistedOfferMedia(expectedOfferMedia));
    }

    protected void assertPersistedOfferMediaToMatchUpdatableProperties(OfferMedia expectedOfferMedia) {
        // Test fails because reactive api returns an empty object instead of null
        // assertOfferMediaAllUpdatablePropertiesEquals(expectedOfferMedia, getPersistedOfferMedia(expectedOfferMedia));
        assertOfferMediaUpdatableFieldsEquals(expectedOfferMedia, getPersistedOfferMedia(expectedOfferMedia));
    }
}
