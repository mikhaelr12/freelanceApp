package com.freelance.app.domain.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.LongFilter;

/**
 * Criteria class for the {@link com.freelance.app.domain.VerificationRequest} entity. This class is used
 * in {@link com.freelance.app.web.rest.VerificationRequestResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /verification-requests?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class VerificationRequestCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LongFilter profileId;

    private LongFilter fileObjectId;

    private Boolean distinct;

    public VerificationRequestCriteria() {}

    public VerificationRequestCriteria(VerificationRequestCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.profileId = other.optionalProfileId().map(LongFilter::copy).orElse(null);
        this.fileObjectId = other.optionalFileObjectId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public VerificationRequestCriteria copy() {
        return new VerificationRequestCriteria(this);
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

    public LongFilter getFileObjectId() {
        return fileObjectId;
    }

    public Optional<LongFilter> optionalFileObjectId() {
        return Optional.ofNullable(fileObjectId);
    }

    public LongFilter fileObjectId() {
        if (fileObjectId == null) {
            setFileObjectId(new LongFilter());
        }
        return fileObjectId;
    }

    public void setFileObjectId(LongFilter fileObjectId) {
        this.fileObjectId = fileObjectId;
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
        final VerificationRequestCriteria that = (VerificationRequestCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(profileId, that.profileId) &&
            Objects.equals(fileObjectId, that.fileObjectId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, profileId, fileObjectId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "VerificationRequestCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalProfileId().map(f -> "profileId=" + f + ", ").orElse("") +
            optionalFileObjectId().map(f -> "fileObjectId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
