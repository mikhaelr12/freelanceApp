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
 * A OfferReview.
 */
@Table("offer_review")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OfferReview extends AbstractAuditingEntity<Long> implements Serializable {

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
    @JsonIgnoreProperties(value = { "owner", "offertype", "tags" }, allowSetters = true)
    private Offer offer;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "user", "profilePicture", "skills" }, allowSetters = true)
    private Profile reviewer;

    @Column("offer_id")
    private Long offerId;

    @Column("reviewer_id")
    private Long reviewerId;

    @Column("checked")
    private Boolean checked;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public OfferReview id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return this.text;
    }

    public OfferReview text(String text) {
        this.setText(text);
        return this;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Double getRating() {
        return this.rating;
    }

    public OfferReview rating(Double rating) {
        this.setRating(rating);
        return this;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Offer getOffer() {
        return this.offer;
    }

    public void setOffer(Offer offer) {
        this.offer = offer;
        this.offerId = offer != null ? offer.getId() : null;
    }

    public OfferReview offer(Offer offer) {
        this.setOffer(offer);
        return this;
    }

    public Profile getReviewer() {
        return this.reviewer;
    }

    public void setReviewer(Profile profile) {
        this.reviewer = profile;
        this.reviewerId = profile != null ? profile.getId() : null;
    }

    public OfferReview reviewer(Profile profile) {
        this.setReviewer(profile);
        return this;
    }

    public Long getOfferId() {
        return this.offerId;
    }

    public void setOfferId(Long offer) {
        this.offerId = offer;
    }

    public Long getReviewerId() {
        return this.reviewerId;
    }

    public void setReviewerId(Long profile) {
        this.reviewerId = profile;
    }

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    public OfferReview createdBy(String login) {
        this.setCreatedBy(login);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OfferReview)) {
            return false;
        }
        return getId() != null && getId().equals(((OfferReview) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OfferReview{" +
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
