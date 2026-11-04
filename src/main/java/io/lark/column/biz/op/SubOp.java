// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz.op;

import io.lark.column.biz.ColumnOperator;
import io.lark.column.compute.Compute;
import io.lark.column.tensor.Tensor;

/** Element-wise subtraction with dtype promotion. */
public final class SubOp implements ColumnOperator {

    @Override
    public int arity() {
        return 2;
    }

    @Override
    public Tensor apply(Tensor... inputs) {
        return Compute.sub(inputs[0], inputs[1]);
    }
}
