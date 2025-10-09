package com.freelance.app.repository.rowmapper;

import com.freelance.app.domain.OfferMedia;
import com.freelance.app.domain.enumeration.MediaKind;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link OfferMedia}, with proper type conversions.
 */
@Service
public class OfferMediaRowMapper implements BiFunction<Row, String, OfferMedia> {

    private final ColumnConverter converter;

    public OfferMediaRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link OfferMedia} stored in the database.
     */
    @Override
    public OfferMedia apply(Row row, String prefix) {
        OfferMedia entity = new OfferMedia();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setMediaKind(converter.fromRow(row, prefix + "_media_kind", MediaKind.class));
        entity.setIsPrimary(converter.fromRow(row, prefix + "_is_primary", Boolean.class));
        entity.setCaption(converter.fromRow(row, prefix + "_caption", String.class));
        entity.setCreatedDate(converter.fromRow(row, prefix + "_created_date", Instant.class));
        entity.setLastModifiedDate(converter.fromRow(row, prefix + "_last_modified_date", Instant.class));
        entity.setCreatedBy(converter.fromRow(row, prefix + "_created_by", String.class));
        entity.setLastModifiedBy(converter.fromRow(row, prefix + "_last_modified_by", String.class));
        entity.setOfferId(converter.fromRow(row, prefix + "_offer_id", Long.class));
        entity.setFileId(converter.fromRow(row, prefix + "_file_id", Long.class));
        return entity;
    }
}
