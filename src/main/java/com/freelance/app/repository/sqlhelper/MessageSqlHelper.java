package com.freelance.app.repository.sqlhelper;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class MessageSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("body", table, columnPrefix + "_body"));
        columns.add(Column.aliased("sent_at", table, columnPrefix + "_sent_at"));

        columns.add(Column.aliased("conversation_id", table, columnPrefix + "_conversation_id"));
        columns.add(Column.aliased("sender_id", table, columnPrefix + "_sender_id"));
        return columns;
    }
}
