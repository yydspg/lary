// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

/** Where a business node input column comes from. */
public enum ColumnSource {
    /** Infer: use a producer node when present, otherwise a feed column. */
    AUTO,
    /** Must come from row-oriented feed data. */
    FEED,
    /** Must be produced by another business node. */
    NODE
}
