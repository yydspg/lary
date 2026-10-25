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
 * <p>Layering:
 * <pre>
 *   business : ColumnNode / NodePipeline
 *   execution: ComputeNode / ComputeGraph
 *   tensor op: OpTensor + FloatAddTensor / FloatMultiTensor / ...
 *   compute  : Compute / VectorOps (Vector API kernels)
 *   data     : Tensor / TensorStore
 * </pre>
 */
public abstract class OpTensor {

    private final String opType;
    private final List<String> inputs;
    private final List<String> outputs;

    protected OpTensor(String opType, List<String> inputs, List<String> outputs) {
        this.opType = Objects.requireNonNull(opType, "opType");
        this.inputs = inputs == null ? List.of() : List.copyOf(inputs);
        this.outputs = outputs == null ? List.of() : List.copyOf(outputs);
    }

    public String opType() {
        return opType;
    }

    public List<String> inputs() {
        return inputs;
    }

    public List<String> outputs() {
        return outputs;
    }

    /** Execute this tensor operation against the shared store. */
    public abstract void compute(OpContext ctx);
}
