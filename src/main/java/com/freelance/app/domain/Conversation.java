package com.freelance.app.domain;

import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Conversation.
 */
@Table("conversation")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Conversation implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("created_at")
    private Instant createdAt;

    @Column("participant_a_id")
    private Long participantAId;

    @Column("participant_b_id")
    private Long participantBId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Conversation id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public Conversation createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Long getParticipantAId() {
        return participantAId;
    }

    public void setParticipantAId(Long participantAId) {
        this.participantAId = participantAId;
    }

    public Long getParticipantBId() {
        return participantBId;
    }

    public void setParticipantBId(Long participantBId) {
        this.participantBId = participantBId;
    }

    public Conversation participantAId(Long participantAId) {
        this.setParticipantAId(participantAId);
        return this;
    }

    public Conversation participantBId(Long participantBId) {
        this.setParticipantBId(participantBId);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Conversation)) {
            return false;
        }
        return getId() != null && getId().equals(((Conversation) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Conversation{" +
            "id=" + getId() +
            ", createdAt='" + getCreatedAt() + "'" +
            "}";
    }
}
