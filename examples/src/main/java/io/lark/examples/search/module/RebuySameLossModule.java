// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.search.module;

import io.lark.column.biz.ColumnModule;
import io.lark.column.biz.ColumnStream;
import io.lark.examples.search.node.OneMinusNode;
import io.lark.examples.search.schema.SearchMetrics;

/** Same-item rebuy loss: {@code 1 - buyagain}. */
public final class RebuySameLossModule extends ColumnModule {

    private final OneMinusNode loss;

    public RebuySameLossModule(BuyAgainModule buyAgain) {
        loss = node(new OneMinusNode(SearchMetrics.REBUY_SAME_LOSS),
                buyAgain.published(SearchMetrics.BUYAGAIN));
        dependsOn(BuyAgainModule.class);
        output(loss.out());
    }

    public ColumnStream<Double> rebuySameLoss() {
        return loss.out();
    }
}
