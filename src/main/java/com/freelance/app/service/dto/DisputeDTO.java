package com.freelance.app.service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.freelance.app.domain.Dispute} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DisputeDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    @Size(max = 512)
    private String reason;

    @NotNull(message = "must not be null")
    private Instant openedAt;

    private Instant closedAt;

    private OrderDTO order;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Instant getOpenedAt() {
        return openedAt;
    }

    public void setOpenedAt(Instant openedAt) {
        this.openedAt = openedAt;
    }

    public Instant getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(Instant closedAt) {
        this.closedAt = closedAt;
    }

    public OrderDTO getOrder() {
        return order;
    }

    public void setOrder(OrderDTO order) {
        this.order = order;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DisputeDTO)) {
            return false;
        }

        DisputeDTO disputeDTO = (DisputeDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, disputeDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DisputeDTO{" +
            "id=" + getId() +
            ", reason='" + getReason() + "'" +
            ", openedAt='" + getOpenedAt() + "'" +
            ", closedAt='" + getClosedAt() + "'" +
            ", order=" + getOrder() +
            "}";
    }
}
