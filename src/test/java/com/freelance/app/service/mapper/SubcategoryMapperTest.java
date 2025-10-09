package com.freelance.app.service.mapper;

import static com.freelance.app.domain.SubcategoryAsserts.*;
import static com.freelance.app.domain.SubcategoryTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SubcategoryMapperTest {

    private SubcategoryMapper subcategoryMapper;

    @BeforeEach
    void setUp() {
        subcategoryMapper = new SubcategoryMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getSubcategorySample1();
        var actual = subcategoryMapper.toEntity(subcategoryMapper.toDto(expected));
        assertSubcategoryAllPropertiesEquals(expected, actual);
    }
}
