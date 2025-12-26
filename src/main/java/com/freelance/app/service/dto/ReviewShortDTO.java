package com.freelance.app.service.dto;

import java.time.Instant;
import org.springframework.data.relational.core.mapping.Column;

public record ReviewShortDTO(
    Long id,
    String text,
    Double rating,
    Instant createdDate,
    @Column("profile_id") Long profileId,
    @Column("profile_full_name") String profileFullName
) {}
