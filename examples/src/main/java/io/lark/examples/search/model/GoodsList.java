// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.search.model;

import java.util.List;

/** Immutable candidate list for one request. */
public record GoodsList(List<GoodsDO> goods) {

    public GoodsList {
        goods = List.copyOf(goods);
    }

    public int size() {
        return goods.size();
    }
}
