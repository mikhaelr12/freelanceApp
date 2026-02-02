package com.freelance.app.repository.sqlhelper;

import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class CategorySqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        return GenericSqlHelper.getCommonColumns(table, columnPrefix);
    }

    public static List<Expression> getColumnsDTO(Table table, String columnPrefix) {
        return List.of(Column.aliased("id", table, columnPrefix + "_id"), Column.aliased("name", table, columnPrefix + "_name"));
    }
}
