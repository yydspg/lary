// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.dag;

import java.time.Duration;
import java.util.Objects;

/**
 * Timeout / cancellation policy shared by the Node and Op executors.
 *
 * <p>A policy is disabled by default, preserving the original unbounded
 * behavior. Enable it per executor and optionally override the timeout on an
 * individual {@link Node} or {@link Op}.
 *
 * <ul>
 *   <li>{@code defaultTimeout} — per-node/op limit; zero disables.
 *   <li>{@code graphTimeout} — whole-run deadline; zero disables.
 *   <li>{@code maxConsecutiveTimeouts} — threshold for repeated-timeout
 *       detection; {@code <= 0} disables quarantine.
 *   <li>{@code quarantineDuration} — how long a repeatedly-timing-out key is
 *       skipped; zero means until {@link TimeoutTracker#reset} is called.
 *   <li>{@code repeatedAction} — quarantine the op or abort the current graph.
 *   <li>{@code interruptOnTimeout} — interrupt the timed-out worker thread.
 * </ul>
 */
public record TimeoutPolicy(
        Duration defaultTimeout,
        Duration graphTimeout,
        int maxConsecutiveTimeouts,
        Duration quarantineDuration,
        TimeoutAction repeatedAction,
        boolean interruptOnTimeout
) {

    public TimeoutPolicy {
        defaultTimeout = nonNegative(defaultTimeout, "defaultTimeout");
        graphTimeout = nonNegative(graphTimeout, "graphTimeout");
        quarantineDuration = nonNegative(quarantineDuration, "quarantineDuration");
        repeatedAction = repeatedAction == null
                ? TimeoutAction.QUARANTINE : repeatedAction;
        if (maxConsecutiveTimeouts < 0) {
            throw new IllegalArgumentException("maxConsecutiveTimeouts must be >= 0");
        }
    }

    /** No timeout, no quarantine, no cancellation. */
    public static TimeoutPolicy disabled() {
        return new TimeoutPolicy(Duration.ZERO, Duration.ZERO, 0,
                Duration.ZERO, TimeoutAction.QUARANTINE, true);
    }

    /** Per-node/op timeout with the default detection settings. */
    public static TimeoutPolicy of(Duration timeout) {
        return builder().defaultTimeout(timeout).build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean timeoutEnabled() {
        return !defaultTimeout.isZero() || !graphTimeout.isZero();
    }

    public boolean quarantineEnabled() {
        return maxConsecutiveTimeouts > 0;
    }

    public static final class Builder {
        private Duration defaultTimeout = Duration.ZERO;
        private Duration graphTimeout = Duration.ZERO;
        private int maxConsecutiveTimeouts = 3;
        private Duration quarantineDuration = Duration.ofSeconds(30);
        private TimeoutAction repeatedAction = TimeoutAction.QUARANTINE;
        private boolean interruptOnTimeout = true;

        public Builder defaultTimeout(Duration value) {
            this.defaultTimeout = value;
            return this;
        }

        public Builder graphTimeout(Duration value) {
            this.graphTimeout = value;
            return this;
        }

        public Builder maxConsecutiveTimeouts(int value) {
            this.maxConsecutiveTimeouts = value;
            return this;
        }

        public Builder quarantineDuration(Duration value) {
            this.quarantineDuration = value;
            return this;
        }

        public Builder repeatedAction(TimeoutAction value) {
            this.repeatedAction = value;
            return this;
        }

        public Builder interruptOnTimeout(boolean value) {
            this.interruptOnTimeout = value;
            return this;
        }

        public TimeoutPolicy build() {
            return new TimeoutPolicy(defaultTimeout, graphTimeout,
                    maxConsecutiveTimeouts, quarantineDuration,
                    repeatedAction, interruptOnTimeout);
        }
    }

    private static Duration nonNegative(Duration value, String name) {
        Duration result = Objects.requireNonNull(value, name);
        if (result.isNegative()) {
            throw new IllegalArgumentException(name + " must not be negative");
        }
        return result;
    }
}
