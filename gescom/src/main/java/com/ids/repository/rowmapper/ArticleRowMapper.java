package com.ids.repository.rowmapper;

import com.ids.domain.Article;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Article}, with proper type conversions.
 */
@Service
public class ArticleRowMapper implements BiFunction<Row, String, Article> {

    private final ColumnConverter converter;

    public ArticleRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Article} stored in the database.
     */
    @Override
    public Article apply(Row row, String prefix) {
        Article entity = new Article();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setDesignation(converter.fromRow(row, prefix + "_designation", String.class));
        entity.setPu(converter.fromRow(row, prefix + "_pu", Double.class));
        entity.setCategorieId(converter.fromRow(row, prefix + "_categorie_id", Long.class));
        return entity;
    }
}
