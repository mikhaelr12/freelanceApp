package com.freelance.app.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.freelance.app.domain.ProfileReview} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProfileReviewDTO implements Serializable {

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

    private ProfileDTO reviewer;

    private ProfileDTO reviewee;

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

    public ProfileDTO getReviewer() {
        return reviewer;
    }

    public void setReviewer(ProfileDTO reviewer) {
        this.reviewer = reviewer;
    }

    public ProfileDTO getReviewee() {
        return reviewee;
    }

    public void setReviewee(ProfileDTO reviewee) {
        this.reviewee = reviewee;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProfileReviewDTO)) {
            return false;
        }

        ProfileReviewDTO profileReviewDTO = (ProfileReviewDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, profileReviewDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProfileReviewDTO{" +
            "id=" + getId() +
            ", text='" + getText() + "'" +
            ", rating=" + getRating() +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", reviewer=" + getReviewer() +
            ", reviewee=" + getReviewee() +
            "}";
    }
}
