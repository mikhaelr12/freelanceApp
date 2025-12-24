package com.freelance.app.service.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

public class OfferReviewCreateDTO {

    private String text;

    @DecimalMin(value = "1.0", message = "Rating minimum value has to be 1.0")
    @DecimalMax(value = "5.0", message = "Rating maximum value has to be 5.0")
    private Double rating;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }
}
