// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.metric;

import java.io.PrintStream;
import java.util.Objects;

/** Prints one human-readable line per event (development / debugging). */
public final class LoggingMonitor implements Monitor {

    private final PrintStream out;

    public LoggingMonitor() {
        this(System.out);
    }

    public LoggingMonitor(PrintStream out) {
        this.out = Objects.requireNonNull(out, "out");
    }

    @Override
    public synchronized void emit(Event event) {
        StringBuilder line = new StringBuilder()
                .append('[').append(event.source()).append("] ")
                .append(event.action()).append(' ')
                .append(event.subject());
        if (event.durationNanos() > 0) {
            line.append(" duration=").append(event.durationNanos()).append("ns");
        }
        line.append(" ok=").append(event.ok());
        if (!event.attrs().isEmpty()) {
            line.append(' ').append(event.attrs());
        }
        out.println(line);
    }
}
