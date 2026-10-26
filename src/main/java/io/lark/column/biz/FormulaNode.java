// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import io.lark.column.tensor.DType;
import io.lark.column.tensor.Tensor;
import io.lark.column.tensor.TensorShape;

/**
 * A single-output node that evaluates a typed {@link Expr} tree.
 *
 * <p>Formula expressions remain columnar: each expression node consumes
 * tensors and returns tensors, so experiment and condition branches are
 * evaluated with the same vectorized compute primitives as the rest of the
 * pipeline.
 */
public final class FormulaNode<O> extends ModuleNode<O> {

    private final Expr<O> expression;

    public FormulaNode(Column<O> output, Expr<O> expression) {
        super(output, expression.inputs().toArray(ColumnStream<?>[]::new));
        this.expression = expression;
        if (expression.dtype() != output.dtype()) {
            throw new IllegalArgumentException(
                    "FormulaNode " + id() + ": output '" + output.name()
                            + "' is " + output.dtype() + " but expression is "
                            + expression.dtype());
        }
    }

    @Override
    public void compute(ColumnContext ctx) {
        ColumnStream<?>[] feeds = feedView();
        TensorShape shape = feeds.length == 0
                ? TensorShape.vector(1)
                : ctx.read(feeds[0]).shape();
        Tensor result = expression.eval(new FormulaContext(ctx, shape));
        if (result.dtype() != out().dtype()) {
            result = result.castTo(out().dtype());
        }
        ctx.write(out(), result);
    }

    @Override
    public String toString() {
        return id() + " formula " + out();
    }
}
