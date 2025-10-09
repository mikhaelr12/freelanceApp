package com.freelance.app.repository.rowmapper;

import com.freelance.app.domain.Category;
import com.freelance.app.service.dto.CategoryDTO;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Category}, with proper type conversions.
 */
@Service
public class CategoryRowMapper implements BiFunction<Row, String, Category> {

    private final ColumnConverter converter;

    public CategoryRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Category} stored in the database.
     */
    @Override
    public Category apply(Row row, String prefix) {
        Category entity = new Category();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setCreatedDate(converter.fromRow(row, prefix + "_created_date", Instant.class));
        entity.setLastModifiedDate(converter.fromRow(row, prefix + "_last_modified_date", Instant.class));
        entity.setCreatedBy(converter.fromRow(row, prefix + "_created_by", String.class));
        entity.setLastModifiedBy(converter.fromRow(row, prefix + "_last_modified_by", String.class));
        entity.setActive(converter.fromRow(row, prefix + "_active", Boolean.class));
        return entity;
    }

    public CategoryDTO applyDTO(Row row, String prefix) {
        CategoryDTO entity = new CategoryDTO();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        return entity;
    }
}
