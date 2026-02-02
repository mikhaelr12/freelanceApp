package com.freelance.app.service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

/**
 * A DTO for the {@link com.freelance.app.domain.Tag} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public record TagDTO(
    Long id,

    @NotNull(message = "must not be null") @Size(max = 64) String name
)
    implements Serializable {}
