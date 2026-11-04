// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.search.model;

/** Search context plus the candidate goods for one request. */
public record SearchRequest(SearchContext context, GoodsList goods) {
}
