package com.freelance.app.service.mapper;

import static com.freelance.app.domain.RequirementAsserts.*;
import static com.freelance.app.domain.RequirementTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RequirementMapperTest {

    private RequirementMapper requirementMapper;

    @BeforeEach
    void setUp() {
        requirementMapper = new RequirementMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getRequirementSample1();
        var actual = requirementMapper.toEntity(requirementMapper.toDto(expected));
        assertRequirementAllPropertiesEquals(expected, actual);
    }
}
