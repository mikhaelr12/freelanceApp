package com.freelance.app.service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.freelance.app.domain.Delivery} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DeliveryDTO implements Serializable {

    private Long id;

    @Size(max = 1024)
    private String note;

    @NotNull(message = "must not be null")
    private Instant deliveredAt;

    private OrderDTO order;

    private FileObjectDTO file;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Instant getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(Instant deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    public OrderDTO getOrder() {
        return order;
    }

    public void setOrder(OrderDTO order) {
        this.order = order;
    }

    public FileObjectDTO getFile() {
        return file;
    }

    public void setFile(FileObjectDTO file) {
        this.file = file;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DeliveryDTO)) {
            return false;
        }

        DeliveryDTO deliveryDTO = (DeliveryDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, deliveryDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DeliveryDTO{" +
            "id=" + getId() +
            ", note='" + getNote() + "'" +
            ", deliveredAt='" + getDeliveredAt() + "'" +
            ", order=" + getOrder() +
            ", file=" + getFile() +
            "}";
    }
}
