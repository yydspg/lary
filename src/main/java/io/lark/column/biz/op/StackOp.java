// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz.op;

import io.lark.column.biz.ColumnOperator;
import io.lark.column.tensor.DType;
import io.lark.column.tensor.Tensor;
import io.lark.column.tensor.TensorShape;

/**
 * Stack 1-D columns into one shaped tensor with a leading row dimension.
 *
 * <p>{@code new StackOp(3)} turns three 1-D columns into {@code [rows, 3]};
 * {@code new StackOp(2, 2)} turns four 1-D columns into {@code [rows, 2, 2]}.
 * The business graph only wires columns; shape and packing stay in the
 * framework operator.
 */
public final class StackOp implements ColumnOperator {

    private final int[] featureShape;
    private final int inputs;

    public StackOp(int... featureShape) {
        if (featureShape == null || featureShape.length == 0) {
            throw new IllegalArgumentException(
                    "StackOp needs at least one feature dimension");
        }
        if (featureShape.length > TensorShape.MAX_RANK - 1) {
            throw new IllegalArgumentException(
                    "StackOp supports at most "
                            + (TensorShape.MAX_RANK - 1)
                            + " feature dimensions");
        }
        int count = 1;
        for (int dimension : featureShape) {
            if (dimension <= 0) {
                throw new IllegalArgumentException(
                        "StackOp dimensions must be positive");
            }
            count = Math.multiplyExact(count, dimension);
        }
        this.featureShape = featureShape.clone();
        this.inputs = count;
    }

    @Override
    public int arity() {
        return inputs;
    }

    @Override
    public Tensor apply(Tensor... values) {
        int rows = values[0].size();
        for (Tensor value : values) {
            if (value.shape().rank() != 1 || value.size() != rows) {
                throw new IllegalArgumentException(
                        "StackOp inputs must be 1-D columns of equal length");
            }
        }

        double[][] columns = new double[inputs][];
        for (int i = 0; i < inputs; i++) {
            Tensor value = values[i];
            columns[i] = (value.dtype() == DType.FLOAT64
                    ? value : value.castTo(DType.FLOAT64)).doubleData();
        }

        int[] shape = new int[featureShape.length + 1];
        shape[0] = rows;
        System.arraycopy(featureShape, 0, shape, 1, featureShape.length);
        double[] packed = new double[rows * inputs];
        for (int row = 0; row < rows; row++) {
            int base = row * inputs;
            for (int i = 0; i < inputs; i++) {
                packed[base + i] = columns[i][row];
            }
        }
        return Tensor.fromDoubles(TensorShape.of(shape), packed);
    }
}
