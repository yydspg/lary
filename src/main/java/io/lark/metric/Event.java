// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.metric;

import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * A generic, transport/domain-agnostic observation.
 *
 * <p>Mirrors {@code lark::metric::Event} from the C++ framework:
 *
 * <ul>
 *   <li>{@code source} — originating module: "dag", "column", "rpc", ...
 *   <li>{@code action} — verb: "node.success", "op.end", "cache.hit", ...
 *   <li>{@code subject} — primary target: node id, cache key, method name, ...
 *   <li>{@code attrs} — flexible string key/value details
 *   <li>{@code durationNanos} — elapsed time for the action (0 when not timed)
 *   <li>{@code ok} — whether the action succeeded
 * </ul>
 */
public final class Event {

    private final String source;
    private final String action;
    private final String subject;
    private final Map<String, String> attrs = new LinkedHashMap<>();
    private final Instant timestamp;
    private long durationNanos;
    private boolean ok = true;

    public Event(String source, String action, String subject) {
        this.source = Objects.requireNonNull(source, "source");
        this.action = Objects.requireNonNull(action, "action");
        this.subject = Objects.requireNonNull(subject, "subject");
        this.timestamp = Instant.now();
    }

    /** Fluent attribute builder (mirrors {@code Event::attr}). */
    public Event attr(String key, String value) {
        attrs.put(Objects.requireNonNull(key, "key"), Objects.requireNonNull(value, "value"));
        return this;
    }

    /** Fluent nanosecond attribute builder (mirrors {@code Event::attr_ns}). */
    public Event attrNanos(String key, long nanos) {
        return attr(key, Long.toString(nanos));
    }

    public Event durationNanos(long durationNanos) {
        this.durationNanos = durationNanos;
        return this;
    }

    public Event ok(boolean ok) {
        this.ok = ok;
        return this;
    }

    public String source() {
        return source;
    }

    public String action() {
        return action;
    }

    public String subject() {
        return subject;
    }

    /** Look up an attribute; empty when absent. */
    public Optional<String> get(String key) {
        return Optional.ofNullable(attrs.get(key));
    }

    public Map<String, String> attrs() {
        return Collections.unmodifiableMap(attrs);
    }

    public long durationNanos() {
        return durationNanos;
    }

    public boolean ok() {
        return ok;
    }

    public Instant timestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "Event{" + source + ", " + action + ", " + subject
                + ", attrs=" + attrs
                + ", durationNanos=" + durationNanos
                + ", ok=" + ok + '}';
    }
}
