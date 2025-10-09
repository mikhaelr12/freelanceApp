package com.freelance.app.service.mapper;

import static com.freelance.app.domain.DisputeAsserts.*;
import static com.freelance.app.domain.DisputeTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DisputeMapperTest {

    private DisputeMapper disputeMapper;

    @BeforeEach
    void setUp() {
        disputeMapper = new DisputeMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getDisputeSample1();
        var actual = disputeMapper.toEntity(disputeMapper.toDto(expected));
        assertDisputeAllPropertiesEquals(expected, actual);
    }
}
