package com.freelance.app.repository.rowmapper;

import com.freelance.app.domain.Profile;
import com.freelance.app.domain.enumeration.ProfileType;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Profile}, with proper type conversions.
 */
@Service
public class ProfileRowMapper implements BiFunction<Row, String, Profile> {

    private final ColumnConverter converter;

    public ProfileRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Profile} stored in the database.
     */
    @Override
    public Profile apply(Row row, String prefix) {
        Profile entity = new Profile();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setFirstName(converter.fromRow(row, prefix + "_first_name", String.class));
        entity.setLastName(converter.fromRow(row, prefix + "_last_name", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setCreatedDate(converter.fromRow(row, prefix + "_created_date", Instant.class));
        entity.setLastModifiedDate(converter.fromRow(row, prefix + "_last_modified_date", Instant.class));
        entity.setCreatedBy(converter.fromRow(row, prefix + "_created_by", String.class));
        entity.setLastModifiedBy(converter.fromRow(row, prefix + "_last_modified_by", String.class));
        entity.setProfileType(converter.fromRow(row, prefix + "_profile_type", ProfileType.class));
        entity.setUserId(converter.fromRow(row, prefix + "_user_id", Long.class));
        entity.setProfilePictureId(converter.fromRow(row, prefix + "_profile_picture_id", Long.class));
        return entity;
    }
}
