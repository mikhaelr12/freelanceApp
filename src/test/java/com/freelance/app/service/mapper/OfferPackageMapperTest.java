package com.freelance.app.service.mapper;

import static com.freelance.app.domain.OfferPackageAsserts.*;
import static com.freelance.app.domain.OfferPackageTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OfferPackageMapperTest {

    private OfferPackageMapper offerPackageMapper;

    @BeforeEach
    void setUp() {
        offerPackageMapper = new OfferPackageMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getOfferPackageSample1();
        var actual = offerPackageMapper.toEntity(offerPackageMapper.toDto(expected));
        assertOfferPackageAllPropertiesEquals(expected, actual);
    }
}
