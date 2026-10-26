// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import io.lark.column.compute.Compute;
import io.lark.column.tensor.DType;
import io.lark.column.tensor.Tensor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Business-level arithmetic ops.
 *
 * <p>Modules wire {@link ColumnStream}s through these factories; no tensor
 * shape, dtype or kernel type appears in business node definitions:
 *
 * <pre>{@code
 * ColumnStream<Double> ctcvr = node(ColumnOps.mul(CTCVR), ctr, cvr);
 * }</pre>
 */
public final class ColumnOps {

    private ColumnOps() {
    }

    public static ColumnOp<Double> add(Column<Double> output) {
        return new ColumnOp<>(output, ctx -> ctx.output(asDouble(
                Compute.add(ctx.input(0), ctx.input(1)))));
    }

    public static ColumnOp<Double> sub(Column<Double> output) {
        return new ColumnOp<>(output, ctx -> ctx.output(asDouble(
                Compute.sub(ctx.input(0), ctx.input(1)))));
    }

    public static ColumnOp<Double> mul(Column<Double> output) {
        return new ColumnOp<>(output, ctx -> ctx.output(asDouble(
                Compute.mul(ctx.input(0), ctx.input(1)))));
    }

    /** Division with a defined zero result for a zero denominator. */
    public static ColumnOp<Double> ratio(Column<Double> output) {
        return new ColumnOp<>(output, ctx -> ctx.output(
                safeDivide(ctx.input(0), ctx.input(1))));
    }

    /** {@code input * scalar + original} using three streams. */
    public static ColumnOp<Double> mulAdd(Column<Double> output) {
        return new ColumnOp<>(output, ctx -> ctx.output(asDouble(
                Compute.mulAdd(ctx.input(0), ctx.input(1), ctx.input(2)))));
    }

    public static ColumnOp<Double> oneMinus(Column<Double> output) {
        return new ColumnOp<>(output, ctx -> ctx.output(asDouble(
                Compute.addScalar(Compute.neg(ctx.input(0)), 1.0))));
    }

    public static ColumnOp<Double> scale(Column<Double> output, double divisor) {
        if (divisor == 0.0) {
            throw new IllegalArgumentException("divisor must not be zero");
        }
        return new ColumnOp<>(output, ctx -> ctx.output(
                Compute.divScalar(ctx.input(0), divisor)));
    }

    /**
     * Select the top {@code k} scores in each PV/group.
     *
     * <p>Inputs are {@code (score, groupId)}; output is an INT64 flag column
     * with the same shape. Grouping is the PV-level integration step used by a
     * ranking module before fetching rows.
     */
    public static ColumnOp<Long> topK(Column<Long> output, int k) {
        if (k <= 0) {
            throw new IllegalArgumentException("topK must be positive");
        }
        return new ColumnOp<>(output, ctx -> {
            Tensor scoreTensor = ctx.input(0);
            Tensor groupTensor = ctx.input(1);
            if (!scoreTensor.shape().equals(groupTensor.shape())) {
                throw new IllegalArgumentException("topK: shape mismatch");
            }
            Tensor scores = scoreTensor.dtype() == DType.FLOAT64
                    ? scoreTensor : scoreTensor.castTo(DType.FLOAT64);
            Tensor groups = groupTensor.dtype() == DType.INT64
                    ? groupTensor : groupTensor.castTo(DType.INT64);
            double[] values = scores.doubleData();
            long[] groupIds = groups.longData();
            Map<Long, List<Integer>> buckets = new HashMap<>();
            for (int i = 0; i < values.length; i++) {
                buckets.computeIfAbsent(groupIds[i], ignored -> new ArrayList<>())
                        .add(i);
            }
            long[] selected = new long[values.length];
            for (List<Integer> indices : buckets.values()) {
                indices.sort(Comparator
                        .<Integer>comparingDouble(index -> values[index])
                        .reversed()
                        .thenComparingInt(Integer::intValue));
                int limit = Math.min(k, indices.size());
                for (int i = 0; i < limit; i++) {
                    selected[indices.get(i)] = 1L;
                }
            }
            ctx.output(Tensor.fromLongs(scoreTensor.shape(), selected));
        });
    }

    private static Tensor asDouble(Tensor tensor) {
        return tensor.dtype() == DType.FLOAT64
                ? tensor : tensor.castTo(DType.FLOAT64);
    }

    private static Tensor safeDivide(Tensor numerator, Tensor denominator) {
        Tensor result = Compute.div(numerator, denominator);
        Tensor zero = Tensor.zeros(result.shape(), DType.FLOAT64);
        return Compute.select(Compute.eqScalar(denominator, 0.0), zero, result);
    }
}
