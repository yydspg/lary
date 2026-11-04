// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import io.lark.column.ColumnExecutionException;
import io.lark.column.tensor.DType;
import io.lark.column.tensor.KernelContext;
import io.lark.column.tensor.Tensor;
import io.lark.column.tensor.TensorKernel;

import java.util.Objects;

/**
 * Adapter from a declarative {@link ColumnOperator} to the tensor kernel layer.
 *
 * <p>The operator owns the math; this adapter resolves the declared output
 * dtype and adapts the runtime context. Operators never see graph metadata.
 */
final class OperatorKernel implements TensorKernel {

    private final ColumnOperator operator;
    private final DType outputDtype;

    OperatorKernel(DType outputDtype, ColumnOperator operator) {
        this.outputDtype = Objects.requireNonNull(outputDtype, "outputDtype");
        this.operator = Objects.requireNonNull(operator, "operator");
    }

    @Override
    public void compute(KernelContext ctx) {
        Tensor[] inputs = new Tensor[ctx.inputCount()];
        for (int i = 0; i < inputs.length; i++) {
            inputs[i] = ctx.input(i);
        }
        Tensor result;
        try {
            result = operator.apply(
                    new ColumnOperatorContext(ctx.experiment(), ctx.runContext()),
                    inputs);
        } catch (ColumnExecutionException failure) {
            throw failure;
        } catch (RuntimeException failure) {
            throw new ColumnExecutionException(
                    "Column operator " + operator.operatorName()
                            + " failed", failure);
        }
        if (result == null) {
            throw new ColumnExecutionException(
                    "Column operator " + operator.operatorName()
                            + " produced no output");
        }
        try {
            ctx.output(result.dtype() == outputDtype
                    ? result : result.castTo(outputDtype));
        } catch (RuntimeException failure) {
            throw new ColumnExecutionException(
                    "Column operator " + operator.operatorName()
                            + " produced " + result.dtype()
                            + ", expected " + outputDtype, failure);
        }
    }
}
