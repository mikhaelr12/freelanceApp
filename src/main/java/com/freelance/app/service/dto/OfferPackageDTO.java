package com.freelance.app.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.freelance.app.domain.enumeration.PackageTier;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record OfferPackageDTO(
    @JsonProperty(access = JsonProperty.Access.READ_ONLY) @Nullable Long id,
    @NotBlank String name,
    @NotBlank String description,
    @JsonProperty(access = JsonProperty.Access.READ_ONLY) @Nullable String currency,
    @NotNull BigDecimal price,
    @NotNull Integer deliveryDays,
    @NotNull PackageTier packageTier,
    @JsonProperty(access = JsonProperty.Access.READ_ONLY) @Nullable Boolean active
) {}
