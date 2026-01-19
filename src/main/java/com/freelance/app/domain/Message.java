package com.freelance.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Message.
 */
@Table("message")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Message implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Size(max = 4096)
    @Column("body")
    private String body;

    @NotNull(message = "must not be null")
    @Column("sent_at")
    private Instant sentAt;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "order" }, allowSetters = true)
    private Conversation conversation;

    @org.springframework.data.annotation.Transient
    private Profile sender;

    @org.springframework.data.annotation.Transient
    private Profile receiver;

    @Column("conversation_id")
    private Long conversationId;

    @Column("sender_id")
    private Long senderId;

    @Column("receiver_id")
    private Long receiverId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Message id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBody() {
        return this.body;
    }

    public Message body(String body) {
        this.setBody(body);
        return this;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Instant getSentAt() {
        return this.sentAt;
    }

    public Message sentAt(Instant sentAt) {
        this.setSentAt(sentAt);
        return this;
    }

    public void setSentAt(Instant sentAt) {
        this.sentAt = sentAt;
    }

    public Conversation getConversation() {
        return this.conversation;
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
        this.conversationId = conversation != null ? conversation.getId() : null;
    }

    public Message conversation(Conversation conversation) {
        this.setConversation(conversation);
        return this;
    }

    public Profile getSender() {
        return this.sender;
    }

    public void setSender(Profile user) {
        this.sender = user;
        this.senderId = user != null ? user.getId() : null;
    }

    public Message sender(Profile user) {
        this.setSender(user);
        return this;
    }

    public Profile getReceiver() {
        return receiver;
    }

    public void setReceiver(Profile receiver) {
        this.receiver = receiver;
    }

    public Message receiver(Profile receiver) {
        this.setReceiver(receiver);
        return this;
    }

    public Long getConversationId() {
        return this.conversationId;
    }

    public void setConversationId(Long conversation) {
        this.conversationId = conversation;
    }

    public Long getSenderId() {
        return this.senderId;
    }

    public void setSenderId(Long user) {
        this.senderId = user;
    }

    public Message conversationId(Long conversationId) {
        this.setConversationId(conversationId);
        return this;
    }

    public Message senderId(Long senderId) {
        this.setSenderId(senderId);
        return this;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public Message receiverId(Long receiverId) {
        this.setReceiverId(receiverId);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Message)) {
            return false;
        }
        return getId() != null && getId().equals(((Message) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Message{" +
            "id=" + getId() +
            ", body='" + getBody() + "'" +
            ", sentAt='" + getSentAt() + "'" +
            "}";
    }
}
