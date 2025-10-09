package com.freelance.app.domain.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.InstantFilter;
import tech.jhipster.service.filter.LongFilter;

/**
 * Criteria class for the {@link com.freelance.app.domain.FavoriteOffer} entity. This class is used
 * in {@link com.freelance.app.web.rest.FavoriteOfferResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /favorite-offers?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class FavoriteOfferCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private InstantFilter createdAt;

    private LongFilter profileId;

    private LongFilter offerId;

    private Boolean distinct;

    public FavoriteOfferCriteria() {}

    public FavoriteOfferCriteria(FavoriteOfferCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.createdAt = other.optionalCreatedAt().map(InstantFilter::copy).orElse(null);
        this.profileId = other.optionalProfileId().map(LongFilter::copy).orElse(null);
        this.offerId = other.optionalOfferId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public FavoriteOfferCriteria copy() {
        return new FavoriteOfferCriteria(this);
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

    public InstantFilter getCreatedAt() {
        return createdAt;
    }

    public Optional<InstantFilter> optionalCreatedAt() {
        return Optional.ofNullable(createdAt);
    }

    public InstantFilter createdAt() {
        if (createdAt == null) {
            setCreatedAt(new InstantFilter());
        }
        return createdAt;
    }

    public void setCreatedAt(InstantFilter createdAt) {
        this.createdAt = createdAt;
    }

    public LongFilter getProfileId() {
        return profileId;
    }

    public Optional<LongFilter> optionalProfileId() {
        return Optional.ofNullable(profileId);
    }

    public LongFilter profileId() {
        if (profileId == null) {
            setProfileId(new LongFilter());
        }
        return profileId;
    }

    public void setProfileId(LongFilter profileId) {
        this.profileId = profileId;
    }

    public LongFilter getOfferId() {
        return offerId;
    }

    public Optional<LongFilter> optionalOfferId() {
        return Optional.ofNullable(offerId);
    }

    public LongFilter offerId() {
        if (offerId == null) {
            setOfferId(new LongFilter());
        }
        return offerId;
    }

    public void setOfferId(LongFilter offerId) {
        this.offerId = offerId;
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
        final FavoriteOfferCriteria that = (FavoriteOfferCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(profileId, that.profileId) &&
            Objects.equals(offerId, that.offerId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, createdAt, profileId, offerId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FavoriteOfferCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
            optionalProfileId().map(f -> "profileId=" + f + ", ").orElse("") +
            optionalOfferId().map(f -> "offerId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
