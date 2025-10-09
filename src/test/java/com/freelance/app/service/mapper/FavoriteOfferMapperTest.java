package com.freelance.app.service.mapper;

import static com.freelance.app.domain.FavoriteOfferAsserts.*;
import static com.freelance.app.domain.FavoriteOfferTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FavoriteOfferMapperTest {

    private FavoriteOfferMapper favoriteOfferMapper;

    @BeforeEach
    void setUp() {
        favoriteOfferMapper = new FavoriteOfferMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getFavoriteOfferSample1();
        var actual = favoriteOfferMapper.toEntity(favoriteOfferMapper.toDto(expected));
        assertFavoriteOfferAllPropertiesEquals(expected, actual);
    }
}
