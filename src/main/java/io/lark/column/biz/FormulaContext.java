// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import io.lark.column.context.ExperimentContext;
import io.lark.column.tensor.Tensor;
import io.lark.column.tensor.TensorShape;

import java.util.Objects;

/** Read-only evaluation context for a formula node. */
public final class FormulaContext {

    private final ColumnContext columns;
    private final TensorShape shape;

    public FormulaContext(ColumnContext columns, TensorShape shape) {
        this.columns = Objects.requireNonNull(columns, "columns");
        this.shape = Objects.requireNonNull(shape, "shape");
    }

    public Tensor read(ColumnStream<?> stream) {
        return columns.read(stream);
    }

    public ExperimentContext experiment() {
        return columns.experiment();
    }

    public TensorShape shape() {
        return shape;
    }
}
