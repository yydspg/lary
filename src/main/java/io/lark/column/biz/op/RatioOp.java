// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz.op;

import io.lark.column.biz.ColumnOperator;
import io.lark.column.compute.Compute;
import io.lark.column.tensor.DType;
import io.lark.column.tensor.Tensor;

/** Division with a defined zero result for a zero denominator. */
public final class RatioOp implements ColumnOperator {

    @Override
    public int arity() {
        return 2;
    }

    @Override
    public Tensor apply(Tensor... inputs) {
        return divide(inputs[0], inputs[1]);
    }

    /** Safe division shared by ratio-style domain operators. */
    public static Tensor divide(Tensor numerator, Tensor denominator) {
        Tensor result = Compute.div(numerator, denominator);
        Tensor zero = Tensor.zeros(result.shape(), DType.FLOAT64);
        return Compute.select(Compute.eqScalar(denominator, 0.0), zero, result);
    }
}
