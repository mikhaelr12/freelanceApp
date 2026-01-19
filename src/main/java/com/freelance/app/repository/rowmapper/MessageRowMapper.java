package com.freelance.app.repository.rowmapper;

import com.freelance.app.domain.Message;
import com.freelance.app.service.dto.MessageShortDTO;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Message}, with proper type conversions.
 */
@Service
public class MessageRowMapper implements BiFunction<Row, String, Message> {

    private final ColumnConverter converter;

    public MessageRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Message} stored in the database.
     */
    @Override
    public Message apply(Row row, String prefix) {
        Message entity = new Message();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setBody(converter.fromRow(row, prefix + "_body", String.class));
        entity.setSentAt(converter.fromRow(row, prefix + "_sent_at", Instant.class));
        entity.setConversationId(converter.fromRow(row, prefix + "_conversation_id", Long.class));
        entity.setSenderId(converter.fromRow(row, prefix + "_sender_id", Long.class));
        entity.setReceiverId(converter.fromRow(row, prefix + "_receiver_id", Long.class));
        return entity;
    }

    public MessageShortDTO applyShortDTO(Row row, String prefix) {
        return new MessageShortDTO(
            converter.fromRow(row, prefix + "_id", Long.class),
            converter.fromRow(row, prefix + "_body", String.class),
            converter.fromRow(row, prefix + "_sender_id", Long.class),
            converter.fromRow(row, prefix + "_receiver_id", Long.class)
        );
    }
}
