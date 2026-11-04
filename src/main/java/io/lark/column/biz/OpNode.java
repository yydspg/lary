// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import io.lark.column.ColumnExecutionException;
import io.lark.column.tensor.FallibleTensorKernel;
import io.lark.column.tensor.KernelContext;
import io.lark.column.tensor.Tensor;
import io.lark.column.tensor.TensorKernel;


/**
 * Internal node bound to one declarative operator.
 *
 * <p>Modules define nodes through
 * {@link ColumnModule#node(Column, ColumnOperator, ColumnStream...)} and see
 * only typed streams. The node resolves its bound input tensors, invokes the
 * operator kernel, and writes the single output.
 */
final class OpNode<O> extends ModuleNode<O> {

    private final TensorKernel kernel;

    OpNode(Column<O> output, ColumnOperator operator,
           ColumnStream<?>... inputs) {
        super(output, inputs);
        this.kernel = new OperatorKernel(output.dtype(), operator);
    }

    /**
     * Execution-layer kernel for this operator node. The compiler binds the
     * kernel directly to the {@code ComputeNode}, avoiding the extra
     * {@link ColumnContext} and {@link KernelContext} round-trip on the hot
     * path.
     */
    TensorKernel executionKernel() {
        return kernel;
    }

    @Override
    public void compute(ColumnContext ctx) {
        ColumnStream<?>[] feeds = feedView();
        Tensor[] inputs = new Tensor[feeds.length];
        for (int i = 0; i < feeds.length; i++) {
            inputs[i] = ctx.read(feeds[i]);
        }

        Tensor[] result = new Tensor[1];
        try {
            kernel.compute(new KernelContext(ctx.store(), inputs,
                    value -> result[0] = value, ctx.experiment(),
                    ctx.runContext()));
        } catch (ColumnExecutionException failure) {
            throw failure;
        } catch (RuntimeException failure) {
            throw new ColumnExecutionException(
                    "OpNode " + id() + " failed", failure);
        }
        if (result[0] == null) {
            throw new ColumnExecutionException(
                    "OpNode " + id() + ": kernel produced no output");
        }
        ctx.write(out(), result[0]);
    }

    @Override
    public boolean fallback(ColumnContext ctx, Throwable error) {
        if (kernel instanceof FallibleTensorKernel fallible) {
            return fallible.fallback(
                    new KernelContext(ctx.store(), new Tensor[0], null,
                            ctx.experiment(), ctx.runContext()), error);
        }
        return false;
    }
}
