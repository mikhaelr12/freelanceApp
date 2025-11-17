package com.freelance.app.service.dto;

import com.freelance.app.domain.enumeration.VerificationRequestStatus;
import java.time.Instant;

public record VerificationRequestDTO(
    Long id,
    Long profileId,
    Long fileObjectId,
    VerificationRequestStatus status,
    String message,
    String createdBy,
    Instant createdDate,
    Instant lastModifiedDate,
    String lastModifiedBy
) {}
