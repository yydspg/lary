// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz.op;

import io.lark.column.biz.ColumnOperator;
import io.lark.column.biz.ColumnOperatorContext;
import io.lark.column.tensor.Tensor;

/**
 * Scalar produced from an experiment parameter.
 *
 * <p>Use it as an anonymous internal node and broadcast it with another column:
 *
 * <pre>{@code
 * ColumnStream<Double> weight = node(new ParamOp("rerank.exp_w", 1.0));
 * ColumnStream<Double> weighted = node(MulOp.class, score, weight);
 * }</pre>
 *
 * <p>The value is read per run from {@code ColumnOperatorContext.experiment()},
 * so changing an experiment does not rebuild the graph.
 */
public final class ParamOp implements ColumnOperator {

    private final String key;
    private final double fallback;

    public ParamOp(String key, double fallback) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("key must not be blank");
        }
        this.key = key;
        this.fallback = fallback;
    }

    @Override
    public int arity() {
        return 0;
    }

    @Override
    public Tensor apply(Tensor... inputs) {
        return Tensor.fromDoubles(fallback);
    }

    @Override
    public Tensor apply(ColumnOperatorContext context, Tensor... inputs) {
        return Tensor.fromDoubles(
                context.experiment().param(key, fallback));
    }

    public String key() {
        return key;
    }

    public double fallback() {
        return fallback;
    }
}
