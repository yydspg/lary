// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
        Objects.requireNonNull(names, "names");
        Objects.requireNonNull(rows, "rows");
        TensorTable table = new TensorTable();
        int rowCount = rows.size();
        for (int c = 0; c < names.size(); c++) {
            boolean anyDouble = false;
            for (int r = 0; r < rowCount; r++) {
                Cell cell = cellAt(rows.get(r), c);
                if (!cell.isNil() && cell.isDouble()) {
                    anyDouble = true;
                    break;
                }
            }
            // Build the primitive buffer directly: no per-cell List<Cell>
            // transpose and no per-element virtual setter.
            if (anyDouble) {
                double[] data = new double[rowCount];
                for (int r = 0; r < rowCount; r++) {
                    data[r] = cellAt(rows.get(r), c).asDouble();
                }
                table.add(names.get(c), Tensor.wrapDoubles(data));
            } else {
                long[] data = new long[rowCount];
                for (int r = 0; r < rowCount; r++) {
                    data[r] = cellAt(rows.get(r), c).asLong();
                }
                table.add(names.get(c), Tensor.wrapLongs(data));
            }
        }
        return table;
    }

    private static Cell cellAt(Row row, int column) {
        if (column >= row.size()) {
            throw new IllegalArgumentException(
                    "feed_table: row has fewer cells than columns");
        }
        return Objects.requireNonNull(row.get(column), "cell");
    }

    /** Build a column table from named, already column-oriented data. */
    public static TensorTable feedTable(Map<String, List<Cell>> columns) {
        Objects.requireNonNull(columns, "columns");
        TensorTable table = new TensorTable();
        int rows = -1;
        for (Map.Entry<String, List<Cell>> entry : columns.entrySet()) {
            List<Cell> cells = Objects.requireNonNull(entry.getValue(), "column cells");
            if (rows < 0) {
                rows = cells.size();
            } else if (rows != cells.size()) {
                throw new IllegalArgumentException(
                        "feed_table: column '" + entry.getKey()
                                + "' length mismatch");
            }
            table.add(entry.getKey(), columnTensor(cells));
        }
        return table;
    }

    private static Tensor columnTensor(List<Cell> cells) {
        int count = cells.size();
        boolean anyDouble = false;
        for (int i = 0; i < count; i++) {
            Cell cell = Objects.requireNonNull(cells.get(i), "cell");
            if (!cell.isNil() && cell.isDouble()) {
                anyDouble = true;
                break;
            }
        }

        if (anyDouble) {
            double[] data = new double[count];
            for (int i = 0; i < count; i++) {
                data[i] = Objects.requireNonNull(cells.get(i), "cell").asDouble();
            }
            return Tensor.wrapDoubles(data);
        }
        long[] data = new long[count];
        for (int i = 0; i < count; i++) {
            data[i] = Objects.requireNonNull(cells.get(i), "cell").asLong();
        }
        return Tensor.wrapLongs(data);
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

    /** Convert selected table columns to named cell lists. */
    public static Map<String, List<Cell>> fetchColumns(TensorTable table,
                                                       List<String> names) {
        if (names.isEmpty()) {
            return Map.of();
        }
        int rows = table.get(names.get(0)).size();
        Map<String, List<Cell>> result = new LinkedHashMap<>();
        for (String name : names) {
            Tensor column = table.get(name);
            if (column.size() != rows) {
                throw new IllegalArgumentException(
                        "fetch_rows: column '" + name + "' length mismatch");
            }
            if (column.dtype() == DType.Q8_0) {
                column = column.dequantize();
            }
            List<Cell> cells = new ArrayList<>(rows);
            for (int r = 0; r < rows; r++) {
                cells.add(cellAt(column, r));
            }
            result.put(name, List.copyOf(cells));
        }
        return Collections.unmodifiableMap(result);
    }

    /** Convert all table columns to named cell lists. */
    public static Map<String, List<Cell>> fetchColumns(TensorTable table) {
        return fetchColumns(table, table.names());
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
            case Q8_0 -> {
                BlockQ8_0 block = tensor.q8Blocks()[0];
                yield block.q(0) * (double) block.scale();
            }
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
