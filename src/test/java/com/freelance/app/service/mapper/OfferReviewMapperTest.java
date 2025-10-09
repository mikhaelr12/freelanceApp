package com.freelance.app.service.mapper;

import static com.freelance.app.domain.OfferReviewAsserts.*;
import static com.freelance.app.domain.OfferReviewTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OfferReviewMapperTest {

    private OfferReviewMapper offerReviewMapper;

    @BeforeEach
    void setUp() {
        offerReviewMapper = new OfferReviewMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getOfferReviewSample1();
        var actual = offerReviewMapper.toEntity(offerReviewMapper.toDto(expected));
        assertOfferReviewAllPropertiesEquals(expected, actual);
    }
}
