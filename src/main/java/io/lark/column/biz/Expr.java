// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import io.lark.column.tensor.DType;
import io.lark.column.tensor.Tensor;

import java.util.Set;

/**
 * Typed, composable business expression.
 *
 * <p>Expressions are evaluated inside a {@link FormulaNode}; they can read
 * typed columns, experiment parameters, variants, comparisons and conditional
 * branches. The generic type documents the business result type while
 * {@link #dtype()} supplies the runtime column dtype.
 */
public interface Expr<T> {

    Tensor eval(FormulaContext ctx);

    Set<ColumnStream<?>> inputs();

    DType dtype();
}
