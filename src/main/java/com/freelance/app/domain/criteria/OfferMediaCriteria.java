package com.freelance.app.domain.criteria;

import com.freelance.app.domain.enumeration.MediaKind;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.freelance.app.domain.OfferMedia} entity. This class is used
 * in {@link com.freelance.app.web.rest.OfferMediaResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /offer-medias?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OfferMediaCriteria implements Serializable, Criteria {

    /**
     * Class for filtering MediaKind
     */
    public static class MediaKindFilter extends Filter<MediaKind> {

        public MediaKindFilter() {}

        public MediaKindFilter(MediaKindFilter filter) {
            super(filter);
        }

        @Override
        public MediaKindFilter copy() {
            return new MediaKindFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private MediaKindFilter mediaKind;

    private BooleanFilter isPrimary;

    private StringFilter caption;

    private InstantFilter createdDate;

    private InstantFilter lastModifiedDate;

    private StringFilter createdBy;

    private StringFilter lastModifiedBy;

    private LongFilter offerId;

    private LongFilter fileId;

    private Boolean distinct;

    public OfferMediaCriteria(OfferMediaCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.mediaKind = other.optionalMediaKind().map(MediaKindFilter::copy).orElse(null);
        this.isPrimary = other.optionalIsPrimary().map(BooleanFilter::copy).orElse(null);
        this.caption = other.optionalCaption().map(StringFilter::copy).orElse(null);
        this.createdDate = other.optionalCreatedDate().map(InstantFilter::copy).orElse(null);
        this.lastModifiedDate = other.optionalLastModifiedDate().map(InstantFilter::copy).orElse(null);
        this.createdBy = other.optionalCreatedBy().map(StringFilter::copy).orElse(null);
        this.lastModifiedBy = other.optionalLastModifiedBy().map(StringFilter::copy).orElse(null);
        this.offerId = other.optionalOfferId().map(LongFilter::copy).orElse(null);
        this.fileId = other.optionalFileId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public OfferMediaCriteria copy() {
        return new OfferMediaCriteria(this);
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

    public MediaKindFilter getMediaKind() {
        return mediaKind;
    }

    public Optional<MediaKindFilter> optionalMediaKind() {
        return Optional.ofNullable(mediaKind);
    }

    public MediaKindFilter mediaKind() {
        if (mediaKind == null) {
            setMediaKind(new MediaKindFilter());
        }
        return mediaKind;
    }

    public void setMediaKind(MediaKindFilter mediaKind) {
        this.mediaKind = mediaKind;
    }

    public BooleanFilter getIsPrimary() {
        return isPrimary;
    }

    public Optional<BooleanFilter> optionalIsPrimary() {
        return Optional.ofNullable(isPrimary);
    }

    public BooleanFilter isPrimary() {
        if (isPrimary == null) {
            setIsPrimary(new BooleanFilter());
        }
        return isPrimary;
    }

    public void setIsPrimary(BooleanFilter isPrimary) {
        this.isPrimary = isPrimary;
    }

    public StringFilter getCaption() {
        return caption;
    }

    public Optional<StringFilter> optionalCaption() {
        return Optional.ofNullable(caption);
    }

    public StringFilter caption() {
        if (caption == null) {
            setCaption(new StringFilter());
        }
        return caption;
    }

    public void setCaption(StringFilter caption) {
        this.caption = caption;
    }

    public InstantFilter getCreatedDate() {
        return createdDate;
    }

    public Optional<InstantFilter> optionalCreatedDate() {
        return Optional.ofNullable(createdDate);
    }

    public InstantFilter createdDate() {
        if (createdDate == null) {
            setCreatedDate(new InstantFilter());
        }
        return createdDate;
    }

    public void setCreatedDate(InstantFilter createdDate) {
        this.createdDate = createdDate;
    }

    public InstantFilter getLastModifiedDate() {
        return lastModifiedDate;
    }

    public Optional<InstantFilter> optionalLastModifiedDate() {
        return Optional.ofNullable(lastModifiedDate);
    }

    public InstantFilter lastModifiedDate() {
        if (lastModifiedDate == null) {
            setLastModifiedDate(new InstantFilter());
        }
        return lastModifiedDate;
    }

    public void setLastModifiedDate(InstantFilter lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public StringFilter getCreatedBy() {
        return createdBy;
    }

    public Optional<StringFilter> optionalCreatedBy() {
        return Optional.ofNullable(createdBy);
    }

    public StringFilter createdBy() {
        if (createdBy == null) {
            setCreatedBy(new StringFilter());
        }
        return createdBy;
    }

    public void setCreatedBy(StringFilter createdBy) {
        this.createdBy = createdBy;
    }

    public StringFilter getLastModifiedBy() {
        return lastModifiedBy;
    }

    public Optional<StringFilter> optionalLastModifiedBy() {
        return Optional.ofNullable(lastModifiedBy);
    }

    public StringFilter lastModifiedBy() {
        if (lastModifiedBy == null) {
            setLastModifiedBy(new StringFilter());
        }
        return lastModifiedBy;
    }

    public void setLastModifiedBy(StringFilter lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
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
        final OfferMediaCriteria that = (OfferMediaCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(mediaKind, that.mediaKind) &&
            Objects.equals(isPrimary, that.isPrimary) &&
            Objects.equals(caption, that.caption) &&
            Objects.equals(createdDate, that.createdDate) &&
            Objects.equals(lastModifiedDate, that.lastModifiedDate) &&
            Objects.equals(createdBy, that.createdBy) &&
            Objects.equals(lastModifiedBy, that.lastModifiedBy) &&
            Objects.equals(offerId, that.offerId) &&
            Objects.equals(fileId, that.fileId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            mediaKind,
            isPrimary,
            caption,
            createdDate,
            lastModifiedDate,
            createdBy,
            lastModifiedBy,
            offerId,
            fileId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OfferMediaCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalMediaKind().map(f -> "mediaKind=" + f + ", ").orElse("") +
            optionalIsPrimary().map(f -> "isPrimary=" + f + ", ").orElse("") +
            optionalCaption().map(f -> "caption=" + f + ", ").orElse("") +
            optionalCreatedDate().map(f -> "createdDate=" + f + ", ").orElse("") +
            optionalLastModifiedDate().map(f -> "lastModifiedDate=" + f + ", ").orElse("") +
            optionalCreatedBy().map(f -> "createdBy=" + f + ", ").orElse("") +
            optionalLastModifiedBy().map(f -> "lastModifiedBy=" + f + ", ").orElse("") +
            optionalOfferId().map(f -> "offerId=" + f + ", ").orElse("") +
            optionalFileId().map(f -> "fileId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
