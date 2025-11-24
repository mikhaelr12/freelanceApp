package com.freelance.app.service.dto;

import com.freelance.app.domain.enumeration.OfferStatus;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link com.freelance.app.domain.Offer} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OfferShortDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    @Size(max = 255)
    private String name;

    @DecimalMin(value = "0")
    @DecimalMax(value = "5")
    private Double rating;

    private ProfileDTO owner;

    private Set<TagDTO> tags;

    private Set<String> offerImages;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public ProfileDTO getOwner() {
        return owner;
    }

    public void setOwner(ProfileDTO owner) {
        this.owner = owner;
    }

    public Set<TagDTO> getTags() {
        return tags;
    }

    public void setTags(Set<TagDTO> tags) {
        this.tags = tags;
    }

    public Set<String> getOfferImages() {
        return offerImages;
    }

    public void setOfferImages(Set<String> offerImages) {
        this.offerImages = offerImages;
    }

    public OfferShortDTO id(Long id) {
        this.setId(id);
        return this;
    }

    public OfferShortDTO name(String name) {
        this.setName(name);
        return this;
    }

    public OfferShortDTO rating(Double rating) {
        this.setRating(rating);
        return this;
    }

    public OfferShortDTO owner(ProfileDTO owner) {
        this.setOwner(owner);
        return this;
    }

    public OfferShortDTO tags(Set<TagDTO> tags) {
        this.setTags(tags);
        return this;
    }

    public OfferShortDTO offerImages(Set<String> offerImages) {
        this.setOfferImages(offerImages);
        return this;
    }
}
