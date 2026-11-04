// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz.operator;

import io.lark.column.compute.Compute;
import io.lark.column.tensor.Tensor;

/** {@code input0 * input1 + input2} with the same broadcast rules. */
public final class MulAddOp implements ColumnOperator {

    @Override
    public int arity() {
        return 3;
    }

    @Override
    public Tensor apply(Tensor... inputs) {
        return Compute.mulAdd(inputs[0], inputs[1], inputs[2]);
    }
}
