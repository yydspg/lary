// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz.op;

import io.lark.column.biz.ColumnOperator;
import io.lark.column.compute.Compute;
import io.lark.column.tensor.Tensor;

/** Element-wise addition with dtype promotion. */
public final class AddOp implements ColumnOperator {

    @Override
    public int arity() {
        return 2;
    }

    @Override
    public Tensor apply(Tensor... inputs) {
        return Compute.add(inputs[0], inputs[1]);
    }
}
