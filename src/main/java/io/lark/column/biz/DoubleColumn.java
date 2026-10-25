// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import io.lark.column.DType;

/** A 64-bit floating-point business column. */
public abstract class DoubleColumn extends ColumnStream<Double> {

    protected DoubleColumn() {
        super(DType.FLOAT64);
    }

    protected DoubleColumn(String name) {
        super(name, DType.FLOAT64);
    }
}
