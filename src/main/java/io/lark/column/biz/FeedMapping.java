// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import io.lark.column.ColumnFeedException;
import io.lark.column.data.Cell;
import io.lark.column.data.Row;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * Internal mapping from business objects to feed columns. Business code
 * declares the same mapping with {@code feed(Column, getter)} on a module.
 */
final class FeedMapping<T> {

    private final ColumnStream<?>[] streams;
    private final List<Function<? super T, ?>> getters;

    private FeedMapping(ColumnStream<?>[] streams,
                        List<Function<? super T, ?>> getters) {
        this.streams = streams;
        this.getters = List.copyOf(getters);
    }

    /** Internal factory used when a module declares feeds with getters. */
    static FeedMapping<Object> of(List<ColumnStream<?>> streams,
                                  List<Function<Object, ?>> getters) {
        return new FeedMapping<>(
                streams.toArray(ColumnStream<?>[]::new), getters);
    }

    /** Map a business list to a feed stream in declaration order. */
    FeedStream map(List<? extends T> rows) {
        Objects.requireNonNull(rows, "rows");
        List<Row> mapped = new ArrayList<>(rows.size());
        for (T value : rows) {
            Cell[] cells = new Cell[getters.size()];
            for (int i = 0; i < cells.length; i++) {
                cells[i] = cell(streams[i], getters.get(i).apply(value));
            }
            mapped.add(Row.of(cells));
        }
        return new FeedStream(streams, mapped);
    }

    private static Cell cell(ColumnStream<?> stream, Object value) {
        if (value == null) {
            return Cell.nil();
        }
        if (!(value instanceof Number number)) {
            throw new ColumnFeedException(
                    "Feed column '" + stream.name()
                            + "' expects a number, got "
                            + value.getClass().getName());
        }
        return stream.dtype().isFloat()
                ? Cell.of(number.doubleValue())
                : Cell.of(number.longValue());
    }
}
