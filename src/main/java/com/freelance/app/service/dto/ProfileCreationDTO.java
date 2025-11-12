package com.freelance.app.service.dto;

import com.freelance.app.domain.enumeration.ProfileType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Set;

public record ProfileCreationDTO(
    @NotBlank String firstName,
    @NotBlank String lastName,
    @NotBlank String description,
    @NotNull ProfileType profileType,

    Set<Long> skillIds
) {}
