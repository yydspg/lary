// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.dag;

import java.util.Locale;

/** Lifecycle state of a node within a single graph execution. */
public enum NodeStatus {
    /** Not started. */
    PENDING,
    /** Executing. */
    RUNNING,
    /** Completed normally. */
    SUCCESS,
    /** Threw and had no (or a failed) fallback. */
    FAILED,
    /** Threw but {@link Node#fallback} produced a degraded result. */
    FALLBACK,
    /** Explicitly disabled for this run (batch-disable). */
    SKIPPED;

    /** Human-readable name used in logs / monitoring. */
    public String displayName() {
        return name().toLowerCase(Locale.ROOT);
    }

    @Override
    public String toString() {
        return displayName();
    }
}
