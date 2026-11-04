// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz.operator;

import io.lark.column.tensor.DType;
import io.lark.column.tensor.Tensor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Select the top {@code k} scores in each group.
 *
 * <p>Inputs are {@code (score, groupId)}; the output is an INT64 flag column
 * with the same shape. Parameterized: {@code node(SELECTED,
 * new TopKOp(k), scores, pvId)}.
 */
public final class TopKOp implements ColumnOperator {

    private final int k;

    public TopKOp(int k) {
        if (k <= 0) {
            throw new IllegalArgumentException("topK must be positive");
        }
        this.k = k;
    }

    @Override
    public String operatorKey() {
        return "topk:" + k;
    }

    @Override
    public int arity() {
        return 2;
    }

    @Override
    public Tensor apply(Tensor... inputs) {
        return select(inputs[0], inputs[1], k);
    }

    /** Stable group-wise top-k flag computation shared with legacy factories. */
    public static Tensor select(Tensor scoreTensor, Tensor groupTensor, int k) {
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
        return Tensor.fromLongs(scoreTensor.shape(), selected);
    }
}
