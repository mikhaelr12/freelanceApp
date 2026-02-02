package com.freelance.app.service.dto;

import jakarta.validation.constraints.NotNull;
import java.util.Set;

public record OfferDTO(@NotNull String name, @NotNull String description, @NotNull Long offerTypeId, Set<Long> tagIds) {}
