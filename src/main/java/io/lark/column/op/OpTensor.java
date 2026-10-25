// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.op;

import java.util.List;
import java.util.Objects;

/**
 * Tensor operation layer.
 *
 * <p>An {@code OpTensor} is a concrete, typed operation over tensors — for
 * example {@link FloatAddTensor} or {@link FloatMultiTensor}. It is NOT an
 * execution-graph node: {@code io.lark.column.exec.ComputeNode} wraps an
 * {@code OpTensor} and handles scheduling/dependencies/status.
 *
 * <p>Inputs/outputs are stored as arrays because the arity is fixed once the
 * operation is constructed. This avoids list allocation/iteration on the hot
 * execution path.
 *
 * <p>Layering:
 * <pre>
 *   business : ColumnNode / ColumnModule / ColumnPipeline
 *   execution: ComputeNode / ComputeGraph
 *   tensor op: OpTensor + FloatAddTensor / FloatMultiTensor / ...
 *   compute  : Compute / VectorOps (Vector API kernels)
 *   data     : Tensor / TensorStore
 * </pre>
 */
public abstract class OpTensor {

    private final String opType;
    private final String[] inputs;
    private final String[] outputs;

    protected OpTensor(String opType, String[] inputs, String[] outputs) {
        this.opType = Objects.requireNonNull(opType, "opType");
        this.inputs = inputs == null ? new String[0] : inputs;
        this.outputs = outputs == null ? new String[0] : outputs;
    }

    protected OpTensor(String opType, List<String> inputs, List<String> outputs) {
        this(opType,
                inputs == null ? new String[0] : inputs.toArray(String[]::new),
                outputs == null ? new String[0] : outputs.toArray(String[]::new));
    }

    public String opType() {
        return opType;
    }

    public String[] inputs() {
        return inputs;
    }

    public String[] outputs() {
        return outputs;
    }

    /** Execute this tensor operation against the shared store. */
    public abstract void compute(OpContext ctx);
}
