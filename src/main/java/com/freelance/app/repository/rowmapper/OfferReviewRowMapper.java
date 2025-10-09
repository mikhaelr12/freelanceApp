package com.freelance.app.repository.rowmapper;

import com.freelance.app.domain.OfferReview;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link OfferReview}, with proper type conversions.
 */
@Service
public class OfferReviewRowMapper implements BiFunction<Row, String, OfferReview> {

    private final ColumnConverter converter;

    public OfferReviewRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link OfferReview} stored in the database.
     */
    @Override
    public OfferReview apply(Row row, String prefix) {
        OfferReview entity = new OfferReview();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setText(converter.fromRow(row, prefix + "_text", String.class));
        entity.setRating(converter.fromRow(row, prefix + "_rating", Double.class));
        entity.setCreatedDate(converter.fromRow(row, prefix + "_created_date", Instant.class));
        entity.setLastModifiedDate(converter.fromRow(row, prefix + "_last_modified_date", Instant.class));
        entity.setCreatedBy(converter.fromRow(row, prefix + "_created_by", String.class));
        entity.setLastModifiedBy(converter.fromRow(row, prefix + "_last_modified_by", String.class));
        entity.setOfferId(converter.fromRow(row, prefix + "_offer_id", Long.class));
        entity.setReviewerId(converter.fromRow(row, prefix + "_reviewer_id", Long.class));
        return entity;
    }
}
