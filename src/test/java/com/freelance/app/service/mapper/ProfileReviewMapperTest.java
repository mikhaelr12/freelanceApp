package com.freelance.app.service.mapper;

import static com.freelance.app.domain.ProfileReviewAsserts.*;
import static com.freelance.app.domain.ProfileReviewTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProfileReviewMapperTest {

    private ProfileReviewMapper profileReviewMapper;

    @BeforeEach
    void setUp() {
        profileReviewMapper = new ProfileReviewMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getProfileReviewSample1();
        var actual = profileReviewMapper.toEntity(profileReviewMapper.toDto(expected));
        assertProfileReviewAllPropertiesEquals(expected, actual);
    }
}
