package com.freelance.app.service.dto;

import org.springframework.data.relational.core.mapping.Column;

public record OfferReviewShortDTO(
    Long id,
    String text,
    Double rating,
    @Column("profile_id") Long profileId,
    @Column("profile_full_name") String profileFullName
) {}
