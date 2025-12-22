package com.freelance.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.freelance.app.domain.enumeration.OfferStatus;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Offer.
 */
@Table("offer")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Offer extends AbstractAuditingEntity<Long> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Size(max = 255)
    @Column("name")
    private String name;

    @NotNull(message = "must not be null")
    @Size(max = 2048)
    @Column("description")
    private String description;

    @DecimalMin(value = "0")
    @DecimalMax(value = "5")
    @Column("rating")
    private Double rating;

    @NotNull(message = "must not be null")
    @Column("status")
    private OfferStatus status;

    @NotNull(message = "must not be null")
    @Column("visibility")
    private Boolean visibility;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "user", "profilePicture", "skills" }, allowSetters = true)
    private Profile owner;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "subcategory" }, allowSetters = true)
    private OfferType offertype;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "offers" }, allowSetters = true)
    private Set<Tag> tags = new HashSet<>();

    @Column("owner_id")
    private Long ownerId;

    @Column("offertype_id")
    private Long offertypeId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Offer id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Offer name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public Offer description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getRating() {
        return this.rating;
    }

    public Offer rating(Double rating) {
        this.setRating(rating);
        return this;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public OfferStatus getStatus() {
        return this.status;
    }

    public Offer status(OfferStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(OfferStatus status) {
        this.status = status;
    }

    public Boolean getVisibility() {
        return this.visibility;
    }

    public Offer visibility(Boolean visibility) {
        this.setVisibility(visibility);
        return this;
    }

    public void setVisibility(Boolean visibility) {
        this.visibility = visibility;
    }

    public Offer createdDate(Instant createdDate) {
        this.setCreatedDate(createdDate);
        return this;
    }

    public Offer lastModifiedDate(Instant lastModifiedDate) {
        this.setLastModifiedDate(lastModifiedDate);
        return this;
    }

    public Offer createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    public Offer lastModifiedBy(String lastModifiedBy) {
        this.setLastModifiedBy(lastModifiedBy);
        return this;
    }

    public Profile getOwner() {
        return this.owner;
    }

    public void setOwner(Profile profile) {
        this.owner = profile;
        this.ownerId = profile != null ? profile.getId() : null;
    }

    public Offer owner(Profile profile) {
        this.setOwner(profile);
        return this;
    }

    public OfferType getOffertype() {
        return this.offertype;
    }

    public void setOffertype(OfferType offerType) {
        this.offertype = offerType;
        this.offertypeId = offerType != null ? offerType.getId() : null;
    }

    public Offer offertype(OfferType offerType) {
        this.setOffertype(offerType);
        return this;
    }

    public Set<Tag> getTags() {
        return this.tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    public Offer tags(Set<Tag> tags) {
        this.setTags(tags);
        return this;
    }

    public void addTag(Tag tag) {
        this.tags.add(tag);
    }

    public void removeTag(Tag tag) {
        this.tags.remove(tag);
    }

    public Long getOwnerId() {
        return this.ownerId;
    }

    public void setOwnerId(Long profile) {
        this.ownerId = profile;
    }

    public Long getOffertypeId() {
        return this.offertypeId;
    }

    public void setOffertypeId(Long offerType) {
        this.offertypeId = offerType;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Offer)) {
            return false;
        }
        return getId() != null && getId().equals(((Offer) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Offer{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", rating=" + getRating() +
            ", status='" + getStatus() + "'" +
            ", visibility='" + getVisibility() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            "}";
    }
}
