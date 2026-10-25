// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.dag;

/**
 * Cross-cutting hooks invoked by {@link OpExecutor} around every op.
 *
 * <ul>
 *   <li>{@link #shouldSkip} — return {@code true} to automatically skip this
 *       op for this run (condition-based skip; the op itself stays untouched).
 *   <li>{@link #onBefore} — run before the op executes.
 *   <li>{@link #onAfter} — run after the op completes, with its final status.
 * </ul>
 */
public interface OpAspect {

    default boolean shouldSkip(Op op, IContext data) {
        return false;
    }

    default void onBefore(Op op, IContext data) {
    }

    default void onAfter(Op op, IContext data, OpStatus status) {
    }
}
