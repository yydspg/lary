// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.demo.model;

/** Request-scoped ranking configuration. */
public record RankConfig(int topK, double budget, int beamWidth) {

    public RankConfig {
        if (topK <= 0) {
            throw new IllegalArgumentException("topK must be positive");
        }
        if (budget < 0) {
            throw new IllegalArgumentException("budget must be non-negative");
        }
        if (beamWidth <= 0) {
            throw new IllegalArgumentException("beamWidth must be positive");
        }
    }
}
