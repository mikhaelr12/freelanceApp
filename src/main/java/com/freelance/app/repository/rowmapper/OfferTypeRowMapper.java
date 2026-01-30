package com.freelance.app.repository.rowmapper;

import com.freelance.app.domain.OfferType;
import com.freelance.app.service.dto.OfferTypeShortDTO;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link OfferType}, with proper type conversions.
 */
@Service
public class OfferTypeRowMapper implements BiFunction<Row, String, OfferType> {

    private final ColumnConverter converter;

    public OfferTypeRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link OfferType} stored in the database.
     */
    @Override
    public OfferType apply(Row row, String prefix) {
        OfferType entity = new OfferType();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setCreatedDate(converter.fromRow(row, prefix + "_created_date", Instant.class));
        entity.setLastModifiedDate(converter.fromRow(row, prefix + "_last_modified_date", Instant.class));
        entity.setCreatedBy(converter.fromRow(row, prefix + "_created_by", String.class));
        entity.setLastModifiedBy(converter.fromRow(row, prefix + "_last_modified_by", String.class));
        entity.setActive(converter.fromRow(row, prefix + "_active", Boolean.class));
        entity.setSubcategoryId(converter.fromRow(row, prefix + "_subcategory_id", Long.class));
        return entity;
    }

    public OfferTypeShortDTO applyShort(Row row, String prefix) {
        return new OfferTypeShortDTO(
            converter.fromRow(row, prefix + "_id", Long.class),
            converter.fromRow(row, prefix + "_name", String.class)
        );
    }
}
