// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz.operator;

import io.lark.column.compute.Compute;
import io.lark.column.tensor.Tensor;

/** Multiply a column by a fixed scalar (n x 1 broadcast). */
public final class MulScalarOp implements ColumnOperator {

    private final double factor;

    public MulScalarOp(double factor) {
        this.factor = factor;
    }

    @Override
    public String operatorKey() {
        return "mul_scalar:" + factor;
    }

    @Override
    public int arity() {
        return 1;
    }

    @Override
    public Tensor apply(Tensor... inputs) {
        return Compute.mulScalar(inputs[0], factor);
    }

    public double factor() {
        return factor;
    }
}
