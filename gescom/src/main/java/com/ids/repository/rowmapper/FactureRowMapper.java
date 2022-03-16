package com.ids.repository.rowmapper;

import com.ids.domain.Facture;
import io.r2dbc.spi.Row;
import java.time.LocalDate;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Facture}, with proper type conversions.
 */
@Service
public class FactureRowMapper implements BiFunction<Row, String, Facture> {

    private final ColumnConverter converter;

    public FactureRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Facture} stored in the database.
     */
    @Override
    public Facture apply(Row row, String prefix) {
        Facture entity = new Facture();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setClient(converter.fromRow(row, prefix + "_client", Long.class));
        entity.setDate(converter.fromRow(row, prefix + "_date", LocalDate.class));
        return entity;
    }
}
