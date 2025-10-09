package com.freelance.app.service.mapper;

import static com.freelance.app.domain.OfferMediaAsserts.*;
import static com.freelance.app.domain.OfferMediaTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OfferMediaMapperTest {

    private OfferMediaMapper offerMediaMapper;

    @BeforeEach
    void setUp() {
        offerMediaMapper = new OfferMediaMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getOfferMediaSample1();
        var actual = offerMediaMapper.toEntity(offerMediaMapper.toDto(expected));
        assertOfferMediaAllPropertiesEquals(expected, actual);
    }
}
