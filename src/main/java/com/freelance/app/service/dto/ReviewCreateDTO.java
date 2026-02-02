package com.freelance.app.service.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import org.springframework.lang.Nullable;

public record ReviewCreateDTO(
    @Nullable String text,

    @DecimalMin(value = "1.0", message = "Rating minimum value has to be 1.0")
    @DecimalMax(value = "5.0", message = "Rating maximum value has to be 5.0")
    Double rating
) {}
