// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.metric;

/**
 * The monitoring abstraction shared by every LARK module.
 *
 * <p>Implementations may be called from any worker thread and must be
 * thread-safe.
 */
@FunctionalInterface
public interface Monitor {

    void emit(Event event);
}
