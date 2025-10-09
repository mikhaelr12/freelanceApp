package com.freelance.app.service.mapper;

import static com.freelance.app.domain.MessageAsserts.*;
import static com.freelance.app.domain.MessageTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MessageMapperTest {

    private MessageMapper messageMapper;

    @BeforeEach
    void setUp() {
        messageMapper = new MessageMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getMessageSample1();
        var actual = messageMapper.toEntity(messageMapper.toDto(expected));
        assertMessageAllPropertiesEquals(expected, actual);
    }
}
