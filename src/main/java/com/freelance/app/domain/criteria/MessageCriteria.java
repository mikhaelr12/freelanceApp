package com.freelance.app.domain.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.InstantFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link com.freelance.app.domain.Message} entity. This class is used
 * in {@link com.freelance.app.web.rest.MessageResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /messages?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MessageCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter body;

    private InstantFilter sentAt;

    private LongFilter conversationId;

    private LongFilter senderId;

    private Boolean distinct;

    public MessageCriteria(MessageCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.body = other.optionalBody().map(StringFilter::copy).orElse(null);
        this.sentAt = other.optionalSentAt().map(InstantFilter::copy).orElse(null);
        this.conversationId = other.optionalConversationId().map(LongFilter::copy).orElse(null);
        this.senderId = other.optionalSenderId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public MessageCriteria copy() {
        return new MessageCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getBody() {
        return body;
    }

    public Optional<StringFilter> optionalBody() {
        return Optional.ofNullable(body);
    }

    public StringFilter body() {
        if (body == null) {
            setBody(new StringFilter());
        }
        return body;
    }

    public void setBody(StringFilter body) {
        this.body = body;
    }

    public InstantFilter getSentAt() {
        return sentAt;
    }

    public Optional<InstantFilter> optionalSentAt() {
        return Optional.ofNullable(sentAt);
    }

    public InstantFilter sentAt() {
        if (sentAt == null) {
            setSentAt(new InstantFilter());
        }
        return sentAt;
    }

    public void setSentAt(InstantFilter sentAt) {
        this.sentAt = sentAt;
    }

    public LongFilter getConversationId() {
        return conversationId;
    }

    public Optional<LongFilter> optionalConversationId() {
        return Optional.ofNullable(conversationId);
    }

    public LongFilter conversationId() {
        if (conversationId == null) {
            setConversationId(new LongFilter());
        }
        return conversationId;
    }

    public void setConversationId(LongFilter conversationId) {
        this.conversationId = conversationId;
    }

    public LongFilter getSenderId() {
        return senderId;
    }

    public Optional<LongFilter> optionalSenderId() {
        return Optional.ofNullable(senderId);
    }

    public LongFilter senderId() {
        if (senderId == null) {
            setSenderId(new LongFilter());
        }
        return senderId;
    }

    public void setSenderId(LongFilter senderId) {
        this.senderId = senderId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final MessageCriteria that = (MessageCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(body, that.body) &&
            Objects.equals(sentAt, that.sentAt) &&
            Objects.equals(conversationId, that.conversationId) &&
            Objects.equals(senderId, that.senderId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, body, sentAt, conversationId, senderId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MessageCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalBody().map(f -> "body=" + f + ", ").orElse("") +
            optionalSentAt().map(f -> "sentAt=" + f + ", ").orElse("") +
            optionalConversationId().map(f -> "conversationId=" + f + ", ").orElse("") +
            optionalSenderId().map(f -> "senderId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
