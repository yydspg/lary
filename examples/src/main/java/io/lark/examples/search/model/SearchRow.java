// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.search.model;

/** One goods row plus the request/PV it belongs to. */
public record SearchRow(long pvId, GoodsDO goods) {
}
