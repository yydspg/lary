// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz.operator;

import io.lark.column.compute.Compute;
import io.lark.column.tensor.Tensor;

/**
 * Multiply a column by an experiment parameter (n x 1 broadcast).
 *
 * <p>The parameter is read per run, so an experiment can change the weight
 * without rebuilding the graph:
 *
 * <pre>{@code
 * ColumnStream<Double> roi = node(RatioOp.class, gmvFen, cost);
 * roi = node(new ExperimentScaleOp("rerank.roi_w", 1.0), roi);
 * }</pre>
 */
public final class ExperimentScaleOp implements ColumnOperator {

    private final String key;
    private final double fallback;

    public ExperimentScaleOp(String key, double fallback) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("key must not be blank");
        }
        this.key = key;
        this.fallback = fallback;
    }

    @Override
    public String operatorKey() {
        return "exp_scale:" + key + ":" + fallback;
    }

    @Override
    public int arity() {
        return 1;
    }

    @Override
    public Tensor apply(Tensor... inputs) {
        return Compute.mulScalar(inputs[0], fallback);
    }

    @Override
    public Tensor apply(ColumnOperatorContext context, Tensor... inputs) {
        return Compute.mulScalar(inputs[0],
                context.experiment().param(key, fallback));
    }

    public String key() {
        return key;
    }

    public double fallback() {
        return fallback;
    }
}
