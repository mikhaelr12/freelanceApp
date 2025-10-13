package com.freelance.app.web.rest;

import static com.freelance.app.domain.ProfileAsserts.*;
import static com.freelance.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.freelance.app.IntegrationTest;
import com.freelance.app.domain.FileObject;
import com.freelance.app.domain.Profile;
import com.freelance.app.domain.User;
import com.freelance.app.domain.enumeration.ProfileType;
import com.freelance.app.repository.EntityManager;
import com.freelance.app.repository.FileObjectRepository;
import com.freelance.app.repository.ProfileRepository;
import com.freelance.app.repository.UserRepository;
import com.freelance.app.repository.UserRepository;
import com.freelance.app.service.ProfileService;
import com.freelance.app.service.dto.ProfileDTO;
import com.freelance.app.service.mapper.ProfileMapper;
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
 * Integration tests for the {@link ProfileResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ProfileResourceIT {

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_LAST_MODIFIED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_MODIFIED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_MODIFIED_BY = "AAAAAAAAAA";
    private static final String UPDATED_LAST_MODIFIED_BY = "BBBBBBBBBB";

    private static final ProfileType DEFAULT_PROFILE_TYPE = ProfileType.CLIENT;
    private static final ProfileType UPDATED_PROFILE_TYPE = ProfileType.FREELANCER;

    private static final String ENTITY_API_URL = "/api/profiles";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private ProfileRepository profileRepositoryMock;

    @Autowired
    private ProfileMapper profileMapper;

    @Mock
    private ProfileService profileServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Profile profile;

    private Profile insertedProfile;

    @Autowired
    private FileObjectRepository fileObjectRepository;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Profile createEntity() {
        return new Profile()
            .firstName(DEFAULT_FIRST_NAME)
            .lastName(DEFAULT_LAST_NAME)
            .description(DEFAULT_DESCRIPTION)
            .createdDate(DEFAULT_CREATED_DATE)
            .lastModifiedDate(DEFAULT_LAST_MODIFIED_DATE)
            .createdBy(DEFAULT_CREATED_BY)
            .lastModifiedBy(DEFAULT_LAST_MODIFIED_BY)
            .profileType(DEFAULT_PROFILE_TYPE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Profile createUpdatedEntity() {
        return new Profile()
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .description(UPDATED_DESCRIPTION)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .createdBy(UPDATED_CREATED_BY)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .profileType(UPDATED_PROFILE_TYPE);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll("rel_profile__skill").block();
            em.deleteAll(Profile.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        profile = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedProfile != null) {
            profileRepository.delete(insertedProfile).block();
            insertedProfile = null;
        }
        deleteEntities(em);
        userRepository.deleteAllUserAuthorities().block();
        userRepository.deleteAll().block();
    }

    @Test
    void createProfile() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Profile
        ProfileDTO profileDTO = profileMapper.toDto(profile);
        var returnedProfileDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(profileDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(ProfileDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Profile in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedProfile = profileMapper.toEntity(returnedProfileDTO);
        assertProfileUpdatableFieldsEquals(returnedProfile, getPersistedProfile(returnedProfile));

        insertedProfile = returnedProfile;
    }

    @Test
    void createProfileWithExistingId() throws Exception {
        // Create the Profile with an existing ID
        profile.setId(1L);
        ProfileDTO profileDTO = profileMapper.toDto(profile);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(profileDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Profile in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkFirstNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        profile.setFirstName(null);

        // Create the Profile, which fails.
        ProfileDTO profileDTO = profileMapper.toDto(profile);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(profileDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkLastNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        profile.setLastName(null);

        // Create the Profile, which fails.
        ProfileDTO profileDTO = profileMapper.toDto(profile);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(profileDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkCreatedDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        profile.setCreatedDate(null);

        // Create the Profile, which fails.
        ProfileDTO profileDTO = profileMapper.toDto(profile);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(profileDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllProfiles() {
        // Initialize the database
        insertedProfile = profileRepository.save(profile).block();

        // Get all the profileList
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
            .value(hasItem(profile.getId().intValue()))
            .jsonPath("$.[*].firstName")
            .value(hasItem(DEFAULT_FIRST_NAME))
            .jsonPath("$.[*].lastName")
            .value(hasItem(DEFAULT_LAST_NAME))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].createdDate")
            .value(hasItem(DEFAULT_CREATED_DATE.toString()))
            .jsonPath("$.[*].lastModifiedDate")
            .value(hasItem(DEFAULT_LAST_MODIFIED_DATE.toString()))
            .jsonPath("$.[*].createdBy")
            .value(hasItem(DEFAULT_CREATED_BY))
            .jsonPath("$.[*].lastModifiedBy")
            .value(hasItem(DEFAULT_LAST_MODIFIED_BY))
            .jsonPath("$.[*].profileType")
            .value(hasItem(DEFAULT_PROFILE_TYPE.toString()));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProfilesWithEagerRelationshipsIsEnabled() {
        when(profileServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(profileServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProfilesWithEagerRelationshipsIsNotEnabled() {
        when(profileServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(profileRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getProfile() {
        // Initialize the database
        insertedProfile = profileRepository.save(profile).block();

        // Get the profile
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, profile.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(profile.getId().intValue()))
            .jsonPath("$.firstName")
            .value(is(DEFAULT_FIRST_NAME))
            .jsonPath("$.lastName")
            .value(is(DEFAULT_LAST_NAME))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION))
            .jsonPath("$.createdDate")
            .value(is(DEFAULT_CREATED_DATE.toString()))
            .jsonPath("$.lastModifiedDate")
            .value(is(DEFAULT_LAST_MODIFIED_DATE.toString()))
            .jsonPath("$.createdBy")
            .value(is(DEFAULT_CREATED_BY))
            .jsonPath("$.lastModifiedBy")
            .value(is(DEFAULT_LAST_MODIFIED_BY))
            .jsonPath("$.profileType")
            .value(is(DEFAULT_PROFILE_TYPE.toString()));
    }

    @Test
    void getProfilesByIdFiltering() {
        // Initialize the database
        insertedProfile = profileRepository.save(profile).block();

        Long id = profile.getId();

        defaultProfileFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultProfileFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultProfileFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllProfilesByFirstNameIsEqualToSomething() {
        // Initialize the database
        insertedProfile = profileRepository.save(profile).block();

        // Get all the profileList where firstName equals to
        defaultProfileFiltering("firstName.equals=" + DEFAULT_FIRST_NAME, "firstName.equals=" + UPDATED_FIRST_NAME);
    }

    @Test
    void getAllProfilesByFirstNameIsInShouldWork() {
        // Initialize the database
        insertedProfile = profileRepository.save(profile).block();

        // Get all the profileList where firstName in
        defaultProfileFiltering("firstName.in=" + DEFAULT_FIRST_NAME + "," + UPDATED_FIRST_NAME, "firstName.in=" + UPDATED_FIRST_NAME);
    }

    @Test
    void getAllProfilesByFirstNameIsNullOrNotNull() {
        // Initialize the database
        insertedProfile = profileRepository.save(profile).block();

        // Get all the profileList where firstName is not null
        defaultProfileFiltering("firstName.specified=true", "firstName.specified=false");
    }

    @Test
    void getAllProfilesByFirstNameContainsSomething() {
        // Initialize the database
        insertedProfile = profileRepository.save(profile).block();

        // Get all the profileList where firstName contains
        defaultProfileFiltering("firstName.contains=" + DEFAULT_FIRST_NAME, "firstName.contains=" + UPDATED_FIRST_NAME);
    }

    @Test
    void getAllProfilesByFirstNameNotContainsSomething() {
        // Initialize the database
        insertedProfile = profileRepository.save(profile).block();

        // Get all the profileList where firstName does not contain
        defaultProfileFiltering("firstName.doesNotContain=" + UPDATED_FIRST_NAME, "firstName.doesNotContain=" + DEFAULT_FIRST_NAME);
    }

    @Test
    void getAllProfilesByLastNameIsEqualToSomething() {
        // Initialize the database
        insertedProfile = profileRepository.save(profile).block();

        // Get all the profileList where lastName equals to
        defaultProfileFiltering("lastName.equals=" + DEFAULT_LAST_NAME, "lastName.equals=" + UPDATED_LAST_NAME);
    }

    @Test
    void getAllProfilesByLastNameIsInShouldWork() {
        // Initialize the database
        insertedProfile = profileRepository.save(profile).block();

        // Get all the profileList where lastName in
        defaultProfileFiltering("lastName.in=" + DEFAULT_LAST_NAME + "," + UPDATED_LAST_NAME, "lastName.in=" + UPDATED_LAST_NAME);
    }

    @Test
    void getAllProfilesByLastNameIsNullOrNotNull() {
        // Initialize the database
        insertedProfile = profileRepository.save(profile).block();

        // Get all the profileList where lastName is not null
        defaultProfileFiltering("lastName.specified=true", "lastName.specified=false");
    }

    @Test
    void getAllProfilesByLastNameContainsSomething() {
        // Initialize the database
        insertedProfile = profileRepository.save(profile).block();

        // Get all the profileList where lastName contains
        defaultProfileFiltering("lastName.contains=" + DEFAULT_LAST_NAME, "lastName.contains=" + UPDATED_LAST_NAME);
    }

    @Test
    void getAllProfilesByLastNameNotContainsSomething() {
        // Initialize the database
        insertedProfile = profileRepository.save(profile).block();

        // Get all the profileList where lastName does not contain
        defaultProfileFiltering("lastName.doesNotContain=" + UPDATED_LAST_NAME, "lastName.doesNotContain=" + DEFAULT_LAST_NAME);
    }

    @Test
    void getAllProfilesByDescriptionIsEqualToSomething() {
        // Initialize the database
        insertedProfile = profileRepository.save(profile).block();

        // Get all the profileList where description equals to
        defaultProfileFiltering("description.equals=" + DEFAULT_DESCRIPTION, "description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    void getAllProfilesByDescriptionIsInShouldWork() {
        // Initialize the database
        insertedProfile = profileRepository.save(profile).block();

        // Get all the profileList where description in
        defaultProfileFiltering(
            "description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION,
            "description.in=" + UPDATED_DESCRIPTION
        );
    }

    @Test
    void getAllProfilesByDescriptionIsNullOrNotNull() {
        // Initialize the database
        insertedProfile = profileRepository.save(profile).block();

        // Get all the profileList where description is not null
        defaultProfileFiltering("description.specified=true", "description.specified=false");
    }

    @Test
    void getAllProfilesByDescriptionContainsSomething() {
        // Initialize the database
        insertedProfile = profileRepository.save(profile).block();

        // Get all the profileList where description contains
        defaultProfileFiltering("description.contains=" + DEFAULT_DESCRIPTION, "description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    void getAllProfilesByDescriptionNotContainsSomething() {
        // Initialize the database
        insertedProfile = profileRepository.save(profile).block();

        // Get all the profileList where description does not contain
        defaultProfileFiltering("description.doesNotContain=" + UPDATED_DESCRIPTION, "description.doesNotContain=" + DEFAULT_DESCRIPTION);
    }

    @Test
    void getAllProfilesByCreatedDateIsEqualToSomething() {
        // Initialize the database
        insertedProfile = profileRepository.save(profile).block();

        // Get all the profileList where createdDate equals to
        defaultProfileFiltering("createdDate.equals=" + DEFAULT_CREATED_DATE, "createdDate.equals=" + UPDATED_CREATED_DATE);
    }

    @Test
    void getAllProfilesByCreatedDateIsInShouldWork() {
        // Initialize the database
        insertedProfile = profileRepository.save(profile).block();

        // Get all the profileList where createdDate in
        defaultProfileFiltering(
            "createdDate.in=" + DEFAULT_CREATED_DATE + "," + UPDATED_CREATED_DATE,
            "createdDate.in=" + UPDATED_CREATED_DATE
        );
    }

    @Test
    void getAllProfilesByCreatedDateIsNullOrNotNull() {
        // Initialize the database
        insertedProfile = profileRepository.save(profile).block();

        // Get all the profileList where createdDate is not null
        defaultProfileFiltering("createdDate.specified=true", "createdDate.specified=false");
    }

    @Test
    void getAllProfilesByLastModifiedDateIsEqualToSomething() {
        // Initialize the database
        insertedProfile = profileRepository.save(profile).block();

        // Get all the profileList where lastModifiedDate equals to
        defaultProfileFiltering(
            "lastModifiedDate.equals=" + DEFAULT_LAST_MODIFIED_DATE,
            "lastModifiedDate.equals=" + UPDATED_LAST_MODIFIED_DATE
        );
    }

    @Test
    void getAllProfilesByLastModifiedDateIsInShouldWork() {
        // Initialize the database
        insertedProfile = profileRepository.save(profile).block();

        // Get all the profileList where lastModifiedDate in
        defaultProfileFiltering(
            "lastModifiedDate.in=" + DEFAULT_LAST_MODIFIED_DATE + "," + UPDATED_LAST_MODIFIED_DATE,
            "lastModifiedDate.in=" + UPDATED_LAST_MODIFIED_DATE
        );
    }

    @Test
    void getAllProfilesByLastModifiedDateIsNullOrNotNull() {
        // Initialize the database
        insertedProfile = profileRepository.save(profile).block();

        // Get all the profileList where lastModifiedDate is not null
        defaultProfileFiltering("lastModifiedDate.specified=true", "lastModifiedDate.specified=false");
    }

    @Test
    void getAllProfilesByCreatedByIsEqualToSomething() {
        // Initialize the database
        insertedProfile = profileRepository.save(profile).block();

        // Get all the profileList where createdBy equals to
        defaultProfileFiltering("createdBy.equals=" + DEFAULT_CREATED_BY, "createdBy.equals=" + UPDATED_CREATED_BY);
    }

    @Test
    void getAllProfilesByCreatedByIsInShouldWork() {
        // Initialize the database
        insertedProfile = profileRepository.save(profile).block();

        // Get all the profileList where createdBy in
        defaultProfileFiltering("createdBy.in=" + DEFAULT_CREATED_BY + "," + UPDATED_CREATED_BY, "createdBy.in=" + UPDATED_CREATED_BY);
    }

    @Test
    void getAllProfilesByCreatedByIsNullOrNotNull() {
        // Initialize the database
        insertedProfile = profileRepository.save(profile).block();

        // Get all the profileList where createdBy is not null
        defaultProfileFiltering("createdBy.specified=true", "createdBy.specified=false");
    }

    @Test
    void getAllProfilesByCreatedByContainsSomething() {
        // Initialize the database
        insertedProfile = profileRepository.save(profile).block();

        // Get all the profileList where createdBy contains
        defaultProfileFiltering("createdBy.contains=" + DEFAULT_CREATED_BY, "createdBy.contains=" + UPDATED_CREATED_BY);
    }

    @Test
    void getAllProfilesByCreatedByNotContainsSomething() {
        // Initialize the database
        insertedProfile = profileRepository.save(profile).block();

        // Get all the profileList where createdBy does not contain
        defaultProfileFiltering("createdBy.doesNotContain=" + UPDATED_CREATED_BY, "createdBy.doesNotContain=" + DEFAULT_CREATED_BY);
    }

    @Test
    void getAllProfilesByLastModifiedByIsEqualToSomething() {
        // Initialize the database
        insertedProfile = profileRepository.save(profile).block();

        // Get all the profileList where lastModifiedBy equals to
        defaultProfileFiltering("lastModifiedBy.equals=" + DEFAULT_LAST_MODIFIED_BY, "lastModifiedBy.equals=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    void getAllProfilesByLastModifiedByIsInShouldWork() {
        // Initialize the database
        insertedProfile = profileRepository.save(profile).block();

        // Get all the profileList where lastModifiedBy in
        defaultProfileFiltering(
            "lastModifiedBy.in=" + DEFAULT_LAST_MODIFIED_BY + "," + UPDATED_LAST_MODIFIED_BY,
            "lastModifiedBy.in=" + UPDATED_LAST_MODIFIED_BY
        );
    }

    @Test
    void getAllProfilesByLastModifiedByIsNullOrNotNull() {
        // Initialize the database
        insertedProfile = profileRepository.save(profile).block();

        // Get all the profileList where lastModifiedBy is not null
        defaultProfileFiltering("lastModifiedBy.specified=true", "lastModifiedBy.specified=false");
    }

    @Test
    void getAllProfilesByLastModifiedByContainsSomething() {
        // Initialize the database
        insertedProfile = profileRepository.save(profile).block();

        // Get all the profileList where lastModifiedBy contains
        defaultProfileFiltering(
            "lastModifiedBy.contains=" + DEFAULT_LAST_MODIFIED_BY,
            "lastModifiedBy.contains=" + UPDATED_LAST_MODIFIED_BY
        );
    }

    @Test
    void getAllProfilesByLastModifiedByNotContainsSomething() {
        // Initialize the database
        insertedProfile = profileRepository.save(profile).block();

        // Get all the profileList where lastModifiedBy does not contain
        defaultProfileFiltering(
            "lastModifiedBy.doesNotContain=" + UPDATED_LAST_MODIFIED_BY,
            "lastModifiedBy.doesNotContain=" + DEFAULT_LAST_MODIFIED_BY
        );
    }

    @Test
    void getAllProfilesByProfileTypeIsEqualToSomething() {
        // Initialize the database
        insertedProfile = profileRepository.save(profile).block();

        // Get all the profileList where profileType equals to
        defaultProfileFiltering("profileType.equals=" + DEFAULT_PROFILE_TYPE, "profileType.equals=" + UPDATED_PROFILE_TYPE);
    }

    @Test
    void getAllProfilesByProfileTypeIsInShouldWork() {
        // Initialize the database
        insertedProfile = profileRepository.save(profile).block();

        // Get all the profileList where profileType in
        defaultProfileFiltering(
            "profileType.in=" + DEFAULT_PROFILE_TYPE + "," + UPDATED_PROFILE_TYPE,
            "profileType.in=" + UPDATED_PROFILE_TYPE
        );
    }

    @Test
    void getAllProfilesByProfileTypeIsNullOrNotNull() {
        // Initialize the database
        insertedProfile = profileRepository.save(profile).block();

        // Get all the profileList where profileType is not null
        defaultProfileFiltering("profileType.specified=true", "profileType.specified=false");
    }

    @Test
    void getAllProfilesByUserIsEqualToSomething() {
        User user = UserResourceIT.createEntity();
        userRepository.save(user).block();
        Long userId = user.getId();
        profile.setUserId(userId);
        insertedProfile = profileRepository.save(profile).block();
        // Get all the profileList where user equals to userId
        defaultProfileShouldBeFound("userId.equals=" + userId);

        // Get all the profileList where user equals to (userId + 1)
        defaultProfileShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    @Test
    void getAllProfilesByProfilePictureIsEqualToSomething() {
        FileObject profilePicture = FileObjectResourceIT.createEntity();
        fileObjectRepository.save(profilePicture).block();
        Long profilePictureId = profilePicture.getId();
        profile.setProfilePictureId(profilePictureId);
        insertedProfile = profileRepository.save(profile).block();
        // Get all the profileList where profilePicture equals to profilePictureId
        defaultProfileShouldBeFound("profilePictureId.equals=" + profilePictureId);

        // Get all the profileList where profilePicture equals to (profilePictureId + 1)
        defaultProfileShouldNotBeFound("profilePictureId.equals=" + (profilePictureId + 1));
    }

    private void defaultProfileFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultProfileShouldBeFound(shouldBeFound);
        defaultProfileShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultProfileShouldBeFound(String filter) {
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
            .value(hasItem(profile.getId().intValue()))
            .jsonPath("$.[*].firstName")
            .value(hasItem(DEFAULT_FIRST_NAME))
            .jsonPath("$.[*].lastName")
            .value(hasItem(DEFAULT_LAST_NAME))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].createdDate")
            .value(hasItem(DEFAULT_CREATED_DATE.toString()))
            .jsonPath("$.[*].lastModifiedDate")
            .value(hasItem(DEFAULT_LAST_MODIFIED_DATE.toString()))
            .jsonPath("$.[*].createdBy")
            .value(hasItem(DEFAULT_CREATED_BY))
            .jsonPath("$.[*].lastModifiedBy")
            .value(hasItem(DEFAULT_LAST_MODIFIED_BY))
            .jsonPath("$.[*].profileType")
            .value(hasItem(DEFAULT_PROFILE_TYPE.toString()));

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
    private void defaultProfileShouldNotBeFound(String filter) {
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
    void getNonExistingProfile() {
        // Get the profile
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingProfile() throws Exception {
        // Initialize the database
        insertedProfile = profileRepository.save(profile).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the profile
        Profile updatedProfile = profileRepository.findById(profile.getId()).block();
        updatedProfile
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .description(UPDATED_DESCRIPTION)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .createdBy(UPDATED_CREATED_BY)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .profileType(UPDATED_PROFILE_TYPE);
        ProfileDTO profileDTO = profileMapper.toDto(updatedProfile);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, profileDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(profileDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Profile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedProfileToMatchAllProperties(updatedProfile);
    }

    @Test
    void putNonExistingProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        profile.setId(longCount.incrementAndGet());

        // Create the Profile
        ProfileDTO profileDTO = profileMapper.toDto(profile);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, profileDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(profileDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Profile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        profile.setId(longCount.incrementAndGet());

        // Create the Profile
        ProfileDTO profileDTO = profileMapper.toDto(profile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(profileDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Profile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        profile.setId(longCount.incrementAndGet());

        // Create the Profile
        ProfileDTO profileDTO = profileMapper.toDto(profile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(profileDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Profile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateProfileWithPatch() throws Exception {
        // Initialize the database
        insertedProfile = profileRepository.save(profile).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the profile using partial update
        Profile partialUpdatedProfile = new Profile();
        partialUpdatedProfile.setId(profile.getId());

        partialUpdatedProfile
            .lastName(UPDATED_LAST_NAME)
            .description(UPDATED_DESCRIPTION)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .profileType(UPDATED_PROFILE_TYPE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProfile.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedProfile))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Profile in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProfileUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedProfile, profile), getPersistedProfile(profile));
    }

    @Test
    void fullUpdateProfileWithPatch() throws Exception {
        // Initialize the database
        insertedProfile = profileRepository.save(profile).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the profile using partial update
        Profile partialUpdatedProfile = new Profile();
        partialUpdatedProfile.setId(profile.getId());

        partialUpdatedProfile
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .description(UPDATED_DESCRIPTION)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .createdBy(UPDATED_CREATED_BY)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .profileType(UPDATED_PROFILE_TYPE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProfile.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedProfile))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Profile in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProfileUpdatableFieldsEquals(partialUpdatedProfile, getPersistedProfile(partialUpdatedProfile));
    }

    @Test
    void patchNonExistingProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        profile.setId(longCount.incrementAndGet());

        // Create the Profile
        ProfileDTO profileDTO = profileMapper.toDto(profile);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, profileDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(profileDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Profile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        profile.setId(longCount.incrementAndGet());

        // Create the Profile
        ProfileDTO profileDTO = profileMapper.toDto(profile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(profileDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Profile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        profile.setId(longCount.incrementAndGet());

        // Create the Profile
        ProfileDTO profileDTO = profileMapper.toDto(profile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(profileDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Profile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteProfile() {
        // Initialize the database
        insertedProfile = profileRepository.save(profile).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the profile
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, profile.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return profileRepository.count().block();
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

    protected Profile getPersistedProfile(Profile profile) {
        return profileRepository.findById(profile.getId()).block();
    }

    protected void assertPersistedProfileToMatchAllProperties(Profile expectedProfile) {
        // Test fails because reactive api returns an empty object instead of null
        // assertProfileAllPropertiesEquals(expectedProfile, getPersistedProfile(expectedProfile));
        assertProfileUpdatableFieldsEquals(expectedProfile, getPersistedProfile(expectedProfile));
    }

    protected void assertPersistedProfileToMatchUpdatableProperties(Profile expectedProfile) {
        // Test fails because reactive api returns an empty object instead of null
        // assertProfileAllUpdatablePropertiesEquals(expectedProfile, getPersistedProfile(expectedProfile));
        assertProfileUpdatableFieldsEquals(expectedProfile, getPersistedProfile(expectedProfile));
    }
}
