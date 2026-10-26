// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.op;

import java.util.List;
import java.util.Objects;

/**
 * Legacy tensor operation adapter.
 *
 * <p>New code separates graph construction from execution:
 * {@link OpSpec} describes the graph op and
 * {@link io.lark.column.kernel.TensorKernel} contains computation logic.
 * {@code ComputeNode} binds the two. This class is retained so existing
 * {@code FloatAddTensor}/{@code TensorOps} code keeps compiling; it still
 * combines metadata and a legacy {@code compute} method.
 *
 * <p>Prefer {@code OpSpec} + {@code TensorKernels.create(spec)} for new code.
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
