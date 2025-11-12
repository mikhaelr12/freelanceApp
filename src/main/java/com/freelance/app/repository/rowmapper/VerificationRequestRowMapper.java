package com.freelance.app.repository.rowmapper;

import com.freelance.app.domain.VerificationRequest;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link VerificationRequest}, with proper type conversions.
 */
@Service
public class VerificationRequestRowMapper implements BiFunction<Row, String, VerificationRequest> {

    private final ColumnConverter converter;

    public VerificationRequestRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link VerificationRequest} stored in the database.
     */
    @Override
    public VerificationRequest apply(Row row, String prefix) {
        VerificationRequest entity = new VerificationRequest();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setProfileId(converter.fromRow(row, prefix + "_profile_id", Long.class));
        entity.setFileObjectId(converter.fromRow(row, prefix + "_file_object_id", Long.class));
        return entity;
    }
}
