// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import java.util.Arrays;
import java.util.List;

/** Column-oriented output bundle. */
public record FetchStream(ColumnStream<?>[] streams) {

    public FetchStream {
        streams = streams == null ? new ColumnStream<?>[0] : streams.clone();
    }

    public static FetchStream of(ColumnStream<?>... streams) {
        return new FetchStream(streams);
    }

    public List<String> names() {
        return Arrays.stream(streams).map(ColumnStream::name).toList();
    }
}
