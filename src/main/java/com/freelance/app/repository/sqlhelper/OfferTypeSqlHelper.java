package com.freelance.app.repository.sqlhelper;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class OfferTypeSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>(GenericSqlHelper.getCommonColumns(table, columnPrefix));
        columns.add(Column.aliased("subcategory_id", table, columnPrefix + "_subcategory_id"));
        return columns;
    }

    public static List<Expression> getColumnsShort(Table table, String columnPrefix) {
        return List.of(Column.aliased("id", table, columnPrefix + "_id"), Column.aliased("name", table, columnPrefix + "_name"));
    }
}
