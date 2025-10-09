package com.freelance.app.service.dto;

import com.freelance.app.domain.enumeration.PackageTier;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.freelance.app.domain.OfferPackage} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OfferPackageDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    @Size(max = 50)
    private String name;

    @NotNull(message = "must not be null")
    @Size(max = 200)
    private String description;

    @NotNull(message = "must not be null")
    private BigDecimal price;

    @NotNull(message = "must not be null")
    @Size(max = 3)
    private String currency;

    @NotNull(message = "must not be null")
    @Min(value = 1)
    private Integer deliveryDays;

    @NotNull(message = "must not be null")
    private PackageTier packageTier;

    @NotNull(message = "must not be null")
    private Boolean active;

    @NotNull(message = "must not be null")
    private Instant createdDate;

    private Instant lastModifiedDate;

    @Size(max = 50)
    private String createdBy;

    @Size(max = 50)
    private String lastModifiedBy;

    private OfferDTO offer;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Integer getDeliveryDays() {
        return deliveryDays;
    }

    public void setDeliveryDays(Integer deliveryDays) {
        this.deliveryDays = deliveryDays;
    }

    public PackageTier getPackageTier() {
        return packageTier;
    }

    public void setPackageTier(PackageTier packageTier) {
        this.packageTier = packageTier;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OfferPackageDTO)) {
            return false;
        }

        OfferPackageDTO offerPackageDTO = (OfferPackageDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, offerPackageDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OfferPackageDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", price=" + getPrice() +
            ", currency='" + getCurrency() + "'" +
            ", deliveryDays=" + getDeliveryDays() +
            ", packageTier='" + getPackageTier() + "'" +
            ", active='" + getActive() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", offer=" + getOffer() +
            "}";
    }
}
