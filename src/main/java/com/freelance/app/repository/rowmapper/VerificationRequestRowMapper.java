package com.freelance.app.repository.rowmapper;

import com.freelance.app.domain.VerificationRequest;
import com.freelance.app.domain.enumeration.VerificationRequestStatus;
import com.freelance.app.service.dto.VerificationRequestDTO;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.time.LocalDateTime;
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
        entity.setCreatedBy(converter.fromRow(row, prefix + "_created_by", String.class));
        entity.setCreatedDate(converter.fromRow(row, prefix + "_created_date", Instant.class));
        entity.setLastModifiedBy(converter.fromRow(row, prefix + "_last_modified_by", String.class));
        entity.setLastModifiedDate(converter.fromRow(row, prefix + "_last_modified_date", Instant.class));
        entity.setMessage(converter.fromRow(row, prefix + "_message", String.class));
        entity.setStatus(converter.fromRow(row, prefix + "_status", VerificationRequestStatus.class));
        return entity;
    }

    public VerificationRequestDTO applyDTO(Row row, String prefix) {
        return new VerificationRequestDTO(
            converter.fromRow(row, prefix + "_id", Long.class),
            converter.fromRow(row, prefix + "_profile_id", Long.class),
            converter.fromRow(row, prefix + "_file_object_id", Long.class),
            converter.fromRow(row, prefix + "_status", VerificationRequestStatus.class),
            converter.fromRow(row, prefix + "_message", String.class),
            converter.fromRow(row, prefix + "_created_by", String.class),
            converter.fromRow(row, prefix + "_created_date", Instant.class),
            converter.fromRow(row, prefix + "_last_modified_date", Instant.class),
            converter.fromRow(row, prefix + "_last_modified_by", String.class)
        );
    }
}
