// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.recsys.module;

import io.lark.column.biz.ColumnComponent;
import io.lark.column.biz.ColumnModule;
import io.lark.column.biz.ColumnStream;
import io.lark.examples.recsys.node.RankingWeightNode;
import io.lark.examples.recsys.schema.RecSysMetrics;

/** Combines funnel and commercial metrics into a ranking weight. */
@ColumnComponent
public final class RankingWeightModule extends ColumnModule {

    private final RankingWeightNode weight;

    public RankingWeightModule(FunnelMetricModule funnel) {
        weight = node(new RankingWeightNode(
                funnel.published(RecSysMetrics.CTR),
                funnel.published(RecSysMetrics.CVR),
                funnel.published(RecSysMetrics.GMV),
                funnel.published(RecSysMetrics.COUPON_ROI),
                funnel.published(RecSysMetrics.BUYAGAIN_COST)));
        dependsOn(FunnelMetricModule.class);
        fetch(weight.out());
    }

    public ColumnStream<Double> weight() {
        return weight.out();
    }
}
