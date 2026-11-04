// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import io.lark.column.ColumnFeedException;
import io.lark.column.data.Cell;
import io.lark.column.data.Row;
import io.lark.column.tensor.Tensor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Internal mapping from business objects to feed columns. Business code
 * declares the same mapping with {@code feed(Column, getter)} on a module.
 */
final class FeedMapping<T> {

    interface Value<T> {
        long longValue(T value);

        double doubleValue(T value);
    }

    private final ColumnStream<?>[] streams;
    private final List<Value<? super T>> getters;

    private FeedMapping(ColumnStream<?>[] streams,
                        List<Value<? super T>> getters) {
        this.streams = streams;
        this.getters = List.copyOf(getters);
    }

    /** Internal factory used when a module declares feeds with getters. */
    static FeedMapping<Object> of(List<ColumnStream<?>> streams,
                                  List<Value<Object>> getters) {
        return new FeedMapping<>(
                streams.toArray(ColumnStream<?>[]::new), getters);
    }

    /** Map a business list to a feed stream in declaration order. */
    FeedStream map(List<? extends T> rows) {
        Objects.requireNonNull(rows, "rows");
        List<Row> mapped = new ArrayList<>(rows.size());
        for (T value : rows) {
            Cell[] cells = new Cell[streams.length];
            for (int i = 0; i < cells.length; i++) {
                Value<? super T> getter = getters.get(i);
                cells[i] = streams[i].dtype().isFloat()
                        ? Cell.of(getter.doubleValue(value))
                        : Cell.of(getter.longValue(value));
            }
            mapped.add(Row.of(cells));
        }
        return new FeedStream(streams, mapped);
    }

    /** Build columns directly, avoiding one Cell/Row object per value. */
    Map<String, Tensor> tensors(List<? extends T> rows) {
        Objects.requireNonNull(rows, "rows");
        int count = rows.size();
        Map<String, Tensor> result = new LinkedHashMap<>();
        for (int column = 0; column < streams.length; column++) {
            ColumnStream<?> stream = streams[column];
            Value<? super T> getter = getters.get(column);
            if (stream.dtype().isFloat()) {
                double[] values = new double[count];
                for (int row = 0; row < count; row++) {
                    values[row] = getter.doubleValue(rows.get(row));
                }
                result.put(stream.name(), Tensor.wrapDoubles(values));
            } else {
                long[] values = new long[count];
                for (int row = 0; row < count; row++) {
                    values[row] = getter.longValue(rows.get(row));
                }
                result.put(stream.name(), Tensor.wrapLongs(values));
            }
        }
        return result;
    }

}
