package com.freelance.app.service.mapper;

import static com.freelance.app.domain.FileObjectAsserts.*;
import static com.freelance.app.domain.FileObjectTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FileObjectMapperTest {

    private FileObjectMapper fileObjectMapper;

    @BeforeEach
    void setUp() {
        fileObjectMapper = new FileObjectMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getFileObjectSample1();
        var actual = fileObjectMapper.toEntity(fileObjectMapper.toDto(expected));
        assertFileObjectAllPropertiesEquals(expected, actual);
    }
}
