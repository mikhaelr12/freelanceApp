package com.freelance.app.service.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.freelance.app.domain.OfferReview} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OfferReviewDTO implements Serializable {

    private Long id;

    @Size(max = 500)
    private String text;

    @NotNull(message = "must not be null")
    @DecimalMin(value = "1")
    @DecimalMax(value = "5")
    private Double rating;

    @NotNull(message = "must not be null")
    private Instant createdDate;

    private Instant lastModifiedDate;

    @Size(max = 50)
    private String createdBy;

    @Size(max = 50)
    private String lastModifiedBy;

    private OfferDTO offer;

    private ProfileDTO reviewer;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public Instant getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public OfferDTO getOffer() {
        return offer;
    }

    public void setOffer(OfferDTO offer) {
        this.offer = offer;
    }

    public ProfileDTO getReviewer() {
        return reviewer;
    }

    public void setReviewer(ProfileDTO reviewer) {
        this.reviewer = reviewer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OfferReviewDTO)) {
            return false;
        }

        OfferReviewDTO offerReviewDTO = (OfferReviewDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, offerReviewDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OfferReviewDTO{" +
            "id=" + getId() +
            ", text='" + getText() + "'" +
            ", rating=" + getRating() +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", offer=" + getOffer() +
            ", reviewer=" + getReviewer() +
            "}";
    }
}
