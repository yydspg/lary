// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import io.lark.column.DType;

/** A 32-bit floating-point business column. */
public abstract class FloatColumn extends ColumnStream<Float> {

    protected FloatColumn() {
        super(DType.FLOAT32);
    }

    protected FloatColumn(String name) {
        super(name, DType.FLOAT32);
    }
}
