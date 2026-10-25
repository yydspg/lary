// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.dag;

/**
 * Per-node execution record captured by {@link StatsCollector}.
 *
 * @param id node id
 * @param type registered node type
 * @param status terminal status
 * @param elapsedNanos execution time
 * @param startedAtNanos offset from the run start (for waterfall charts)
 */
public record NodeRunStats(String id, String type, NodeStatus status,
                           long elapsedNanos, long startedAtNanos) {
}
