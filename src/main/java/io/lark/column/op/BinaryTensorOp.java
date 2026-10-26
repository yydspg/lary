// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.op;

import io.lark.column.tensor.DType;
import io.lark.column.tensor.Tensor;

import java.util.List;

/**
 * Base class for typed element-wise binary tensor operations.
 *
 * <p>Concrete classes such as {@link FloatAddTensor} or
 * {@link FloatMultiTensor} declare their working dtype. Inputs are cast to
 * that dtype before the kernel runs.
 */
public abstract class BinaryTensorOp extends OpTensor {

    private final DType dtype;

    protected BinaryTensorOp(String opType, List<String> inputs,
                             List<String> outputs, DType dtype) {
        this(opType,
                inputs == null ? new String[0] : inputs.toArray(String[]::new),
                outputs == null ? new String[0] : outputs.toArray(String[]::new),
                dtype);
    }

    protected BinaryTensorOp(String opType, String[] inputs,
                             String[] outputs, DType dtype) {
        super(opType, inputs, outputs);
        if (inputs == null || inputs.length != 2) {
            throw new IllegalArgumentException(
                    opType + " requires 2 inputs, got "
                            + (inputs == null ? 0 : inputs.length));
        }
        this.dtype = dtype;
    }

    public DType dtype() {
        return dtype;
    }

    @Override
    public final void compute(OpContext ctx) {
        Tensor a = ctx.input(inputs()[0]);
        Tensor b = ctx.input(inputs()[1]);
        if (!a.shape().isBroadcastCompatible(b.shape())) {
            throw new IllegalArgumentException(
                    opType() + ": shapes are not broadcast-compatible ("
                            + a.shape() + " vs " + b.shape() + ")");
        }
        Tensor left = a.dtype() == dtype ? a : a.castTo(dtype);
        Tensor right = b.dtype() == dtype ? b : b.castTo(dtype);
        ctx.set(outputs()[0], apply(left, right));
    }

    /** Apply the typed kernel; both inputs are already {@link #dtype()}. */
    protected abstract Tensor apply(Tensor a, Tensor b);
}
