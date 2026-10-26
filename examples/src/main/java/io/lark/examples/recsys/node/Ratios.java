// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.recsys.node;

import io.lark.column.data.DType;
import io.lark.column.data.Tensor;
import io.lark.column.compute.Compute;

/** Division with a defined result for zero denominators. */
final class Ratios {

    private Ratios() {
    }

    static Tensor divide(Tensor numerator, Tensor denominator) {
        Tensor result = Compute.div(numerator, denominator);
        Tensor zero = Tensor.zeros(result.size(), DType.FLOAT64);
        return Compute.select(Compute.eqScalar(denominator, 0.0), zero, result);
    }
}
