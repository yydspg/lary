// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import io.lark.column.Row;

import java.util.List;
import java.util.Objects;

/** Explicit row-oriented feed stream (row → column). */
public record FeedStream(List<String> columns, List<Row> rows) {

    public FeedStream {
        columns = List.copyOf(Objects.requireNonNull(columns, "columns"));
        rows = List.copyOf(Objects.requireNonNull(rows, "rows"));
    }

    public static FeedStream of(List<String> columns, List<Row> rows) {
        return new FeedStream(columns, rows);
    }
}
