// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.dag;

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
     * Business logic. Reads inputs from / writes outputs to the shared
     * context. A thrown exception marks this op failed but does not prevent
     * dependents from running.
     */
    public abstract void execute(IContext data);
}
