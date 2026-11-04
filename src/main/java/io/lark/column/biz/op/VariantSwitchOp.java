// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz.op;

import io.lark.column.ColumnExecutionException;
import io.lark.column.biz.ColumnOperator;
import io.lark.column.biz.ColumnOperatorContext;
import io.lark.column.tensor.Tensor;

/**
 * Runtime experiment switch between two columns.
 *
 * <p>When the experiment layer is assigned {@code variant}, input 1 is
 * returned; otherwise input 0. The graph is not rebuilt.
 */
public final class VariantSwitchOp implements ColumnOperator {

    private final String layer;
    private final String variant;

    public VariantSwitchOp(String layer, String variant) {
        if (layer == null || layer.isBlank()) {
            throw new IllegalArgumentException("layer must not be blank");
        }
        if (variant == null || variant.isBlank()) {
            throw new IllegalArgumentException("variant must not be blank");
        }
        this.layer = layer;
        this.variant = variant;
    }

    @Override
    public int arity() {
        return 2;
    }

    @Override
    public Tensor apply(Tensor... inputs) {
        return inputs[0];
    }

    @Override
    public Tensor apply(ColumnOperatorContext context, Tensor... inputs) {
        Tensor first = inputs[0];
        Tensor second = inputs[1];
        if (!first.shape().equals(second.shape())) {
            throw new ColumnExecutionException(
                    "VariantSwitchOp inputs must have the same shape");
        }
        return context.experiment().is(layer, variant) ? second : first;
    }

    public String layer() {
        return layer;
    }

    public String variant() {
        return variant;
    }
}
