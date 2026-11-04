// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz.operator;

import io.lark.column.compute.Compute;
import io.lark.column.tensor.Tensor;

/**
 * Divide by a constant, e.g. {@code gmv_fen / 100}.
 *
 * <p>Parameterized operators are passed as instances:
 * {@code node(GMV, new ScaleOp(100.0), gmvFen)}.
 */
public record ScaleOp(double divisor) implements ColumnOperator {

    public ScaleOp {
        if (divisor == 0.0) {
            throw new IllegalArgumentException("divisor must not be zero");
        }
    }

    @Override
    public String operatorKey() {
        return "scale:" + divisor;
    }

    @Override
    public int arity() {
        return 1;
    }

    @Override
    public Tensor apply(Tensor... inputs) {
        return Compute.divScalar(inputs[0], divisor);
    }
}
