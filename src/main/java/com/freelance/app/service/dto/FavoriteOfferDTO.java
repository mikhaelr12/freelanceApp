package com.freelance.app.service.dto;

import java.time.Instant;

public record FavoriteOfferDTO(Long id, Long profileId, Long offerId, String profileFullName, String offerName, Instant createdAt) {}
