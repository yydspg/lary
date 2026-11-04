// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz.operator;

import io.lark.column.compute.Compute;
import io.lark.column.tensor.Tensor;

/** {@code 1 - input}. */
public final class OneMinusOp implements ColumnOperator {

    @Override
    public int arity() {
        return 1;
    }

    @Override
    public Tensor apply(Tensor... inputs) {
        return Compute.addScalar(Compute.neg(inputs[0]), 1.0);
    }
}
