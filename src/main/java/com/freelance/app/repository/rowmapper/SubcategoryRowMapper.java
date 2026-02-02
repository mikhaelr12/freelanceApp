package com.freelance.app.repository.rowmapper;

import com.freelance.app.domain.Subcategory;
import com.freelance.app.service.dto.CategoryShortDTO;
import com.freelance.app.service.dto.SubcategoryDTO;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Subcategory}, with proper type conversions.
 */
@Service
public class SubcategoryRowMapper implements BiFunction<Row, String, Subcategory> {

    private final ColumnConverter converter;

    public SubcategoryRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Subcategory} stored in the database.
     */
    @Override
    public Subcategory apply(Row row, String prefix) {
        Subcategory entity = new Subcategory();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setCreatedDate(converter.fromRow(row, prefix + "_created_date", Instant.class));
        entity.setLastModifiedDate(converter.fromRow(row, prefix + "_last_modified_date", Instant.class));
        entity.setCreatedBy(converter.fromRow(row, prefix + "_created_by", String.class));
        entity.setLastModifiedBy(converter.fromRow(row, prefix + "_last_modified_by", String.class));
        entity.setActive(converter.fromRow(row, prefix + "_active", Boolean.class));
        entity.setCategoryId(converter.fromRow(row, prefix + "_category_id", Long.class));
        return entity;
    }

    public SubcategoryDTO map(Row row) {
        return new SubcategoryDTO(
            row.get("e_id", Long.class),
            row.get("e_name", String.class),
            new CategoryShortDTO(row.get("category_id", Long.class), row.get("category_name", String.class))
        );
    }
}
