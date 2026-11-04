// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz.flow;

import io.lark.column.biz.ColumnStream;

import io.lark.column.ColumnFeedException;
import io.lark.column.data.Row;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/** Row-oriented input bundle: typed external streams plus rows. */
public record FeedStream(ColumnStream<?>[] streams, List<Row> rows) {

    public FeedStream {
        streams = streams == null ? new ColumnStream<?>[0] : streams.clone();
        rows = List.copyOf(Objects.requireNonNull(rows, "rows"));
    }

    public static FeedStream of(ColumnStream<?> stream, List<Row> rows) {
        return new FeedStream(new ColumnStream<?>[]{stream}, rows);
    }

    public static FeedStream of(ColumnStream<?>[] streams, List<Row> rows) {
        return new FeedStream(streams, rows);
    }

    /** Concatenate feeds that share the same stream handles. */
    public static FeedStream concat(FeedStream... feeds) {
        Objects.requireNonNull(feeds, "feeds");
        if (feeds.length == 0) {
            return new FeedStream(new ColumnStream<?>[0], List.of());
        }
        ColumnStream<?>[] streams = feeds[0].streams;
        List<String> names = feeds[0].names();
        List<Row> rows = new ArrayList<>();
        for (FeedStream feed : feeds) {
            if (!names.equals(feed.names())) {
                throw new ColumnFeedException(
                        "FeedStream.concat: column mismatch " + names
                                + " vs " + feed.names());
            }
            rows.addAll(feed.rows);
        }
        return new FeedStream(streams, rows);
    }

    public List<String> names() {
        return Arrays.stream(streams).map(ColumnStream::name).toList();
    }
}
