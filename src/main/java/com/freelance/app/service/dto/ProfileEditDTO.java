package com.freelance.app.service.dto;

import io.micrometer.common.lang.Nullable;
import java.util.Set;

public record ProfileEditDTO(
    @Nullable String firstName,
    @Nullable String lastName,
    @Nullable String description,
    @Nullable Set<Long> skills
) {}
