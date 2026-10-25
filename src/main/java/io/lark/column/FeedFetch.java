// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Row ↔ column conversion.
 *
 * <p>Feed infers FLOAT64 when a column contains any double, otherwise INT64.
 * Missing cells are zero-filled. Fetch renders integer columns as int cells and
 * float / quantized columns as double cells.
 */
public final class FeedFetch {

    private FeedFetch() {
    }

    public record Column(String name, Tensor tensor) {
        public Column {
            Objects.requireNonNull(name, "name");
            Objects.requireNonNull(tensor, "tensor");
        }
    }

    public static TensorTable feedTable(List<String> names, List<Row> rows) {
        TensorTable table = new TensorTable();

        for (int c = 0; c < names.size(); c++) {
            boolean anyDouble = false;
            for (Row row : rows) {
                if (c >= row.size()) {
                    throw new IllegalArgumentException(
                            "feed_table: row has fewer cells than columns");
                }
                Cell cell = row.get(c);
                if (!cell.isNil() && cell.isDouble()) {
                    anyDouble = true;
                }
            }

            DType dtype = anyDouble ? DType.FLOAT64 : DType.INT64;
            Tensor column = Tensor.zeros(rows.size(), dtype);
            for (int r = 0; r < rows.size(); r++) {
                Cell cell = rows.get(r).get(c);
                if (cell.isNil()) {
                    if (dtype == DType.INT64) column.setLong(r, 0L);
                    else column.setDouble(r, 0.0);
                } else if (cell.isDouble()) {
                    if (dtype == DType.INT64) column.setLong(r, cell.asLong());
                    else column.setDouble(r, cell.asDouble());
                } else {
                    if (dtype == DType.INT64) column.setLong(r, cell.asLong());
                    else column.setDouble(r, cell.asDouble());
                }
            }
            table.add(names.get(c), column);
        }

        return table;
    }

    public static TensorTable tableFromColumns(List<Column> columns) {
        TensorTable table = new TensorTable();
        for (Column column : columns) {
            table.add(column.name(), column.tensor());
        }
        return table;
    }

    public static List<Row> fetchRows(TensorTable table, List<String> names) {
        if (names.isEmpty()) {
            return List.of();
        }
        int rows = table.get(names.get(0)).size();
        for (String name : names) {
            if (!table.has(name)) {
                throw new NoSuchElementException("fetch_rows: unknown column '" + name + "'");
            }
            if (table.get(name).size() != rows) {
                throw new IllegalArgumentException(
                        "fetch_rows: column '" + name + "' length mismatch");
            }
        }

        List<Row> result = new ArrayList<>(rows);
        Tensor[] columns = new Tensor[names.size()];
        for (int c = 0; c < names.size(); c++) {
            Tensor column = table.get(names.get(c));
            columns[c] = column.dtype() == DType.Q8_0
                    ? column.dequantize() : column;
        }

        for (int r = 0; r < rows; r++) {
            List<Cell> cells = new ArrayList<>(names.size());
            for (Tensor col : columns) {
                cells.add(cellAt(col, r));
            }
            result.add(new Row(cells));
        }
        return result;
    }

    public static List<Row> fetchRows(TensorTable table) {
        return fetchRows(table, table.names());
    }

    public static double fetchScalar(Tensor tensor) {
        if (tensor.size() == 0) {
            return 0.0;
        }
        return switch (tensor.dtype()) {
            case INT32 -> tensor.getInt(0);
            case INT64 -> tensor.getLong(0);
            case FLOAT32 -> tensor.getFloat(0);
            case FLOAT64 -> tensor.getDouble(0);
            case Q8_0 -> tensor.dequantize().getDouble(0);
        };
    }

    private static Cell cellAt(Tensor tensor, int row) {
        return switch (tensor.dtype()) {
            case INT32 -> Cell.of((long) tensor.getInt(row));
            case INT64 -> Cell.of(tensor.getLong(row));
            case FLOAT32 -> Cell.of((double) tensor.getFloat(row));
            case FLOAT64 -> Cell.of(tensor.getDouble(row));
            case Q8_0 -> Cell.of(tensor.dequantize().getDouble(row));
        };
    }
}
