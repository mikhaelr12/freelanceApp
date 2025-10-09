package com.freelance.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Delivery.
 */
@Table("delivery")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Delivery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Size(max = 1024)
    @Column("note")
    private String note;

    @NotNull(message = "must not be null")
    @Column("delivered_at")
    private Instant deliveredAt;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "buyer", "seller", "offerpackage" }, allowSetters = true)
    private Order order;

    @org.springframework.data.annotation.Transient
    private FileObject file;

    @Column("order_id")
    private Long orderId;

    @Column("file_id")
    private Long fileId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Delivery id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNote() {
        return this.note;
    }

    public Delivery note(String note) {
        this.setNote(note);
        return this;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Instant getDeliveredAt() {
        return this.deliveredAt;
    }

    public Delivery deliveredAt(Instant deliveredAt) {
        this.setDeliveredAt(deliveredAt);
        return this;
    }

    public void setDeliveredAt(Instant deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    public Order getOrder() {
        return this.order;
    }

    public void setOrder(Order order) {
        this.order = order;
        this.orderId = order != null ? order.getId() : null;
    }

    public Delivery order(Order order) {
        this.setOrder(order);
        return this;
    }

    public FileObject getFile() {
        return this.file;
    }

    public void setFile(FileObject fileObject) {
        this.file = fileObject;
        this.fileId = fileObject != null ? fileObject.getId() : null;
    }

    public Delivery file(FileObject fileObject) {
        this.setFile(fileObject);
        return this;
    }

    public Long getOrderId() {
        return this.orderId;
    }

    public void setOrderId(Long order) {
        this.orderId = order;
    }

    public Long getFileId() {
        return this.fileId;
    }

    public void setFileId(Long fileObject) {
        this.fileId = fileObject;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Delivery)) {
            return false;
        }
        return getId() != null && getId().equals(((Delivery) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Delivery{" +
            "id=" + getId() +
            ", note='" + getNote() + "'" +
            ", deliveredAt='" + getDeliveredAt() + "'" +
            "}";
    }
}
