// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.search.module;

import io.lark.column.biz.ColumnComponent;
import io.lark.column.biz.ColumnModule;
import io.lark.column.biz.ColumnStream;
import io.lark.examples.search.node.SearchOps;
import io.lark.examples.search.schema.SearchMetrics;

/** Same-item rebuy loss: {@code 1 - buyagain}. */
@ColumnComponent
public final class RebuySameLossModule extends ColumnModule {

    private final BuyAgainModule buyAgain;
    private ColumnStream<Double> loss;

    public RebuySameLossModule(BuyAgainModule buyAgain) {
        this.buyAgain = buyAgain;
    }

    @Override
    protected void define() {
        loss = node(SearchOps.oneMinus(SearchMetrics.REBUY_SAME_LOSS),
                buyAgain.published(SearchMetrics.BUYAGAIN));
        dependsOn(BuyAgainModule.class);
        output(loss);
    }

    public ColumnStream<Double> rebuySameLoss() {
        return loss;
    }
}
