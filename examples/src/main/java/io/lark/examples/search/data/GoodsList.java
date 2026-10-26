// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.search.data;

import java.util.List;
import java.util.Objects;

/** Immutable goods result list for one PV. */
public record GoodsList(List<GoodsDO> goods) {

    public GoodsList {
        goods = List.copyOf(Objects.requireNonNull(goods, "goods"));
    }

    public int size() {
        return goods.size();
    }
}
