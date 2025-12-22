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
 * Criteria class for the {@link com.freelance.app.domain.Dispute} entity. This class is used
 * in {@link com.freelance.app.web.rest.DisputeResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /disputes?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DisputeCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter reason;

    private InstantFilter openedAt;

    private InstantFilter closedAt;

    private LongFilter orderId;

    private Boolean distinct;

    public DisputeCriteria() {}

    public DisputeCriteria(DisputeCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.reason = other.optionalReason().map(StringFilter::copy).orElse(null);
        this.openedAt = other.optionalOpenedAt().map(InstantFilter::copy).orElse(null);
        this.closedAt = other.optionalClosedAt().map(InstantFilter::copy).orElse(null);
        this.orderId = other.optionalOrderId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public DisputeCriteria copy() {
        return new DisputeCriteria(this);
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

    public StringFilter getReason() {
        return reason;
    }

    public Optional<StringFilter> optionalReason() {
        return Optional.ofNullable(reason);
    }

    public StringFilter reason() {
        if (reason == null) {
            setReason(new StringFilter());
        }
        return reason;
    }

    public void setReason(StringFilter reason) {
        this.reason = reason;
    }

    public InstantFilter getOpenedAt() {
        return openedAt;
    }

    public Optional<InstantFilter> optionalOpenedAt() {
        return Optional.ofNullable(openedAt);
    }

    public InstantFilter openedAt() {
        if (openedAt == null) {
            setOpenedAt(new InstantFilter());
        }
        return openedAt;
    }

    public void setOpenedAt(InstantFilter openedAt) {
        this.openedAt = openedAt;
    }

    public InstantFilter getClosedAt() {
        return closedAt;
    }

    public Optional<InstantFilter> optionalClosedAt() {
        return Optional.ofNullable(closedAt);
    }

    public InstantFilter closedAt() {
        if (closedAt == null) {
            setClosedAt(new InstantFilter());
        }
        return closedAt;
    }

    public void setClosedAt(InstantFilter closedAt) {
        this.closedAt = closedAt;
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
        final DisputeCriteria that = (DisputeCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(reason, that.reason) &&
            Objects.equals(openedAt, that.openedAt) &&
            Objects.equals(closedAt, that.closedAt) &&
            Objects.equals(orderId, that.orderId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, reason, openedAt, closedAt, orderId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DisputeCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalReason().map(f -> "reason=" + f + ", ").orElse("") +
            optionalOpenedAt().map(f -> "openedAt=" + f + ", ").orElse("") +
            optionalClosedAt().map(f -> "closedAt=" + f + ", ").orElse("") +
            optionalOrderId().map(f -> "orderId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
