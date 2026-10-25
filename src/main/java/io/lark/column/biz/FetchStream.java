// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import java.util.List;
import java.util.Objects;

/** Explicit column-oriented fetch stream (column → row). */
public record FetchStream(List<String> columns) {

    public FetchStream {
        columns = List.copyOf(Objects.requireNonNull(columns, "columns"));
    }

    public static FetchStream of(String... columns) {
        return new FetchStream(List.of(columns));
    }

    public static FetchStream of(List<String> columns) {
        return new FetchStream(columns);
    }
}
