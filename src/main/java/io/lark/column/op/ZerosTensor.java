// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.op;

import io.lark.column.DType;
import io.lark.column.Tensor;
import io.lark.column.TensorShape;

import java.util.List;
import java.util.Objects;

/**
 * Creates a zero-filled tensor with an explicit dtype and shape.
 *
 * <p>Use {@link io.lark.column.biz.ZerosNode} when defining the same value as a
 * typed business source column.
 */
public final class ZerosTensor extends OpTensor {

    private final DType dtype;
    private final TensorShape shape;

    public ZerosTensor(List<String> outputs, DType dtype, TensorShape shape) {
        this(outputs == null ? new String[0] : outputs.toArray(String[]::new),
                dtype, shape);
    }

    public ZerosTensor(String[] outputs, DType dtype, TensorShape shape) {
        super("zeros", new String[0], outputs);
        if (outputs == null || outputs.length != 1) {
            throw new IllegalArgumentException(
                    "zeros requires exactly one output");
        }
        this.dtype = Objects.requireNonNull(dtype, "dtype");
        this.shape = Objects.requireNonNull(shape, "shape");
    }

    public DType dtype() {
        return dtype;
    }

    public TensorShape shape() {
        return shape;
    }

    @Override
    public void compute(OpContext ctx) {
        ctx.set(outputs()[0], Tensor.zeros(shape, dtype));
    }
}
