package com.freelance.app.repository.rowmapper;

import com.freelance.app.domain.FavoriteOffer;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link FavoriteOffer}, with proper type conversions.
 */
@Service
public class FavoriteOfferRowMapper implements BiFunction<Row, String, FavoriteOffer> {

    private final ColumnConverter converter;

    public FavoriteOfferRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link FavoriteOffer} stored in the database.
     */
    @Override
    public FavoriteOffer apply(Row row, String prefix) {
        FavoriteOffer entity = new FavoriteOffer();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setCreatedAt(converter.fromRow(row, prefix + "_created_at", Instant.class));
        entity.setProfileId(converter.fromRow(row, prefix + "_profile_id", Long.class));
        entity.setOfferId(converter.fromRow(row, prefix + "_offer_id", Long.class));
        return entity;
    }
}
