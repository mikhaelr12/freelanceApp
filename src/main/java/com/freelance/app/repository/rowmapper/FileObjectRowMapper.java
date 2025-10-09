package com.freelance.app.repository.rowmapper;

import com.freelance.app.domain.FileObject;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link FileObject}, with proper type conversions.
 */
@Service
public class FileObjectRowMapper implements BiFunction<Row, String, FileObject> {

    private final ColumnConverter converter;

    public FileObjectRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link FileObject} stored in the database.
     */
    @Override
    public FileObject apply(Row row, String prefix) {
        FileObject entity = new FileObject();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setBucket(converter.fromRow(row, prefix + "_bucket", String.class));
        entity.setObjectKey(converter.fromRow(row, prefix + "_object_key", String.class));
        entity.setContentType(converter.fromRow(row, prefix + "_content_type", String.class));
        entity.setFileSize(converter.fromRow(row, prefix + "_file_size", Long.class));
        entity.setChecksum(converter.fromRow(row, prefix + "_checksum", String.class));
        entity.setDurationSeconds(converter.fromRow(row, prefix + "_duration_seconds", Integer.class));
        entity.setCreatedDate(converter.fromRow(row, prefix + "_created_date", Instant.class));
        entity.setLastModifiedDate(converter.fromRow(row, prefix + "_last_modified_date", Instant.class));
        entity.setCreatedBy(converter.fromRow(row, prefix + "_created_by", String.class));
        entity.setLastModifiedBy(converter.fromRow(row, prefix + "_last_modified_by", String.class));
        return entity;
    }
}
