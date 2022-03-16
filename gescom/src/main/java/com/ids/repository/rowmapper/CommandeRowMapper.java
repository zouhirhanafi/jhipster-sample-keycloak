package com.ids.repository.rowmapper;

import com.ids.domain.Commande;
import io.r2dbc.spi.Row;
import java.time.LocalDate;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Commande}, with proper type conversions.
 */
@Service
public class CommandeRowMapper implements BiFunction<Row, String, Commande> {

    private final ColumnConverter converter;

    public CommandeRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Commande} stored in the database.
     */
    @Override
    public Commande apply(Row row, String prefix) {
        Commande entity = new Commande();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setDate(converter.fromRow(row, prefix + "_date", LocalDate.class));
        entity.setClient(converter.fromRow(row, prefix + "_client", Long.class));
        return entity;
    }
}
