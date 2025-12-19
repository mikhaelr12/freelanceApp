package com.freelance.app.domain.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.freelance.app.domain.Requirement} entity. This class is used
 * in {@link com.freelance.app.web.rest.RequirementResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /requirements?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RequirementCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter prompt;

    private StringFilter answer;

    private LongFilter orderId;

    private Boolean distinct;

    public RequirementCriteria() {}

    public RequirementCriteria(RequirementCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.prompt = other.optionalPrompt().map(StringFilter::copy).orElse(null);
        this.answer = other.optionalAnswer().map(StringFilter::copy).orElse(null);
        this.orderId = other.optionalOrderId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public RequirementCriteria copy() {
        return new RequirementCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getPrompt() {
        return prompt;
    }

    public Optional<StringFilter> optionalPrompt() {
        return Optional.ofNullable(prompt);
    }

    public StringFilter prompt() {
        if (prompt == null) {
            setPrompt(new StringFilter());
        }
        return prompt;
    }

    public void setPrompt(StringFilter prompt) {
        this.prompt = prompt;
    }

    public StringFilter getAnswer() {
        return answer;
    }

    public Optional<StringFilter> optionalAnswer() {
        return Optional.ofNullable(answer);
    }

    public StringFilter answer() {
        if (answer == null) {
            setAnswer(new StringFilter());
        }
        return answer;
    }

    public void setAnswer(StringFilter answer) {
        this.answer = answer;
    }

    public LongFilter getOrderId() {
        return orderId;
    }

    public Optional<LongFilter> optionalOrderId() {
        return Optional.ofNullable(orderId);
    }

    public LongFilter orderId() {
        if (orderId == null) {
            setOrderId(new LongFilter());
        }
        return orderId;
    }

    public void setOrderId(LongFilter orderId) {
        this.orderId = orderId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final RequirementCriteria that = (RequirementCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(prompt, that.prompt) &&
            Objects.equals(answer, that.answer) &&
            Objects.equals(orderId, that.orderId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, prompt, answer, orderId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RequirementCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalPrompt().map(f -> "prompt=" + f + ", ").orElse("") +
            optionalAnswer().map(f -> "answer=" + f + ", ").orElse("") +
            optionalOrderId().map(f -> "orderId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
