// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz.op;

import io.lark.column.biz.ColumnOperator;
import io.lark.column.compute.Compute;
import io.lark.column.tensor.Tensor;

/** Element-wise multiplication with dtype promotion. */
public final class MulOp implements ColumnOperator {

    @Override
    public int arity() {
        return 2;
    }

    @Override
    public Tensor apply(Tensor... inputs) {
        return Compute.mul(inputs[0], inputs[1]);
    }
}
