package com.freelance.app.service.mapper;

import static com.freelance.app.domain.OfferTypeAsserts.*;
import static com.freelance.app.domain.OfferTypeTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OfferTypeMapperTest {

    private OfferTypeMapper offerTypeMapper;

    @BeforeEach
    void setUp() {
        offerTypeMapper = new OfferTypeMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getOfferTypeSample1();
        var actual = offerTypeMapper.toEntity(offerTypeMapper.toDto(expected));
        assertOfferTypeAllPropertiesEquals(expected, actual);
    }
}
