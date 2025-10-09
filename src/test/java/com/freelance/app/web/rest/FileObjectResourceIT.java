package com.freelance.app.web.rest;

import static com.freelance.app.domain.FileObjectAsserts.*;
import static com.freelance.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.freelance.app.IntegrationTest;
import com.freelance.app.domain.FileObject;
import com.freelance.app.repository.EntityManager;
import com.freelance.app.repository.FileObjectRepository;
import com.freelance.app.service.dto.FileObjectDTO;
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
 * Integration tests for the {@link FileObjectResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class FileObjectResourceIT {

    private static final String DEFAULT_BUCKET = "AAAAAAAAAA";
    private static final String UPDATED_BUCKET = "BBBBBBBBBB";

    private static final String DEFAULT_OBJECT_KEY = "AAAAAAAAAA";
    private static final String UPDATED_OBJECT_KEY = "BBBBBBBBBB";

    private static final String DEFAULT_CONTENT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT_TYPE = "BBBBBBBBBB";

    private static final Long DEFAULT_FILE_SIZE = 1L;
    private static final Long UPDATED_FILE_SIZE = 2L;
    private static final Long SMALLER_FILE_SIZE = 1L - 1L;

    private static final String DEFAULT_CHECKSUM = "AAAAAAAAAA";
    private static final String UPDATED_CHECKSUM = "BBBBBBBBBB";

    private static final Integer DEFAULT_DURATION_SECONDS = 1;
    private static final Integer UPDATED_DURATION_SECONDS = 2;
    private static final Integer SMALLER_DURATION_SECONDS = 1 - 1;

    private static final Instant DEFAULT_CREATED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_LAST_MODIFIED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_MODIFIED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_MODIFIED_BY = "AAAAAAAAAA";
    private static final String UPDATED_LAST_MODIFIED_BY = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/file-objects";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private FileObjectRepository fileObjectRepository;

    @Autowired
    private FileObjectMapper fileObjectMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private FileObject fileObject;

    private FileObject insertedFileObject;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FileObject createEntity() {
        return new FileObject()
            .bucket(DEFAULT_BUCKET)
            .objectKey(DEFAULT_OBJECT_KEY)
            .contentType(DEFAULT_CONTENT_TYPE)
            .fileSize(DEFAULT_FILE_SIZE)
            .checksum(DEFAULT_CHECKSUM)
            .durationSeconds(DEFAULT_DURATION_SECONDS)
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
    public static FileObject createUpdatedEntity() {
        return new FileObject()
            .bucket(UPDATED_BUCKET)
            .objectKey(UPDATED_OBJECT_KEY)
            .contentType(UPDATED_CONTENT_TYPE)
            .fileSize(UPDATED_FILE_SIZE)
            .checksum(UPDATED_CHECKSUM)
            .durationSeconds(UPDATED_DURATION_SECONDS)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .createdBy(UPDATED_CREATED_BY)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(FileObject.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        fileObject = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedFileObject != null) {
            fileObjectRepository.delete(insertedFileObject).block();
            insertedFileObject = null;
        }
        deleteEntities(em);
    }

    @Test
    void createFileObject() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the FileObject
        FileObjectDTO fileObjectDTO = fileObjectMapper.toDto(fileObject);
        var returnedFileObjectDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(fileObjectDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(FileObjectDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the FileObject in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedFileObject = fileObjectMapper.toEntity(returnedFileObjectDTO);
        assertFileObjectUpdatableFieldsEquals(returnedFileObject, getPersistedFileObject(returnedFileObject));

        insertedFileObject = returnedFileObject;
    }

    @Test
    void createFileObjectWithExistingId() throws Exception {
        // Create the FileObject with an existing ID
        fileObject.setId(1L);
        FileObjectDTO fileObjectDTO = fileObjectMapper.toDto(fileObject);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(fileObjectDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the FileObject in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkBucketIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        fileObject.setBucket(null);

        // Create the FileObject, which fails.
        FileObjectDTO fileObjectDTO = fileObjectMapper.toDto(fileObject);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(fileObjectDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkObjectKeyIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        fileObject.setObjectKey(null);

        // Create the FileObject, which fails.
        FileObjectDTO fileObjectDTO = fileObjectMapper.toDto(fileObject);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(fileObjectDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkCreatedDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        fileObject.setCreatedDate(null);

        // Create the FileObject, which fails.
        FileObjectDTO fileObjectDTO = fileObjectMapper.toDto(fileObject);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(fileObjectDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllFileObjects() {
        // Initialize the database
        insertedFileObject = fileObjectRepository.save(fileObject).block();

        // Get all the fileObjectList
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
            .value(hasItem(fileObject.getId().intValue()))
            .jsonPath("$.[*].bucket")
            .value(hasItem(DEFAULT_BUCKET))
            .jsonPath("$.[*].objectKey")
            .value(hasItem(DEFAULT_OBJECT_KEY))
            .jsonPath("$.[*].contentType")
            .value(hasItem(DEFAULT_CONTENT_TYPE))
            .jsonPath("$.[*].fileSize")
            .value(hasItem(DEFAULT_FILE_SIZE.intValue()))
            .jsonPath("$.[*].checksum")
            .value(hasItem(DEFAULT_CHECKSUM))
            .jsonPath("$.[*].durationSeconds")
            .value(hasItem(DEFAULT_DURATION_SECONDS))
            .jsonPath("$.[*].createdDate")
            .value(hasItem(DEFAULT_CREATED_DATE.toString()))
            .jsonPath("$.[*].lastModifiedDate")
            .value(hasItem(DEFAULT_LAST_MODIFIED_DATE.toString()))
            .jsonPath("$.[*].createdBy")
            .value(hasItem(DEFAULT_CREATED_BY))
            .jsonPath("$.[*].lastModifiedBy")
            .value(hasItem(DEFAULT_LAST_MODIFIED_BY));
    }

    @Test
    void getFileObject() {
        // Initialize the database
        insertedFileObject = fileObjectRepository.save(fileObject).block();

        // Get the fileObject
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, fileObject.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(fileObject.getId().intValue()))
            .jsonPath("$.bucket")
            .value(is(DEFAULT_BUCKET))
            .jsonPath("$.objectKey")
            .value(is(DEFAULT_OBJECT_KEY))
            .jsonPath("$.contentType")
            .value(is(DEFAULT_CONTENT_TYPE))
            .jsonPath("$.fileSize")
            .value(is(DEFAULT_FILE_SIZE.intValue()))
            .jsonPath("$.checksum")
            .value(is(DEFAULT_CHECKSUM))
            .jsonPath("$.durationSeconds")
            .value(is(DEFAULT_DURATION_SECONDS))
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
    void getFileObjectsByIdFiltering() {
        // Initialize the database
        insertedFileObject = fileObjectRepository.save(fileObject).block();

        Long id = fileObject.getId();

        defaultFileObjectFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultFileObjectFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultFileObjectFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllFileObjectsByBucketIsEqualToSomething() {
        // Initialize the database
        insertedFileObject = fileObjectRepository.save(fileObject).block();

        // Get all the fileObjectList where bucket equals to
        defaultFileObjectFiltering("bucket.equals=" + DEFAULT_BUCKET, "bucket.equals=" + UPDATED_BUCKET);
    }

    @Test
    void getAllFileObjectsByBucketIsInShouldWork() {
        // Initialize the database
        insertedFileObject = fileObjectRepository.save(fileObject).block();

        // Get all the fileObjectList where bucket in
        defaultFileObjectFiltering("bucket.in=" + DEFAULT_BUCKET + "," + UPDATED_BUCKET, "bucket.in=" + UPDATED_BUCKET);
    }

    @Test
    void getAllFileObjectsByBucketIsNullOrNotNull() {
        // Initialize the database
        insertedFileObject = fileObjectRepository.save(fileObject).block();

        // Get all the fileObjectList where bucket is not null
        defaultFileObjectFiltering("bucket.specified=true", "bucket.specified=false");
    }

    @Test
    void getAllFileObjectsByBucketContainsSomething() {
        // Initialize the database
        insertedFileObject = fileObjectRepository.save(fileObject).block();

        // Get all the fileObjectList where bucket contains
        defaultFileObjectFiltering("bucket.contains=" + DEFAULT_BUCKET, "bucket.contains=" + UPDATED_BUCKET);
    }

    @Test
    void getAllFileObjectsByBucketNotContainsSomething() {
        // Initialize the database
        insertedFileObject = fileObjectRepository.save(fileObject).block();

        // Get all the fileObjectList where bucket does not contain
        defaultFileObjectFiltering("bucket.doesNotContain=" + UPDATED_BUCKET, "bucket.doesNotContain=" + DEFAULT_BUCKET);
    }

    @Test
    void getAllFileObjectsByObjectKeyIsEqualToSomething() {
        // Initialize the database
        insertedFileObject = fileObjectRepository.save(fileObject).block();

        // Get all the fileObjectList where objectKey equals to
        defaultFileObjectFiltering("objectKey.equals=" + DEFAULT_OBJECT_KEY, "objectKey.equals=" + UPDATED_OBJECT_KEY);
    }

    @Test
    void getAllFileObjectsByObjectKeyIsInShouldWork() {
        // Initialize the database
        insertedFileObject = fileObjectRepository.save(fileObject).block();

        // Get all the fileObjectList where objectKey in
        defaultFileObjectFiltering("objectKey.in=" + DEFAULT_OBJECT_KEY + "," + UPDATED_OBJECT_KEY, "objectKey.in=" + UPDATED_OBJECT_KEY);
    }

    @Test
    void getAllFileObjectsByObjectKeyIsNullOrNotNull() {
        // Initialize the database
        insertedFileObject = fileObjectRepository.save(fileObject).block();

        // Get all the fileObjectList where objectKey is not null
        defaultFileObjectFiltering("objectKey.specified=true", "objectKey.specified=false");
    }

    @Test
    void getAllFileObjectsByObjectKeyContainsSomething() {
        // Initialize the database
        insertedFileObject = fileObjectRepository.save(fileObject).block();

        // Get all the fileObjectList where objectKey contains
        defaultFileObjectFiltering("objectKey.contains=" + DEFAULT_OBJECT_KEY, "objectKey.contains=" + UPDATED_OBJECT_KEY);
    }

    @Test
    void getAllFileObjectsByObjectKeyNotContainsSomething() {
        // Initialize the database
        insertedFileObject = fileObjectRepository.save(fileObject).block();

        // Get all the fileObjectList where objectKey does not contain
        defaultFileObjectFiltering("objectKey.doesNotContain=" + UPDATED_OBJECT_KEY, "objectKey.doesNotContain=" + DEFAULT_OBJECT_KEY);
    }

    @Test
    void getAllFileObjectsByContentTypeIsEqualToSomething() {
        // Initialize the database
        insertedFileObject = fileObjectRepository.save(fileObject).block();

        // Get all the fileObjectList where contentType equals to
        defaultFileObjectFiltering("contentType.equals=" + DEFAULT_CONTENT_TYPE, "contentType.equals=" + UPDATED_CONTENT_TYPE);
    }

    @Test
    void getAllFileObjectsByContentTypeIsInShouldWork() {
        // Initialize the database
        insertedFileObject = fileObjectRepository.save(fileObject).block();

        // Get all the fileObjectList where contentType in
        defaultFileObjectFiltering(
            "contentType.in=" + DEFAULT_CONTENT_TYPE + "," + UPDATED_CONTENT_TYPE,
            "contentType.in=" + UPDATED_CONTENT_TYPE
        );
    }

    @Test
    void getAllFileObjectsByContentTypeIsNullOrNotNull() {
        // Initialize the database
        insertedFileObject = fileObjectRepository.save(fileObject).block();

        // Get all the fileObjectList where contentType is not null
        defaultFileObjectFiltering("contentType.specified=true", "contentType.specified=false");
    }

    @Test
    void getAllFileObjectsByContentTypeContainsSomething() {
        // Initialize the database
        insertedFileObject = fileObjectRepository.save(fileObject).block();

        // Get all the fileObjectList where contentType contains
        defaultFileObjectFiltering("contentType.contains=" + DEFAULT_CONTENT_TYPE, "contentType.contains=" + UPDATED_CONTENT_TYPE);
    }

    @Test
    void getAllFileObjectsByContentTypeNotContainsSomething() {
        // Initialize the database
        insertedFileObject = fileObjectRepository.save(fileObject).block();

        // Get all the fileObjectList where contentType does not contain
        defaultFileObjectFiltering(
            "contentType.doesNotContain=" + UPDATED_CONTENT_TYPE,
            "contentType.doesNotContain=" + DEFAULT_CONTENT_TYPE
        );
    }

    @Test
    void getAllFileObjectsByFileSizeIsEqualToSomething() {
        // Initialize the database
        insertedFileObject = fileObjectRepository.save(fileObject).block();

        // Get all the fileObjectList where fileSize equals to
        defaultFileObjectFiltering("fileSize.equals=" + DEFAULT_FILE_SIZE, "fileSize.equals=" + UPDATED_FILE_SIZE);
    }

    @Test
    void getAllFileObjectsByFileSizeIsInShouldWork() {
        // Initialize the database
        insertedFileObject = fileObjectRepository.save(fileObject).block();

        // Get all the fileObjectList where fileSize in
        defaultFileObjectFiltering("fileSize.in=" + DEFAULT_FILE_SIZE + "," + UPDATED_FILE_SIZE, "fileSize.in=" + UPDATED_FILE_SIZE);
    }

    @Test
    void getAllFileObjectsByFileSizeIsNullOrNotNull() {
        // Initialize the database
        insertedFileObject = fileObjectRepository.save(fileObject).block();

        // Get all the fileObjectList where fileSize is not null
        defaultFileObjectFiltering("fileSize.specified=true", "fileSize.specified=false");
    }

    @Test
    void getAllFileObjectsByFileSizeIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedFileObject = fileObjectRepository.save(fileObject).block();

        // Get all the fileObjectList where fileSize is greater than or equal to
        defaultFileObjectFiltering("fileSize.greaterThanOrEqual=" + DEFAULT_FILE_SIZE, "fileSize.greaterThanOrEqual=" + UPDATED_FILE_SIZE);
    }

    @Test
    void getAllFileObjectsByFileSizeIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedFileObject = fileObjectRepository.save(fileObject).block();

        // Get all the fileObjectList where fileSize is less than or equal to
        defaultFileObjectFiltering("fileSize.lessThanOrEqual=" + DEFAULT_FILE_SIZE, "fileSize.lessThanOrEqual=" + SMALLER_FILE_SIZE);
    }

    @Test
    void getAllFileObjectsByFileSizeIsLessThanSomething() {
        // Initialize the database
        insertedFileObject = fileObjectRepository.save(fileObject).block();

        // Get all the fileObjectList where fileSize is less than
        defaultFileObjectFiltering("fileSize.lessThan=" + UPDATED_FILE_SIZE, "fileSize.lessThan=" + DEFAULT_FILE_SIZE);
    }

    @Test
    void getAllFileObjectsByFileSizeIsGreaterThanSomething() {
        // Initialize the database
        insertedFileObject = fileObjectRepository.save(fileObject).block();

        // Get all the fileObjectList where fileSize is greater than
        defaultFileObjectFiltering("fileSize.greaterThan=" + SMALLER_FILE_SIZE, "fileSize.greaterThan=" + DEFAULT_FILE_SIZE);
    }

    @Test
    void getAllFileObjectsByChecksumIsEqualToSomething() {
        // Initialize the database
        insertedFileObject = fileObjectRepository.save(fileObject).block();

        // Get all the fileObjectList where checksum equals to
        defaultFileObjectFiltering("checksum.equals=" + DEFAULT_CHECKSUM, "checksum.equals=" + UPDATED_CHECKSUM);
    }

    @Test
    void getAllFileObjectsByChecksumIsInShouldWork() {
        // Initialize the database
        insertedFileObject = fileObjectRepository.save(fileObject).block();

        // Get all the fileObjectList where checksum in
        defaultFileObjectFiltering("checksum.in=" + DEFAULT_CHECKSUM + "," + UPDATED_CHECKSUM, "checksum.in=" + UPDATED_CHECKSUM);
    }

    @Test
    void getAllFileObjectsByChecksumIsNullOrNotNull() {
        // Initialize the database
        insertedFileObject = fileObjectRepository.save(fileObject).block();

        // Get all the fileObjectList where checksum is not null
        defaultFileObjectFiltering("checksum.specified=true", "checksum.specified=false");
    }

    @Test
    void getAllFileObjectsByChecksumContainsSomething() {
        // Initialize the database
        insertedFileObject = fileObjectRepository.save(fileObject).block();

        // Get all the fileObjectList where checksum contains
        defaultFileObjectFiltering("checksum.contains=" + DEFAULT_CHECKSUM, "checksum.contains=" + UPDATED_CHECKSUM);
    }

    @Test
    void getAllFileObjectsByChecksumNotContainsSomething() {
        // Initialize the database
        insertedFileObject = fileObjectRepository.save(fileObject).block();

        // Get all the fileObjectList where checksum does not contain
        defaultFileObjectFiltering("checksum.doesNotContain=" + UPDATED_CHECKSUM, "checksum.doesNotContain=" + DEFAULT_CHECKSUM);
    }

    @Test
    void getAllFileObjectsByDurationSecondsIsEqualToSomething() {
        // Initialize the database
        insertedFileObject = fileObjectRepository.save(fileObject).block();

        // Get all the fileObjectList where durationSeconds equals to
        defaultFileObjectFiltering(
            "durationSeconds.equals=" + DEFAULT_DURATION_SECONDS,
            "durationSeconds.equals=" + UPDATED_DURATION_SECONDS
        );
    }

    @Test
    void getAllFileObjectsByDurationSecondsIsInShouldWork() {
        // Initialize the database
        insertedFileObject = fileObjectRepository.save(fileObject).block();

        // Get all the fileObjectList where durationSeconds in
        defaultFileObjectFiltering(
            "durationSeconds.in=" + DEFAULT_DURATION_SECONDS + "," + UPDATED_DURATION_SECONDS,
            "durationSeconds.in=" + UPDATED_DURATION_SECONDS
        );
    }

    @Test
    void getAllFileObjectsByDurationSecondsIsNullOrNotNull() {
        // Initialize the database
        insertedFileObject = fileObjectRepository.save(fileObject).block();

        // Get all the fileObjectList where durationSeconds is not null
        defaultFileObjectFiltering("durationSeconds.specified=true", "durationSeconds.specified=false");
    }

    @Test
    void getAllFileObjectsByDurationSecondsIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedFileObject = fileObjectRepository.save(fileObject).block();

        // Get all the fileObjectList where durationSeconds is greater than or equal to
        defaultFileObjectFiltering(
            "durationSeconds.greaterThanOrEqual=" + DEFAULT_DURATION_SECONDS,
            "durationSeconds.greaterThanOrEqual=" + UPDATED_DURATION_SECONDS
        );
    }

    @Test
    void getAllFileObjectsByDurationSecondsIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedFileObject = fileObjectRepository.save(fileObject).block();

        // Get all the fileObjectList where durationSeconds is less than or equal to
        defaultFileObjectFiltering(
            "durationSeconds.lessThanOrEqual=" + DEFAULT_DURATION_SECONDS,
            "durationSeconds.lessThanOrEqual=" + SMALLER_DURATION_SECONDS
        );
    }

    @Test
    void getAllFileObjectsByDurationSecondsIsLessThanSomething() {
        // Initialize the database
        insertedFileObject = fileObjectRepository.save(fileObject).block();

        // Get all the fileObjectList where durationSeconds is less than
        defaultFileObjectFiltering(
            "durationSeconds.lessThan=" + UPDATED_DURATION_SECONDS,
            "durationSeconds.lessThan=" + DEFAULT_DURATION_SECONDS
        );
    }

    @Test
    void getAllFileObjectsByDurationSecondsIsGreaterThanSomething() {
        // Initialize the database
        insertedFileObject = fileObjectRepository.save(fileObject).block();

        // Get all the fileObjectList where durationSeconds is greater than
        defaultFileObjectFiltering(
            "durationSeconds.greaterThan=" + SMALLER_DURATION_SECONDS,
            "durationSeconds.greaterThan=" + DEFAULT_DURATION_SECONDS
        );
    }

    @Test
    void getAllFileObjectsByCreatedDateIsEqualToSomething() {
        // Initialize the database
        insertedFileObject = fileObjectRepository.save(fileObject).block();

        // Get all the fileObjectList where createdDate equals to
        defaultFileObjectFiltering("createdDate.equals=" + DEFAULT_CREATED_DATE, "createdDate.equals=" + UPDATED_CREATED_DATE);
    }

    @Test
    void getAllFileObjectsByCreatedDateIsInShouldWork() {
        // Initialize the database
        insertedFileObject = fileObjectRepository.save(fileObject).block();

        // Get all the fileObjectList where createdDate in
        defaultFileObjectFiltering(
            "createdDate.in=" + DEFAULT_CREATED_DATE + "," + UPDATED_CREATED_DATE,
            "createdDate.in=" + UPDATED_CREATED_DATE
        );
    }

    @Test
    void getAllFileObjectsByCreatedDateIsNullOrNotNull() {
        // Initialize the database
        insertedFileObject = fileObjectRepository.save(fileObject).block();

        // Get all the fileObjectList where createdDate is not null
        defaultFileObjectFiltering("createdDate.specified=true", "createdDate.specified=false");
    }

    @Test
    void getAllFileObjectsByLastModifiedDateIsEqualToSomething() {
        // Initialize the database
        insertedFileObject = fileObjectRepository.save(fileObject).block();

        // Get all the fileObjectList where lastModifiedDate equals to
        defaultFileObjectFiltering(
            "lastModifiedDate.equals=" + DEFAULT_LAST_MODIFIED_DATE,
            "lastModifiedDate.equals=" + UPDATED_LAST_MODIFIED_DATE
        );
    }

    @Test
    void getAllFileObjectsByLastModifiedDateIsInShouldWork() {
        // Initialize the database
        insertedFileObject = fileObjectRepository.save(fileObject).block();

        // Get all the fileObjectList where lastModifiedDate in
        defaultFileObjectFiltering(
            "lastModifiedDate.in=" + DEFAULT_LAST_MODIFIED_DATE + "," + UPDATED_LAST_MODIFIED_DATE,
            "lastModifiedDate.in=" + UPDATED_LAST_MODIFIED_DATE
        );
    }

    @Test
    void getAllFileObjectsByLastModifiedDateIsNullOrNotNull() {
        // Initialize the database
        insertedFileObject = fileObjectRepository.save(fileObject).block();

        // Get all the fileObjectList where lastModifiedDate is not null
        defaultFileObjectFiltering("lastModifiedDate.specified=true", "lastModifiedDate.specified=false");
    }

    @Test
    void getAllFileObjectsByCreatedByIsEqualToSomething() {
        // Initialize the database
        insertedFileObject = fileObjectRepository.save(fileObject).block();

        // Get all the fileObjectList where createdBy equals to
        defaultFileObjectFiltering("createdBy.equals=" + DEFAULT_CREATED_BY, "createdBy.equals=" + UPDATED_CREATED_BY);
    }

    @Test
    void getAllFileObjectsByCreatedByIsInShouldWork() {
        // Initialize the database
        insertedFileObject = fileObjectRepository.save(fileObject).block();

        // Get all the fileObjectList where createdBy in
        defaultFileObjectFiltering("createdBy.in=" + DEFAULT_CREATED_BY + "," + UPDATED_CREATED_BY, "createdBy.in=" + UPDATED_CREATED_BY);
    }

    @Test
    void getAllFileObjectsByCreatedByIsNullOrNotNull() {
        // Initialize the database
        insertedFileObject = fileObjectRepository.save(fileObject).block();

        // Get all the fileObjectList where createdBy is not null
        defaultFileObjectFiltering("createdBy.specified=true", "createdBy.specified=false");
    }

    @Test
    void getAllFileObjectsByCreatedByContainsSomething() {
        // Initialize the database
        insertedFileObject = fileObjectRepository.save(fileObject).block();

        // Get all the fileObjectList where createdBy contains
        defaultFileObjectFiltering("createdBy.contains=" + DEFAULT_CREATED_BY, "createdBy.contains=" + UPDATED_CREATED_BY);
    }

    @Test
    void getAllFileObjectsByCreatedByNotContainsSomething() {
        // Initialize the database
        insertedFileObject = fileObjectRepository.save(fileObject).block();

        // Get all the fileObjectList where createdBy does not contain
        defaultFileObjectFiltering("createdBy.doesNotContain=" + UPDATED_CREATED_BY, "createdBy.doesNotContain=" + DEFAULT_CREATED_BY);
    }

    @Test
    void getAllFileObjectsByLastModifiedByIsEqualToSomething() {
        // Initialize the database
        insertedFileObject = fileObjectRepository.save(fileObject).block();

        // Get all the fileObjectList where lastModifiedBy equals to
        defaultFileObjectFiltering(
            "lastModifiedBy.equals=" + DEFAULT_LAST_MODIFIED_BY,
            "lastModifiedBy.equals=" + UPDATED_LAST_MODIFIED_BY
        );
    }

    @Test
    void getAllFileObjectsByLastModifiedByIsInShouldWork() {
        // Initialize the database
        insertedFileObject = fileObjectRepository.save(fileObject).block();

        // Get all the fileObjectList where lastModifiedBy in
        defaultFileObjectFiltering(
            "lastModifiedBy.in=" + DEFAULT_LAST_MODIFIED_BY + "," + UPDATED_LAST_MODIFIED_BY,
            "lastModifiedBy.in=" + UPDATED_LAST_MODIFIED_BY
        );
    }

    @Test
    void getAllFileObjectsByLastModifiedByIsNullOrNotNull() {
        // Initialize the database
        insertedFileObject = fileObjectRepository.save(fileObject).block();

        // Get all the fileObjectList where lastModifiedBy is not null
        defaultFileObjectFiltering("lastModifiedBy.specified=true", "lastModifiedBy.specified=false");
    }

    @Test
    void getAllFileObjectsByLastModifiedByContainsSomething() {
        // Initialize the database
        insertedFileObject = fileObjectRepository.save(fileObject).block();

        // Get all the fileObjectList where lastModifiedBy contains
        defaultFileObjectFiltering(
            "lastModifiedBy.contains=" + DEFAULT_LAST_MODIFIED_BY,
            "lastModifiedBy.contains=" + UPDATED_LAST_MODIFIED_BY
        );
    }

    @Test
    void getAllFileObjectsByLastModifiedByNotContainsSomething() {
        // Initialize the database
        insertedFileObject = fileObjectRepository.save(fileObject).block();

        // Get all the fileObjectList where lastModifiedBy does not contain
        defaultFileObjectFiltering(
            "lastModifiedBy.doesNotContain=" + UPDATED_LAST_MODIFIED_BY,
            "lastModifiedBy.doesNotContain=" + DEFAULT_LAST_MODIFIED_BY
        );
    }

    private void defaultFileObjectFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultFileObjectShouldBeFound(shouldBeFound);
        defaultFileObjectShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultFileObjectShouldBeFound(String filter) {
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
            .value(hasItem(fileObject.getId().intValue()))
            .jsonPath("$.[*].bucket")
            .value(hasItem(DEFAULT_BUCKET))
            .jsonPath("$.[*].objectKey")
            .value(hasItem(DEFAULT_OBJECT_KEY))
            .jsonPath("$.[*].contentType")
            .value(hasItem(DEFAULT_CONTENT_TYPE))
            .jsonPath("$.[*].fileSize")
            .value(hasItem(DEFAULT_FILE_SIZE.intValue()))
            .jsonPath("$.[*].checksum")
            .value(hasItem(DEFAULT_CHECKSUM))
            .jsonPath("$.[*].durationSeconds")
            .value(hasItem(DEFAULT_DURATION_SECONDS))
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
    private void defaultFileObjectShouldNotBeFound(String filter) {
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
    void getNonExistingFileObject() {
        // Get the fileObject
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingFileObject() throws Exception {
        // Initialize the database
        insertedFileObject = fileObjectRepository.save(fileObject).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the fileObject
        FileObject updatedFileObject = fileObjectRepository.findById(fileObject.getId()).block();
        updatedFileObject
            .bucket(UPDATED_BUCKET)
            .objectKey(UPDATED_OBJECT_KEY)
            .contentType(UPDATED_CONTENT_TYPE)
            .fileSize(UPDATED_FILE_SIZE)
            .checksum(UPDATED_CHECKSUM)
            .durationSeconds(UPDATED_DURATION_SECONDS)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .createdBy(UPDATED_CREATED_BY)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY);
        FileObjectDTO fileObjectDTO = fileObjectMapper.toDto(updatedFileObject);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, fileObjectDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(fileObjectDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the FileObject in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedFileObjectToMatchAllProperties(updatedFileObject);
    }

    @Test
    void putNonExistingFileObject() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        fileObject.setId(longCount.incrementAndGet());

        // Create the FileObject
        FileObjectDTO fileObjectDTO = fileObjectMapper.toDto(fileObject);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, fileObjectDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(fileObjectDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the FileObject in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchFileObject() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        fileObject.setId(longCount.incrementAndGet());

        // Create the FileObject
        FileObjectDTO fileObjectDTO = fileObjectMapper.toDto(fileObject);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(fileObjectDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the FileObject in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamFileObject() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        fileObject.setId(longCount.incrementAndGet());

        // Create the FileObject
        FileObjectDTO fileObjectDTO = fileObjectMapper.toDto(fileObject);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(fileObjectDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the FileObject in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateFileObjectWithPatch() throws Exception {
        // Initialize the database
        insertedFileObject = fileObjectRepository.save(fileObject).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the fileObject using partial update
        FileObject partialUpdatedFileObject = new FileObject();
        partialUpdatedFileObject.setId(fileObject.getId());

        partialUpdatedFileObject
            .bucket(UPDATED_BUCKET)
            .objectKey(UPDATED_OBJECT_KEY)
            .checksum(UPDATED_CHECKSUM)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedFileObject.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedFileObject))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the FileObject in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFileObjectUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedFileObject, fileObject),
            getPersistedFileObject(fileObject)
        );
    }

    @Test
    void fullUpdateFileObjectWithPatch() throws Exception {
        // Initialize the database
        insertedFileObject = fileObjectRepository.save(fileObject).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the fileObject using partial update
        FileObject partialUpdatedFileObject = new FileObject();
        partialUpdatedFileObject.setId(fileObject.getId());

        partialUpdatedFileObject
            .bucket(UPDATED_BUCKET)
            .objectKey(UPDATED_OBJECT_KEY)
            .contentType(UPDATED_CONTENT_TYPE)
            .fileSize(UPDATED_FILE_SIZE)
            .checksum(UPDATED_CHECKSUM)
            .durationSeconds(UPDATED_DURATION_SECONDS)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .createdBy(UPDATED_CREATED_BY)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedFileObject.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedFileObject))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the FileObject in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFileObjectUpdatableFieldsEquals(partialUpdatedFileObject, getPersistedFileObject(partialUpdatedFileObject));
    }

    @Test
    void patchNonExistingFileObject() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        fileObject.setId(longCount.incrementAndGet());

        // Create the FileObject
        FileObjectDTO fileObjectDTO = fileObjectMapper.toDto(fileObject);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, fileObjectDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(fileObjectDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the FileObject in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchFileObject() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        fileObject.setId(longCount.incrementAndGet());

        // Create the FileObject
        FileObjectDTO fileObjectDTO = fileObjectMapper.toDto(fileObject);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(fileObjectDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the FileObject in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamFileObject() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        fileObject.setId(longCount.incrementAndGet());

        // Create the FileObject
        FileObjectDTO fileObjectDTO = fileObjectMapper.toDto(fileObject);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(fileObjectDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the FileObject in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteFileObject() {
        // Initialize the database
        insertedFileObject = fileObjectRepository.save(fileObject).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the fileObject
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, fileObject.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return fileObjectRepository.count().block();
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

    protected FileObject getPersistedFileObject(FileObject fileObject) {
        return fileObjectRepository.findById(fileObject.getId()).block();
    }

    protected void assertPersistedFileObjectToMatchAllProperties(FileObject expectedFileObject) {
        // Test fails because reactive api returns an empty object instead of null
        // assertFileObjectAllPropertiesEquals(expectedFileObject, getPersistedFileObject(expectedFileObject));
        assertFileObjectUpdatableFieldsEquals(expectedFileObject, getPersistedFileObject(expectedFileObject));
    }

    protected void assertPersistedFileObjectToMatchUpdatableProperties(FileObject expectedFileObject) {
        // Test fails because reactive api returns an empty object instead of null
        // assertFileObjectAllUpdatablePropertiesEquals(expectedFileObject, getPersistedFileObject(expectedFileObject));
        assertFileObjectUpdatableFieldsEquals(expectedFileObject, getPersistedFileObject(expectedFileObject));
    }
}
