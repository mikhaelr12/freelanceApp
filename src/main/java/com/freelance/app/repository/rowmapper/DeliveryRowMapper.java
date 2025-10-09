package com.freelance.app.repository.rowmapper;

import com.freelance.app.domain.Delivery;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Delivery}, with proper type conversions.
 */
@Service
public class DeliveryRowMapper implements BiFunction<Row, String, Delivery> {

    private final ColumnConverter converter;

    public DeliveryRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Delivery} stored in the database.
     */
    @Override
    public Delivery apply(Row row, String prefix) {
        Delivery entity = new Delivery();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setNote(converter.fromRow(row, prefix + "_note", String.class));
        entity.setDeliveredAt(converter.fromRow(row, prefix + "_delivered_at", Instant.class));
        entity.setOrderId(converter.fromRow(row, prefix + "_order_id", Long.class));
        entity.setFileId(converter.fromRow(row, prefix + "_file_id", Long.class));
        return entity;
    }
}
