package com.freelance.app.repository.rowmapper;

import com.freelance.app.domain.Dispute;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Dispute}, with proper type conversions.
 */
@Service
public class DisputeRowMapper implements BiFunction<Row, String, Dispute> {

    private final ColumnConverter converter;

    public DisputeRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Dispute} stored in the database.
     */
    @Override
    public Dispute apply(Row row, String prefix) {
        Dispute entity = new Dispute();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setReason(converter.fromRow(row, prefix + "_reason", String.class));
        entity.setOpenedAt(converter.fromRow(row, prefix + "_opened_at", Instant.class));
        entity.setClosedAt(converter.fromRow(row, prefix + "_closed_at", Instant.class));
        entity.setOrderId(converter.fromRow(row, prefix + "_order_id", Long.class));
        return entity;
    }
}
