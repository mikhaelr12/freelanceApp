package com.freelance.app.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class VerificationRequestSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));

        columns.add(Column.aliased("profile_id", table, columnPrefix + "_profile_id"));
        columns.add(Column.aliased("file_object_id", table, columnPrefix + "_file_object_id"));
        return columns;
    }
}
