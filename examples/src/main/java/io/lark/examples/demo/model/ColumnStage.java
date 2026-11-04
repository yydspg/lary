// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.demo.model;

import io.lark.column.monitor.RunStats;
import io.lark.column.tensor.Tensor;

import java.util.List;

/** Result of the column stage: selected candidates plus shaped outputs. */
public record ColumnStage(List<ScoredGoods> candidates, int selectedCount,
                          Tensor retargetAd, Tensor delay, RunStats stats) {

    public ColumnStage {
        candidates = List.copyOf(candidates);
    }
}
