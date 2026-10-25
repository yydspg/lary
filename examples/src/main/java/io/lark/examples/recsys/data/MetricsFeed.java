// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.recsys.data;

import io.lark.column.Row;
import io.lark.column.biz.ColumnStream;
import io.lark.column.biz.FeedStream;
import io.lark.examples.recsys.module.FunnelMetricModule;

import java.util.List;

/** Builds a feed request from the typed FunnelMetricModule columns. */
public final class MetricsFeed {

    private MetricsFeed() {
    }

    /** Six sample item-level metric rows used by the demo. */
    public static FeedStream sample(FunnelMetricModule metrics) {
        return of(metrics, MetricsRepository.rows());
    }

    /** Feed arbitrary rows; useful for tests and edge-case fixtures. */
    public static FeedStream of(FunnelMetricModule metrics, List<Row> rows) {
        return FeedStream.of(new ColumnStream<?>[]{
                metrics.impressions(),
                metrics.clicks(),
                metrics.orders(),
                metrics.gmvFen(),
                metrics.couponCostFen(),
                metrics.buyAgainOrders()
        }, rows);
    }
}
