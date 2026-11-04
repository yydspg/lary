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
 * Reusable mapping from business objects to typed feed columns.
 *
 * <p>Business code defines the mapping once, next to the graph:
 *
 * <pre>{@code
 * FeedMapping<Goods> goodsFeed = FeedMapping.<Goods>builder()
 *         .column(PRICE, Goods::price)
 *         .column(COUPON_AMOUNT, Goods::couponAmount)
 *         .build();
 *
 * pipeline.feed(goodsFeed.map(goodsList));
 * }</pre>
 *
 * <p>Each column binds one {@link ColumnStream} to one getter. The getter
 * result is converted with the stream's declared dtype; {@code null} becomes a
 * nil cell. Mapping is read-only and reusable across requests.
 */
public final class FeedMapping<T> {

    private final ColumnStream<?>[] streams;
    private final List<Function<? super T, ?>> getters;

    private FeedMapping(ColumnStream<?>[] streams,
                        List<Function<? super T, ?>> getters) {
        this.streams = streams;
        this.getters = List.copyOf(getters);
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    /** Internal factory used when a module declares feeds with getters. */
    static FeedMapping<Object> of(List<ColumnStream<?>> streams,
                                  List<Function<Object, ?>> getters) {
        return new FeedMapping<>(
                streams.toArray(ColumnStream<?>[]::new), getters);
    }

    /** Map a business list to a feed stream in declaration order. */
    public FeedStream map(List<? extends T> rows) {
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

    public List<String> names() {
        return java.util.Arrays.stream(streams)
                .map(ColumnStream::name).toList();
    }

    public static final class Builder<T> {

        private final List<ColumnStream<?>> streams = new ArrayList<>();
        private final List<Function<? super T, ?>> getters = new ArrayList<>();

        private Builder() {
        }

        public <V> Builder<T> column(ColumnStream<V> stream,
                                     Function<? super T, ? extends V> getter) {
            streams.add(Objects.requireNonNull(stream, "stream"));
            getters.add(Objects.requireNonNull(getter, "getter"));
            return this;
        }

        public FeedMapping<T> build() {
            if (streams.isEmpty()) {
                throw new ColumnFeedException(
                        "FeedMapping.builder(): declare at least one column");
            }
            return new FeedMapping<>(
                    streams.toArray(ColumnStream<?>[]::new), getters);
        }
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
