package com.freelance.app.service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.freelance.app.domain.Requirement} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RequirementDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    @Size(max = 512)
    private String prompt;

    @Size(max = 2048)
    private String answer;

    private OrderDTO order;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
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
        if (!(o instanceof RequirementDTO)) {
            return false;
        }

        RequirementDTO requirementDTO = (RequirementDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, requirementDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RequirementDTO{" +
            "id=" + getId() +
            ", prompt='" + getPrompt() + "'" +
            ", answer='" + getAnswer() + "'" +
            ", order=" + getOrder() +
            "}";
    }
}
