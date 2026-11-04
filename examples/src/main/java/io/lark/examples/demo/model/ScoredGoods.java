// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.demo.model;

import io.lark.examples.search.model.GoodsDO;

/** One column-scored goods candidate for the ranking stages. */
public record ScoredGoods(GoodsDO goods, double baseScore, int category,
                          int brand, long cost, boolean restricted) {
}
