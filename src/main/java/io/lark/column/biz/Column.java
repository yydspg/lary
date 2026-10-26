// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import io.lark.column.data.DType;

import java.util.Objects;

/**
 * Immutable definition of a typed business column.
 *
 * <p>A column definition is a value object, not a node. Declare the whole
 * schema once as constants:
 *
 * <pre>{@code
 * public final class Metrics {
 *     public static final Column<Long> IMPRESSIONS =
 *             new Column<>("impressions", DType.INT64);
 *     public static final Column<Double> CTR =
 *             new Column<>("ctr", DType.FLOAT64);
 * }
 * }</pre>
 *
 * <p>{@link ColumnNode#fetch(Column)} and {@link ColumnModule#input(Column)}
 * turn a definition into a {@link ColumnStream} handle bound to a producer or
 * external feed. The definition carries the name and dtype; the handle carries
 * the graph wiring identity.
 */
public record Column<T>(String name, DType dtype) {

    public Column {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(dtype, "dtype");
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Column name must not be empty");
        }
    }
}
