// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.search.node;

import io.lark.column.Tensor;
import io.lark.column.TensorShape;
import io.lark.column.biz.ColumnContext;
import io.lark.column.biz.ColumnNode;
import io.lark.column.biz.ColumnStream;
import io.lark.examples.search.schema.SearchMetrics;

/**
 * Produces a 3-D tensor {@code [row, signal, delayed]}.
 *
 * <p>The middle axis is 0 = orders and 1 = GMV. The last axis is
 * 0 = on-time share and 1 = delayed share. This demonstrates a 3-D result
 * without changing the 1-D business column API.
 */
public final class DelayNode extends ColumnNode {

    private final ColumnStream<Long> delayedOrders;
    private final ColumnStream<Long> orders;
    private final ColumnStream<Long> delayedGmvFen;
    private final ColumnStream<Long> gmvFen;
    private final ColumnStream<Double> out = fetch(SearchMetrics.DELAY);

    public DelayNode(ColumnStream<Long> delayedOrders,
                     ColumnStream<Long> orders,
                     ColumnStream<Long> delayedGmvFen,
                     ColumnStream<Long> gmvFen) {
        super(delayedOrders, orders, delayedGmvFen, gmvFen);
        this.delayedOrders = delayedOrders;
        this.orders = orders;
        this.delayedGmvFen = delayedGmvFen;
        this.gmvFen = gmvFen;
    }

    public ColumnStream<Double> out() {
        return out;
    }

    @Override
    public void compute(ColumnContext ctx) {
        Tensor orderDelay = Ratios.divide(ctx.read(delayedOrders), ctx.read(orders));
        Tensor gmvDelay = Ratios.divide(ctx.read(delayedGmvFen), ctx.read(gmvFen));

        int rows = orderDelay.size();
        double[] order = orderDelay.doubleData();
        double[] gmv = gmvDelay.doubleData();
        double[] values = new double[rows * 4];
        for (int i = 0; i < rows; i++) {
            values[i * 4] = 1.0 - order[i];
            values[i * 4 + 1] = order[i];
            values[i * 4 + 2] = 1.0 - gmv[i];
            values[i * 4 + 3] = gmv[i];
        }
        ctx.write(out, Tensor.fromDoubles(TensorShape.of(rows, 2, 2), values));
    }
}
