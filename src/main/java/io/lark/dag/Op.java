// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.dag;

import java.time.Duration;

/**
 * The business-facing unit of computation in the Op layer.
 *
 * <p>Business subclasses override only {@link #name()} and
 * {@link #execute(IContext)}; the framework schedules the virtual thread,
 * waits for dependencies, applies aspects and records timing. Blocking code is
 * fine — each op runs on its own virtual thread.
 */
public abstract class Op {

    /** Stable business name, e.g. {@code "fetch_user"}. */
    public abstract String name();

    /**
     * Per-op deadline. Zero (the default) means "use the executor's
     * {@link TimeoutPolicy}"; a positive value overrides it.
     */
    public Duration timeout() {
        return Duration.ZERO;
    }

    /**
     * Optional business fallback, invoked when {@link #execute} fails or times
     * out. Return {@code true} if a degraded result was written into the
     * context.
     */
    public boolean fallback(IContext data, Throwable error) {
        return false;
    }

    /**
     * Business logic. Reads inputs from / writes outputs to the shared
     * context. A thrown exception marks this op failed but does not prevent
     * dependents from running.
     */
    public abstract void execute(IContext data);
}
