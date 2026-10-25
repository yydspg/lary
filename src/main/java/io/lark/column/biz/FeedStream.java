// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import io.lark.column.Row;

import java.util.List;
import java.util.Objects;

/** Row-oriented input bundle: external streams plus rows. */
public record FeedStream(ColumnStream[] streams, List<Row> rows) {

    public FeedStream {
        streams = streams == null ? new ColumnStream[0] : streams.clone();
        rows = List.copyOf(Objects.requireNonNull(rows, "rows"));
    }

    public static FeedStream of(ColumnStream stream, List<Row> rows) {
        return new FeedStream(new ColumnStream[]{stream}, rows);
    }

    public static FeedStream of(ColumnStream[] streams, List<Row> rows) {
        return new FeedStream(streams, rows);
    }

    public List<String> names() {
        return java.util.Arrays.stream(streams).map(ColumnStream::name).toList();
    }
}
