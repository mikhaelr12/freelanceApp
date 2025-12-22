package com.freelance.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.freelance.app.domain.enumeration.OrderStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Order.
 */
@Table("jhi_order")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Order extends AbstractAuditingEntity<Long> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("status")
    private OrderStatus status;

    @NotNull(message = "must not be null")
    @Column("total_amount")
    private BigDecimal totalAmount;

    @NotNull(message = "must not be null")
    @Size(max = 3)
    @Column("currency")
    private String currency;

    @org.springframework.data.annotation.Transient
    private User buyer;

    @org.springframework.data.annotation.Transient
    private User seller;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "offer" }, allowSetters = true)
    private OfferPackage offerpackage;

    @Column("buyer_id")
    private Long buyerId;

    @Column("seller_id")
    private Long sellerId;

    @Column("offerpackage_id")
    private Long offerpackageId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Order id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OrderStatus getStatus() {
        return this.status;
    }

    public Order status(OrderStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public BigDecimal getTotalAmount() {
        return this.totalAmount;
    }

    public Order totalAmount(BigDecimal totalAmount) {
        this.setTotalAmount(totalAmount);
        return this;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount != null ? totalAmount.stripTrailingZeros() : null;
    }

    public String getCurrency() {
        return this.currency;
    }

    public Order currency(String currency) {
        this.setCurrency(currency);
        return this;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public User getBuyer() {
        return this.buyer;
    }

    public void setBuyer(User user) {
        this.buyer = user;
        this.buyerId = user != null ? user.getId() : null;
    }

    public Order buyer(User user) {
        this.setBuyer(user);
        return this;
    }

    public User getSeller() {
        return this.seller;
    }

    public void setSeller(User user) {
        this.seller = user;
        this.sellerId = user != null ? user.getId() : null;
    }

    public Order seller(User user) {
        this.setSeller(user);
        return this;
    }

    public OfferPackage getOfferpackage() {
        return this.offerpackage;
    }

    public void setOfferpackage(OfferPackage offerPackage) {
        this.offerpackage = offerPackage;
        this.offerpackageId = offerPackage != null ? offerPackage.getId() : null;
    }

    public Order offerpackage(OfferPackage offerPackage) {
        this.setOfferpackage(offerPackage);
        return this;
    }

    public Long getBuyerId() {
        return this.buyerId;
    }

    public void setBuyerId(Long user) {
        this.buyerId = user;
    }

    public Long getSellerId() {
        return this.sellerId;
    }

    public void setSellerId(Long user) {
        this.sellerId = user;
    }

    public Long getOfferpackageId() {
        return this.offerpackageId;
    }

    public void setOfferpackageId(Long offerPackage) {
        this.offerpackageId = offerPackage;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Order)) {
            return false;
        }
        return getId() != null && getId().equals(((Order) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Order{" +
            "id=" + getId() +
            ", status='" + getStatus() + "'" +
            ", totalAmount=" + getTotalAmount() +
            ", currency='" + getCurrency() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            "}";
    }
}
