// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import io.lark.column.DType;

/** A 32-bit integer business column. */
public abstract class IntColumn extends ColumnStream<Integer> {

    protected IntColumn() {
        super(DType.INT32);
    }

    protected IntColumn(String name) {
        super(name, DType.INT32);
    }
}
