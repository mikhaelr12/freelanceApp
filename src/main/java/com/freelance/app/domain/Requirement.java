package com.freelance.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Requirement.
 */
@Table("requirement")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Requirement implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Size(max = 512)
    @Column("prompt")
    private String prompt;

    @Size(max = 2048)
    @Column("answer")
    private String answer;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "buyer", "seller", "offerpackage" }, allowSetters = true)
    private Order order;

    @Column("order_id")
    private Long orderId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Requirement id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPrompt() {
        return this.prompt;
    }

    public Requirement prompt(String prompt) {
        this.setPrompt(prompt);
        return this;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getAnswer() {
        return this.answer;
    }

    public Requirement answer(String answer) {
        this.setAnswer(answer);
        return this;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Order getOrder() {
        return this.order;
    }

    public void setOrder(Order order) {
        this.order = order;
        this.orderId = order != null ? order.getId() : null;
    }

    public Requirement order(Order order) {
        this.setOrder(order);
        return this;
    }

    public Long getOrderId() {
        return this.orderId;
    }

    public void setOrderId(Long order) {
        this.orderId = order;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Requirement)) {
            return false;
        }
        return getId() != null && getId().equals(((Requirement) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Requirement{" +
            "id=" + getId() +
            ", prompt='" + getPrompt() + "'" +
            ", answer='" + getAnswer() + "'" +
            "}";
    }
}
