package com.freelance.app.repository.sqlhelper;

import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class GenericSqlHelper {

    public static List<Expression> getCommonColumns(Table table, String columnPrefix) {
        return List.of(
            Column.aliased("id", table, columnPrefix + "_id"),
            Column.aliased("name", table, columnPrefix + "_name"),
            Column.aliased("created_date", table, columnPrefix + "_created_date"),
            Column.aliased("last_modified_date", table, columnPrefix + "_last_modified_date"),
            Column.aliased("created_by", table, columnPrefix + "_created_by"),
            Column.aliased("last_modified_by", table, columnPrefix + "_last_modified_by"),
            Column.aliased("active", table, columnPrefix + "_active")
        );
    }
}
