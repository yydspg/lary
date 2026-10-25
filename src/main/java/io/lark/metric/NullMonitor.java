// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.metric;

/** Discards every event (production default). */
public final class NullMonitor implements Monitor {

    public static final NullMonitor INSTANCE = new NullMonitor();

    private NullMonitor() {
    }

    @Override
    public void emit(Event event) {
        // Intentionally empty.
    }
}
