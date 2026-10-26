// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import io.lark.column.tensor.FallibleTensorKernel;
import io.lark.column.tensor.KernelContext;
import io.lark.column.tensor.Tensor;
import io.lark.column.tensor.TensorKernel;


/**
 * Internal node bound to exactly one business {@link ColumnOp}.
 *
 * <p>Modules define nodes through
 * {@link ColumnModule#node(ColumnOp, ColumnStream...)} and see only typed
 * streams:
 *
 * <pre>{@code
 * ColumnStream<Double> ctcvr = node(ColumnOps.mul(CTCVR), ctr, cvr);
 * }</pre>
 *
 * <p>The node itself contains no formula: it resolves its bound input tensors,
 * invokes the op kernel, and writes the single output.
 */
final class OpNode<O> extends ModuleNode<O> {

    private final TensorKernel kernel;

    OpNode(ColumnOp<O> op, ColumnStream<?>... inputs) {
        super(op.output(), inputs);
        this.kernel = op.kernel();
    }

    @Override
    public void compute(ColumnContext ctx) {
        ColumnStream<?>[] feeds = feedView();
        Tensor[] inputs = new Tensor[feeds.length];
        for (int i = 0; i < feeds.length; i++) {
            inputs[i] = ctx.read(feeds[i]);
        }

        Tensor[] result = new Tensor[1];
        kernel.compute(new KernelContext(ctx.store(), inputs,
                value -> result[0] = value, ctx.experiment()));
        if (result[0] == null) {
            throw new IllegalStateException(
                    "OpNode " + id() + ": kernel produced no output");
        }
        ctx.write(out(), result[0]);
    }

    @Override
    public boolean fallback(ColumnContext ctx, Throwable error) {
        if (kernel instanceof FallibleTensorKernel fallible) {
            return fallible.fallback(
                    new KernelContext(ctx.store(), new Tensor[0], null,
                            ctx.experiment()), error);
        }
        return false;
    }
}
