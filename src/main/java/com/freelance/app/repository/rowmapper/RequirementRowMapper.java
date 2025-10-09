package com.freelance.app.repository.rowmapper;

import com.freelance.app.domain.Requirement;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Requirement}, with proper type conversions.
 */
@Service
public class RequirementRowMapper implements BiFunction<Row, String, Requirement> {

    private final ColumnConverter converter;

    public RequirementRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Requirement} stored in the database.
     */
    @Override
    public Requirement apply(Row row, String prefix) {
        Requirement entity = new Requirement();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setPrompt(converter.fromRow(row, prefix + "_prompt", String.class));
        entity.setAnswer(converter.fromRow(row, prefix + "_answer", String.class));
        entity.setOrderId(converter.fromRow(row, prefix + "_order_id", Long.class));
        return entity;
    }
}
