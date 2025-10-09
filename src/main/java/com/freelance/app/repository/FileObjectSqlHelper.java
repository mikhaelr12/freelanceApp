package com.freelance.app.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class FileObjectSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("bucket", table, columnPrefix + "_bucket"));
        columns.add(Column.aliased("object_key", table, columnPrefix + "_object_key"));
        columns.add(Column.aliased("content_type", table, columnPrefix + "_content_type"));
        columns.add(Column.aliased("file_size", table, columnPrefix + "_file_size"));
        columns.add(Column.aliased("checksum", table, columnPrefix + "_checksum"));
        columns.add(Column.aliased("duration_seconds", table, columnPrefix + "_duration_seconds"));
        columns.add(Column.aliased("created_date", table, columnPrefix + "_created_date"));
        columns.add(Column.aliased("last_modified_date", table, columnPrefix + "_last_modified_date"));
        columns.add(Column.aliased("created_by", table, columnPrefix + "_created_by"));
        columns.add(Column.aliased("last_modified_by", table, columnPrefix + "_last_modified_by"));

        return columns;
    }
}
