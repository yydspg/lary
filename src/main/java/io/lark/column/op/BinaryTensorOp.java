// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.op;

import io.lark.column.DType;
import io.lark.column.Tensor;

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
        super(opType, inputs, outputs);
        if (inputs == null || inputs.size() != 2) {
            throw new IllegalArgumentException(
                    opType + " requires 2 inputs, got "
                            + (inputs == null ? 0 : inputs.size()));
        }
        this.dtype = dtype;
    }

    public DType dtype() {
        return dtype;
    }

    @Override
    public final void compute(OpContext ctx) {
        Tensor a = ctx.input(inputs().get(0));
        Tensor b = ctx.input(inputs().get(1));
        if (a.size() != b.size()) {
            throw new IllegalArgumentException(
                    opType() + ": length mismatch (" + a.size()
                            + " vs " + b.size() + ")");
        }
        Tensor left = a.dtype() == dtype ? a : a.castTo(dtype);
        Tensor right = b.dtype() == dtype ? b : b.castTo(dtype);
        ctx.set(outputs().get(0), apply(left, right));
    }

    /** Apply the typed kernel; both inputs are already {@link #dtype()}. */
    protected abstract Tensor apply(Tensor a, Tensor b);
}
