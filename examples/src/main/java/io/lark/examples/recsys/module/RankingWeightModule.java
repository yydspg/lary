// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.recsys.module;

import io.lark.column.biz.ColumnModule;
import io.lark.column.biz.ColumnStream;
import io.lark.examples.recsys.node.RankingWeightNode;

/** Combines funnel and commercial metrics into a ranking weight. */
public final class RankingWeightModule extends ColumnModule {

    private final RankingWeightNode weight;

    public RankingWeightModule(FunnelMetricModule funnel) {
        weight = add(new RankingWeightNode(
                funnel.ctr(), funnel.cvr(), funnel.gmv(),
                funnel.couponRoi(), funnel.buyAgainCost()));
        dependsOn(FunnelMetricModule.class);
        output(weight.out());
    }

    public ColumnStream<Double> weight() {
        return weight.out();
    }
}
