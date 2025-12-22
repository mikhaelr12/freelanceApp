package com.freelance.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.freelance.app.domain.enumeration.PackageTier;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A OfferPackage.
 */
@Table("offer_package")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OfferPackage extends AbstractAuditingEntity<Long> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Size(max = 50)
    @Column("name")
    private String name;

    @NotNull(message = "must not be null")
    @Size(max = 200)
    @Column("description")
    private String description;

    @NotNull(message = "must not be null")
    @Column("price")
    private BigDecimal price;

    @NotNull(message = "must not be null")
    @Size(max = 3)
    @Column("currency")
    private String currency;

    @NotNull(message = "must not be null")
    @Min(value = 1)
    @Column("delivery_days")
    private Integer deliveryDays;

    @NotNull(message = "must not be null")
    @Column("package_tier")
    private PackageTier packageTier;

    @NotNull(message = "must not be null")
    @Column("active")
    private Boolean active;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "owner", "offertype", "tags" }, allowSetters = true)
    private Offer offer;

    @Column("offer_id")
    private Long offerId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public OfferPackage id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public OfferPackage name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public OfferPackage description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public OfferPackage price(BigDecimal price) {
        this.setPrice(price);
        return this;
    }

    public void setPrice(BigDecimal price) {
        this.price = price != null ? price.stripTrailingZeros() : null;
    }

    public String getCurrency() {
        return this.currency;
    }

    public OfferPackage currency(String currency) {
        this.setCurrency(currency);
        return this;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Integer getDeliveryDays() {
        return this.deliveryDays;
    }

    public OfferPackage deliveryDays(Integer deliveryDays) {
        this.setDeliveryDays(deliveryDays);
        return this;
    }

    public void setDeliveryDays(Integer deliveryDays) {
        this.deliveryDays = deliveryDays;
    }

    public PackageTier getPackageTier() {
        return this.packageTier;
    }

    public OfferPackage packageTier(PackageTier packageTier) {
        this.setPackageTier(packageTier);
        return this;
    }

    public void setPackageTier(PackageTier packageTier) {
        this.packageTier = packageTier;
    }

    public Boolean getActive() {
        return this.active;
    }

    public OfferPackage active(Boolean active) {
        this.setActive(active);
        return this;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Offer getOffer() {
        return this.offer;
    }

    public void setOffer(Offer offer) {
        this.offer = offer;
        this.offerId = offer != null ? offer.getId() : null;
    }

    public OfferPackage offer(Offer offer) {
        this.setOffer(offer);
        return this;
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
        if (!(o instanceof OfferPackage)) {
            return false;
        }
        return getId() != null && getId().equals(((OfferPackage) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OfferPackage{" +
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
            "}";
    }
}
