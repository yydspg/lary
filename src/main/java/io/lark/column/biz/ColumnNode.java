// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import java.time.Duration;
import java.util.List;

/**
 * Business unit of columnar computation.
 *
 * <p>Business code subclasses this and implements only {@link #name()},
 * {@link #inputs()}, {@link #outputs()} and {@link #execute(ColumnContext)}.
 * The framework compiles each business node into a hidden
 * {@code io.lark.column.exec.ComputeNode} execution node, derives dependency
 * edges from column names and runs the resulting graph.
 *
 * <p>Inputs that are produced by another registered business node create an
 * implicit dependency edge; inputs without a producer are treated as feed
 * columns. {@link #fallback} gives the node a chance to produce a degraded
 * result when execution fails.
 */
public abstract class ColumnNode {

    /** Unique business node name. */
    public abstract String name();

    /** Columns this node reads. */
    public List<String> inputs() {
        return List.of();
    }

    /** Columns this node writes. */
    public List<String> outputs() {
        return List.of();
    }

    /** Business computation. Blocking calls are fine. */
    public abstract void execute(ColumnContext ctx);

    /**
     * Optional degradation hook invoked when {@link #execute} fails.
     *
     * @return {@code true} if a usable degraded result was written into the
     *     context
     */
    public boolean fallback(ColumnContext ctx, Throwable error) {
        return false;
    }

    /** Optional per-node deadline (reserved for the execution deadline layer). */
    public Duration timeout() {
        return Duration.ZERO;
    }
}
