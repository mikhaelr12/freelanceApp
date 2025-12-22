package com.freelance.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A ProfileReview.
 */
@Table("profile_review")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProfileReview extends AbstractAuditingEntity<Long> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Size(max = 500)
    @Column("text")
    private String text;

    @NotNull(message = "must not be null")
    @DecimalMin(value = "1")
    @DecimalMax(value = "5")
    @Column("rating")
    private Double rating;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "user", "profilePicture", "skills" }, allowSetters = true)
    private Profile reviewer;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "user", "profilePicture", "skills" }, allowSetters = true)
    private Profile reviewee;

    @Column("reviewer_id")
    private Long reviewerId;

    @Column("reviewee_id")
    private Long revieweeId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ProfileReview id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return this.text;
    }

    public ProfileReview text(String text) {
        this.setText(text);
        return this;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Double getRating() {
        return this.rating;
    }

    public ProfileReview rating(Double rating) {
        this.setRating(rating);
        return this;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Profile getReviewer() {
        return this.reviewer;
    }

    public void setReviewer(Profile profile) {
        this.reviewer = profile;
        this.reviewerId = profile != null ? profile.getId() : null;
    }

    public ProfileReview reviewer(Profile profile) {
        this.setReviewer(profile);
        return this;
    }

    public Profile getReviewee() {
        return this.reviewee;
    }

    public void setReviewee(Profile profile) {
        this.reviewee = profile;
        this.revieweeId = profile != null ? profile.getId() : null;
    }

    public ProfileReview reviewee(Profile profile) {
        this.setReviewee(profile);
        return this;
    }

    public Long getReviewerId() {
        return this.reviewerId;
    }

    public void setReviewerId(Long profile) {
        this.reviewerId = profile;
    }

    public Long getRevieweeId() {
        return this.revieweeId;
    }

    public void setRevieweeId(Long profile) {
        this.revieweeId = profile;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProfileReview)) {
            return false;
        }
        return getId() != null && getId().equals(((ProfileReview) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProfileReview{" +
            "id=" + getId() +
            ", text='" + getText() + "'" +
            ", rating=" + getRating() +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            "}";
    }
}
