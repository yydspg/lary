// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import io.lark.column.DType;

/** A 64-bit integer business column. */
public abstract class LongColumn extends ColumnStream<Long> {

    protected LongColumn() {
        super(DType.INT64);
    }

    protected LongColumn(String name) {
        super(name, DType.INT64);
    }
}
