package com.freelance.app.service.dto;

import java.util.Set;
import org.springframework.lang.Nullable;

public record OfferUpdateDTO(@Nullable String name, @Nullable String description, @Nullable Long offerTypeId, @Nullable Set<Long> tagIds) {}
