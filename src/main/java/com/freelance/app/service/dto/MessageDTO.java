package com.freelance.app.service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.freelance.app.domain.Message} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MessageDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    @Size(max = 4096)
    private String body;

    @NotNull(message = "must not be null")
    private Instant sentAt;

    private ConversationDTO conversation;

    private UserDTO sender;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Instant getSentAt() {
        return sentAt;
    }

    public void setSentAt(Instant sentAt) {
        this.sentAt = sentAt;
    }

    public ConversationDTO getConversation() {
        return conversation;
    }

    public void setConversation(ConversationDTO conversation) {
        this.conversation = conversation;
    }

    public UserDTO getSender() {
        return sender;
    }

    public void setSender(UserDTO sender) {
        this.sender = sender;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MessageDTO)) {
            return false;
        }

        MessageDTO messageDTO = (MessageDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, messageDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MessageDTO{" +
            "id=" + getId() +
            ", body='" + getBody() + "'" +
            ", sentAt='" + getSentAt() + "'" +
            ", conversation=" + getConversation() +
            ", sender=" + getSender() +
            "}";
    }
}
