// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.demo.model;

import io.lark.rank.RankingResult;

/** Complete output of one end-to-end demo run. */
public record DemoResult(Prepared prepared, ColumnStage column,
                         RankingResult<ScoredGoods> greedy,
                         RankingResult<ScoredGoods> beam) {
}
