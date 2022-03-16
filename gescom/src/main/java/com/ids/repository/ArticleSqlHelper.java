package com.ids.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class ArticleSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("designation", table, columnPrefix + "_designation"));
        columns.add(Column.aliased("pu", table, columnPrefix + "_pu"));

        columns.add(Column.aliased("categorie_id", table, columnPrefix + "_categorie_id"));
        return columns;
    }
}
