// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.search.data;

import java.util.Objects;

/** Search context + candidate goods list. */
public record SearchRequest(SearchContext context, GoodsList goods) {

    public SearchRequest {
        Objects.requireNonNull(context, "context");
        Objects.requireNonNull(goods, "goods");
    }
}
