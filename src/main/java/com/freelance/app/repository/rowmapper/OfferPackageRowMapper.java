package com.freelance.app.repository.rowmapper;

import com.freelance.app.domain.OfferPackage;
import com.freelance.app.domain.enumeration.PackageTier;
import io.r2dbc.spi.Row;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link OfferPackage}, with proper type conversions.
 */
@Service
public class OfferPackageRowMapper implements BiFunction<Row, String, OfferPackage> {

    private final ColumnConverter converter;

    public OfferPackageRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link OfferPackage} stored in the database.
     */
    @Override
    public OfferPackage apply(Row row, String prefix) {
        OfferPackage entity = new OfferPackage();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setPrice(converter.fromRow(row, prefix + "_price", BigDecimal.class));
        entity.setCurrency(converter.fromRow(row, prefix + "_currency", String.class));
        entity.setDeliveryDays(converter.fromRow(row, prefix + "_delivery_days", Integer.class));
        entity.setPackageTier(converter.fromRow(row, prefix + "_package_tier", PackageTier.class));
        entity.setActive(converter.fromRow(row, prefix + "_active", Boolean.class));
        entity.setCreatedDate(converter.fromRow(row, prefix + "_created_date", Instant.class));
        entity.setLastModifiedDate(converter.fromRow(row, prefix + "_last_modified_date", Instant.class));
        entity.setCreatedBy(converter.fromRow(row, prefix + "_created_by", String.class));
        entity.setLastModifiedBy(converter.fromRow(row, prefix + "_last_modified_by", String.class));
        entity.setOfferId(converter.fromRow(row, prefix + "_offer_id", Long.class));
        return entity;
    }
}
