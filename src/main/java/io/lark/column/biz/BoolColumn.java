// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import io.lark.column.DType;

/** A boolean mask column, stored as 0/1 in INT64 (the tensor convention). */
public abstract class BoolColumn extends ColumnStream<Boolean> {

    protected BoolColumn() {
        super(DType.INT64);
    }

    protected BoolColumn(String name) {
        super(name, DType.INT64);
    }
}
