package com.freelance.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A FavoriteOffer.
 */
@Table("favorite_offer")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class FavoriteOffer implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("created_at")
    private Instant createdAt = Instant.now();

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "user", "profilePicture", "skills" }, allowSetters = true)
    private Profile profile;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "owner", "offertype", "tags" }, allowSetters = true)
    private Offer offer;

    @Column("profile_id")
    private Long profileId;

    @Column("offer_id")
    private Long offerId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public FavoriteOffer id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public FavoriteOffer createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Profile getProfile() {
        return this.profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
        this.profileId = profile != null ? profile.getId() : null;
    }

    public FavoriteOffer profile(Profile profile) {
        this.setProfile(profile);
        return this;
    }

    public Offer getOffer() {
        return this.offer;
    }

    public void setOffer(Offer offer) {
        this.offer = offer;
        this.offerId = offer != null ? offer.getId() : null;
    }

    public FavoriteOffer offer(Offer offer) {
        this.setOffer(offer);
        return this;
    }

    public Long getProfileId() {
        return this.profileId;
    }

    public void setProfileId(Long profile) {
        this.profileId = profile;
    }

    public Long getOfferId() {
        return this.offerId;
    }

    public void setOfferId(Long offer) {
        this.offerId = offer;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FavoriteOffer)) {
            return false;
        }
        return getId() != null && getId().equals(((FavoriteOffer) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FavoriteOffer{" +
            "id=" + getId() +
            ", createdAt='" + getCreatedAt() + "'" +
            "}";
    }
}
