// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz.operator;

import io.lark.column.compute.Compute;
import io.lark.column.tensor.Tensor;

/** Element-wise division; use {@link RatioOp} for a defined zero result. */
public final class DivOp implements ColumnOperator {

    @Override
    public int arity() {
        return 2;
    }

    @Override
    public Tensor apply(Tensor... inputs) {
        return Compute.div(inputs[0], inputs[1]);
    }
}
