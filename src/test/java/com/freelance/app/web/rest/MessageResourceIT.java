package com.freelance.app.web.rest;

import static com.freelance.app.domain.MessageAsserts.*;
import static com.freelance.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.freelance.app.IntegrationTest;
import com.freelance.app.domain.Conversation;
import com.freelance.app.domain.Message;
import com.freelance.app.domain.User;
import com.freelance.app.repository.ConversationRepository;
import com.freelance.app.repository.EntityManager;
import com.freelance.app.repository.MessageRepository;
import com.freelance.app.repository.UserRepository;
import com.freelance.app.service.MessageService;
import com.freelance.app.service.dto.MessageDTO;
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
 * Integration tests for the {@link MessageResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class MessageResourceIT {

    private static final String DEFAULT_BODY = "AAAAAAAAAA";
    private static final String UPDATED_BODY = "BBBBBBBBBB";

    private static final Instant DEFAULT_SENT_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_SENT_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/messages";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private MessageRepository messageRepositoryMock;

    @Autowired
    private MessageMapper messageMapper;

    @Mock
    private MessageService messageServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Message message;

    private Message insertedMessage;

    @Autowired
    private ConversationRepository conversationRepository;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Message createEntity() {
        return new Message().body(DEFAULT_BODY).sentAt(DEFAULT_SENT_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Message createUpdatedEntity() {
        return new Message().body(UPDATED_BODY).sentAt(UPDATED_SENT_AT);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Message.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        message = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedMessage != null) {
            messageRepository.delete(insertedMessage).block();
            insertedMessage = null;
        }
        deleteEntities(em);
        userRepository.deleteAllUserAuthorities().block();
        userRepository.deleteAll().block();
    }

    @Test
    void createMessage() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Message
        MessageDTO messageDTO = messageMapper.toDto(message);
        var returnedMessageDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(messageDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(MessageDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Message in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedMessage = messageMapper.toEntity(returnedMessageDTO);
        assertMessageUpdatableFieldsEquals(returnedMessage, getPersistedMessage(returnedMessage));

        insertedMessage = returnedMessage;
    }

    @Test
    void createMessageWithExistingId() throws Exception {
        // Create the Message with an existing ID
        message.setId(1L);
        MessageDTO messageDTO = messageMapper.toDto(message);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(messageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Message in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkBodyIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        message.setBody(null);

        // Create the Message, which fails.
        MessageDTO messageDTO = messageMapper.toDto(message);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(messageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkSentAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        message.setSentAt(null);

        // Create the Message, which fails.
        MessageDTO messageDTO = messageMapper.toDto(message);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(messageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllMessages() {
        // Initialize the database
        insertedMessage = messageRepository.save(message).block();

        // Get all the messageList
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
            .value(hasItem(message.getId().intValue()))
            .jsonPath("$.[*].body")
            .value(hasItem(DEFAULT_BODY))
            .jsonPath("$.[*].sentAt")
            .value(hasItem(DEFAULT_SENT_AT.toString()));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMessagesWithEagerRelationshipsIsEnabled() {
        when(messageServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(messageServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMessagesWithEagerRelationshipsIsNotEnabled() {
        when(messageServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(messageRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getMessage() {
        // Initialize the database
        insertedMessage = messageRepository.save(message).block();

        // Get the message
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, message.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(message.getId().intValue()))
            .jsonPath("$.body")
            .value(is(DEFAULT_BODY))
            .jsonPath("$.sentAt")
            .value(is(DEFAULT_SENT_AT.toString()));
    }

    @Test
    void getMessagesByIdFiltering() {
        // Initialize the database
        insertedMessage = messageRepository.save(message).block();

        Long id = message.getId();

        defaultMessageFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultMessageFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultMessageFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllMessagesByBodyIsEqualToSomething() {
        // Initialize the database
        insertedMessage = messageRepository.save(message).block();

        // Get all the messageList where body equals to
        defaultMessageFiltering("body.equals=" + DEFAULT_BODY, "body.equals=" + UPDATED_BODY);
    }

    @Test
    void getAllMessagesByBodyIsInShouldWork() {
        // Initialize the database
        insertedMessage = messageRepository.save(message).block();

        // Get all the messageList where body in
        defaultMessageFiltering("body.in=" + DEFAULT_BODY + "," + UPDATED_BODY, "body.in=" + UPDATED_BODY);
    }

    @Test
    void getAllMessagesByBodyIsNullOrNotNull() {
        // Initialize the database
        insertedMessage = messageRepository.save(message).block();

        // Get all the messageList where body is not null
        defaultMessageFiltering("body.specified=true", "body.specified=false");
    }

    @Test
    void getAllMessagesByBodyContainsSomething() {
        // Initialize the database
        insertedMessage = messageRepository.save(message).block();

        // Get all the messageList where body contains
        defaultMessageFiltering("body.contains=" + DEFAULT_BODY, "body.contains=" + UPDATED_BODY);
    }

    @Test
    void getAllMessagesByBodyNotContainsSomething() {
        // Initialize the database
        insertedMessage = messageRepository.save(message).block();

        // Get all the messageList where body does not contain
        defaultMessageFiltering("body.doesNotContain=" + UPDATED_BODY, "body.doesNotContain=" + DEFAULT_BODY);
    }

    @Test
    void getAllMessagesBySentAtIsEqualToSomething() {
        // Initialize the database
        insertedMessage = messageRepository.save(message).block();

        // Get all the messageList where sentAt equals to
        defaultMessageFiltering("sentAt.equals=" + DEFAULT_SENT_AT, "sentAt.equals=" + UPDATED_SENT_AT);
    }

    @Test
    void getAllMessagesBySentAtIsInShouldWork() {
        // Initialize the database
        insertedMessage = messageRepository.save(message).block();

        // Get all the messageList where sentAt in
        defaultMessageFiltering("sentAt.in=" + DEFAULT_SENT_AT + "," + UPDATED_SENT_AT, "sentAt.in=" + UPDATED_SENT_AT);
    }

    @Test
    void getAllMessagesBySentAtIsNullOrNotNull() {
        // Initialize the database
        insertedMessage = messageRepository.save(message).block();

        // Get all the messageList where sentAt is not null
        defaultMessageFiltering("sentAt.specified=true", "sentAt.specified=false");
    }

    @Test
    void getAllMessagesByConversationIsEqualToSomething() {
        Conversation conversation = ConversationResourceIT.createEntity();
        conversationRepository.save(conversation).block();
        Long conversationId = conversation.getId();
        message.setConversationId(conversationId);
        insertedMessage = messageRepository.save(message).block();
        // Get all the messageList where conversation equals to conversationId
        defaultMessageShouldBeFound("conversationId.equals=" + conversationId);

        // Get all the messageList where conversation equals to (conversationId + 1)
        defaultMessageShouldNotBeFound("conversationId.equals=" + (conversationId + 1));
    }

    @Test
    void getAllMessagesBySenderIsEqualToSomething() {
        User sender = UserResourceIT.createEntity();
        userRepository.save(sender).block();
        Long senderId = sender.getId();
        message.setSenderId(senderId);
        insertedMessage = messageRepository.save(message).block();
        // Get all the messageList where sender equals to senderId
        defaultMessageShouldBeFound("senderId.equals=" + senderId);

        // Get all the messageList where sender equals to (senderId + 1)
        defaultMessageShouldNotBeFound("senderId.equals=" + (senderId + 1));
    }

    private void defaultMessageFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultMessageShouldBeFound(shouldBeFound);
        defaultMessageShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultMessageShouldBeFound(String filter) {
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
            .value(hasItem(message.getId().intValue()))
            .jsonPath("$.[*].body")
            .value(hasItem(DEFAULT_BODY))
            .jsonPath("$.[*].sentAt")
            .value(hasItem(DEFAULT_SENT_AT.toString()));

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
    private void defaultMessageShouldNotBeFound(String filter) {
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
    void getNonExistingMessage() {
        // Get the message
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingMessage() throws Exception {
        // Initialize the database
        insertedMessage = messageRepository.save(message).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the message
        Message updatedMessage = messageRepository.findById(message.getId()).block();
        updatedMessage.body(UPDATED_BODY).sentAt(UPDATED_SENT_AT);
        MessageDTO messageDTO = messageMapper.toDto(updatedMessage);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, messageDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(messageDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Message in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMessageToMatchAllProperties(updatedMessage);
    }

    @Test
    void putNonExistingMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        message.setId(longCount.incrementAndGet());

        // Create the Message
        MessageDTO messageDTO = messageMapper.toDto(message);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, messageDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(messageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Message in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        message.setId(longCount.incrementAndGet());

        // Create the Message
        MessageDTO messageDTO = messageMapper.toDto(message);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(messageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Message in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        message.setId(longCount.incrementAndGet());

        // Create the Message
        MessageDTO messageDTO = messageMapper.toDto(message);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(messageDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Message in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateMessageWithPatch() throws Exception {
        // Initialize the database
        insertedMessage = messageRepository.save(message).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the message using partial update
        Message partialUpdatedMessage = new Message();
        partialUpdatedMessage.setId(message.getId());

        partialUpdatedMessage.body(UPDATED_BODY);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMessage.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedMessage))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Message in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMessageUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedMessage, message), getPersistedMessage(message));
    }

    @Test
    void fullUpdateMessageWithPatch() throws Exception {
        // Initialize the database
        insertedMessage = messageRepository.save(message).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the message using partial update
        Message partialUpdatedMessage = new Message();
        partialUpdatedMessage.setId(message.getId());

        partialUpdatedMessage.body(UPDATED_BODY).sentAt(UPDATED_SENT_AT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMessage.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedMessage))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Message in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMessageUpdatableFieldsEquals(partialUpdatedMessage, getPersistedMessage(partialUpdatedMessage));
    }

    @Test
    void patchNonExistingMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        message.setId(longCount.incrementAndGet());

        // Create the Message
        MessageDTO messageDTO = messageMapper.toDto(message);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, messageDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(messageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Message in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        message.setId(longCount.incrementAndGet());

        // Create the Message
        MessageDTO messageDTO = messageMapper.toDto(message);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(messageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Message in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        message.setId(longCount.incrementAndGet());

        // Create the Message
        MessageDTO messageDTO = messageMapper.toDto(message);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(messageDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Message in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteMessage() {
        // Initialize the database
        insertedMessage = messageRepository.save(message).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the message
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, message.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return messageRepository.count().block();
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

    protected Message getPersistedMessage(Message message) {
        return messageRepository.findById(message.getId()).block();
    }

    protected void assertPersistedMessageToMatchAllProperties(Message expectedMessage) {
        // Test fails because reactive api returns an empty object instead of null
        // assertMessageAllPropertiesEquals(expectedMessage, getPersistedMessage(expectedMessage));
        assertMessageUpdatableFieldsEquals(expectedMessage, getPersistedMessage(expectedMessage));
    }

    protected void assertPersistedMessageToMatchUpdatableProperties(Message expectedMessage) {
        // Test fails because reactive api returns an empty object instead of null
        // assertMessageAllUpdatablePropertiesEquals(expectedMessage, getPersistedMessage(expectedMessage));
        assertMessageUpdatableFieldsEquals(expectedMessage, getPersistedMessage(expectedMessage));
    }
}
