// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.search.node;

import io.lark.column.tensor.DType;
import io.lark.column.tensor.Tensor;
import io.lark.column.compute.Compute;

/** Division with a defined zero-denominator result. */
public final class Ratios {

    private Ratios() {
    }

    public static Tensor divide(Tensor numerator, Tensor denominator) {
        Tensor result = Compute.div(numerator, denominator);
        Tensor zero = Tensor.zeros(result.shape(), DType.FLOAT64);
        return Compute.select(Compute.eqScalar(denominator, 0.0), zero, result);
    }
}
