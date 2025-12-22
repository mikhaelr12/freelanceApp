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
 * Criteria class for the {@link com.freelance.app.domain.Delivery} entity. This class is used
 * in {@link com.freelance.app.web.rest.DeliveryResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /deliveries?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DeliveryCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter note;

    private InstantFilter deliveredAt;

    private LongFilter orderId;

    private LongFilter fileId;

    private Boolean distinct;

    public DeliveryCriteria() {}

    public DeliveryCriteria(DeliveryCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.note = other.optionalNote().map(StringFilter::copy).orElse(null);
        this.deliveredAt = other.optionalDeliveredAt().map(InstantFilter::copy).orElse(null);
        this.orderId = other.optionalOrderId().map(LongFilter::copy).orElse(null);
        this.fileId = other.optionalFileId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public DeliveryCriteria copy() {
        return new DeliveryCriteria(this);
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

    public StringFilter getNote() {
        return note;
    }

    public Optional<StringFilter> optionalNote() {
        return Optional.ofNullable(note);
    }

    public StringFilter note() {
        if (note == null) {
            setNote(new StringFilter());
        }
        return note;
    }

    public void setNote(StringFilter note) {
        this.note = note;
    }

    public InstantFilter getDeliveredAt() {
        return deliveredAt;
    }

    public Optional<InstantFilter> optionalDeliveredAt() {
        return Optional.ofNullable(deliveredAt);
    }

    public InstantFilter deliveredAt() {
        if (deliveredAt == null) {
            setDeliveredAt(new InstantFilter());
        }
        return deliveredAt;
    }

    public void setDeliveredAt(InstantFilter deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    public LongFilter getOrderId() {
        return orderId;
    }

    public Optional<LongFilter> optionalOrderId() {
        return Optional.ofNullable(orderId);
    }

    public LongFilter orderId() {
        if (orderId == null) {
            setOrderId(new LongFilter());
        }
        return orderId;
    }

    public void setOrderId(LongFilter orderId) {
        this.orderId = orderId;
    }

    public LongFilter getFileId() {
        return fileId;
    }

    public Optional<LongFilter> optionalFileId() {
        return Optional.ofNullable(fileId);
    }

    public LongFilter fileId() {
        if (fileId == null) {
            setFileId(new LongFilter());
        }
        return fileId;
    }

    public void setFileId(LongFilter fileId) {
        this.fileId = fileId;
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
        final DeliveryCriteria that = (DeliveryCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(note, that.note) &&
            Objects.equals(deliveredAt, that.deliveredAt) &&
            Objects.equals(orderId, that.orderId) &&
            Objects.equals(fileId, that.fileId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, note, deliveredAt, orderId, fileId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DeliveryCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalNote().map(f -> "note=" + f + ", ").orElse("") +
            optionalDeliveredAt().map(f -> "deliveredAt=" + f + ", ").orElse("") +
            optionalOrderId().map(f -> "orderId=" + f + ", ").orElse("") +
            optionalFileId().map(f -> "fileId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
