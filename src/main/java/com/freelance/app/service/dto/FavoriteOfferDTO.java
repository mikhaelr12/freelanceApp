package com.freelance.app.service.dto;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.freelance.app.domain.FavoriteOffer} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class FavoriteOfferDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    private Instant createdAt;

    private ProfileDTO profile;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public ProfileDTO getProfile() {
        return profile;
    }

    public void setProfile(ProfileDTO profile) {
        this.profile = profile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FavoriteOfferDTO)) {
            return false;
        }

        FavoriteOfferDTO favoriteOfferDTO = (FavoriteOfferDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, favoriteOfferDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FavoriteOfferDTO{" +
            "id=" + getId() +
            ", createdAt='" + getCreatedAt() + "'" +
            ", profile=" + getProfile() +
            "}";
    }
}
