package com.freelance.app.repository.rowmapper;

import com.freelance.app.domain.Offer;
import com.freelance.app.domain.enumeration.OfferStatus;
import com.freelance.app.service.dto.OfferShortDTO;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Offer}, with proper type conversions.
 */
@Service
public class OfferRowMapper implements BiFunction<Row, String, Offer> {

    private final ColumnConverter converter;

    public OfferRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Offer} stored in the database.
     */
    @Override
    public Offer apply(Row row, String prefix) {
        Offer entity = new Offer();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setRating(converter.fromRow(row, prefix + "_rating", Double.class));
        entity.setStatus(converter.fromRow(row, prefix + "_status", OfferStatus.class));
        entity.setVisibility(converter.fromRow(row, prefix + "_visibility", Boolean.class));
        entity.setCreatedDate(converter.fromRow(row, prefix + "_created_date", Instant.class));
        entity.setLastModifiedDate(converter.fromRow(row, prefix + "_last_modified_date", Instant.class));
        entity.setCreatedBy(converter.fromRow(row, prefix + "_created_by", String.class));
        entity.setLastModifiedBy(converter.fromRow(row, prefix + "_last_modified_by", String.class));
        entity.setOwnerId(converter.fromRow(row, prefix + "_owner_id", Long.class));
        entity.setOffertypeId(converter.fromRow(row, prefix + "_offertype_id", Long.class));
        return entity;
    }

    public OfferShortDTO applyShort(Row row, String prefix) {
        OfferShortDTO entity = new OfferShortDTO();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setRating(converter.fromRow(row, prefix + "_rating", Double.class));

        return entity;
    }
}
