// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.rank;

import java.util.Objects;

/**
 * Admission rule for one candidate at the current slot.
 *
 * <p>Rules may be stateful (sliding windows, quotas, already-selected sets).
 * The engine calls {@link #newSession()} once per {@link SequentialRanker#rank}
 * execution, then {@link #begin(RankingContext)} before slot 0 and
 * {@link #onSelected(RankingContext, RankedSlot)} after each filled slot. A
 * stateful rule should return a fresh instance from {@code newSession()} so
 * concurrent or repeated runs do not share state. Composites created with
 * {@link #all}, {@link #any} and {@link #not} propagate the same lifecycle to
 * their child rules.
 *
 * <p>Slot- and spacing-dependent rules can also inspect
 * {@link RankingContext#slot()}, {@link RankingContext#selected()} and
 * {@link RankingContext#gapSince(java.util.function.Predicate)}.
 */
@FunctionalInterface
public interface SlotAdmission<T, C> {

    boolean admit(RankingContext<T, C> context, T candidate);

    /**
     * Create the state holder for one ranking execution. Stateless rules return
     * {@code this}; stateful rules return a fresh instance.
     */
    default SlotAdmission<T, C> newSession() {
        return this;
    }

    /** Called once before the first slot for this execution. */
    default void begin(RankingContext<T, C> context) {
    }

    /** Called after a candidate fills a slot; update counters/windows here. */
    default void onSelected(RankingContext<T, C> context, RankedSlot<T> slot) {
    }

    static <T, C> SlotAdmission<T, C> always() {
        return (context, candidate) -> true;
    }

    /** All rules must admit (short-circuits on the first rejection). */
    @SafeVarargs
    static <T, C> SlotAdmission<T, C> all(SlotAdmission<T, C>... rules) {
        Objects.requireNonNull(rules, "rules");
        for (SlotAdmission<T, C> rule : rules) {
            Objects.requireNonNull(rule, "rule");
        }
        return new AllAdmission<>(rules);
    }

    /** Any rule may admit (short-circuits on the first acceptance). */
    @SafeVarargs
    static <T, C> SlotAdmission<T, C> any(SlotAdmission<T, C>... rules) {
        Objects.requireNonNull(rules, "rules");
        if (rules.length == 0) {
            throw new IllegalArgumentException("any() needs at least one rule");
        }
        for (SlotAdmission<T, C> rule : rules) {
            Objects.requireNonNull(rule, "rule");
        }
        return new AnyAdmission<>(rules);
    }

    static <T, C> SlotAdmission<T, C> not(SlotAdmission<T, C> rule) {
        return new NotAdmission<>(Objects.requireNonNull(rule, "rule"));
    }
}

final class AllAdmission<T, C> implements SlotAdmission<T, C> {

    private final SlotAdmission<T, C>[] rules;

    @SafeVarargs
    AllAdmission(SlotAdmission<T, C>... rules) {
        this.rules = rules.clone();
    }

    @Override
    public boolean admit(RankingContext<T, C> context, T candidate) {
        for (SlotAdmission<T, C> rule : rules) {
            if (!rule.admit(context, candidate)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public SlotAdmission<T, C> newSession() {
        @SuppressWarnings("unchecked")
        SlotAdmission<T, C>[] sessions = new SlotAdmission[rules.length];
        for (int i = 0; i < rules.length; i++) {
            sessions[i] = rules[i].newSession();
        }
        return new AllAdmission<>(sessions);
    }

    @Override
    public void begin(RankingContext<T, C> context) {
        for (SlotAdmission<T, C> rule : rules) {
            rule.begin(context);
        }
    }

    @Override
    public void onSelected(RankingContext<T, C> context, RankedSlot<T> slot) {
        for (SlotAdmission<T, C> rule : rules) {
            rule.onSelected(context, slot);
        }
    }
}

final class AnyAdmission<T, C> implements SlotAdmission<T, C> {

    private final SlotAdmission<T, C>[] rules;

    @SafeVarargs
    AnyAdmission(SlotAdmission<T, C>... rules) {
        this.rules = rules.clone();
    }

    @Override
    public boolean admit(RankingContext<T, C> context, T candidate) {
        for (SlotAdmission<T, C> rule : rules) {
            if (rule.admit(context, candidate)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public SlotAdmission<T, C> newSession() {
        @SuppressWarnings("unchecked")
        SlotAdmission<T, C>[] sessions = new SlotAdmission[rules.length];
        for (int i = 0; i < rules.length; i++) {
            sessions[i] = rules[i].newSession();
        }
        return new AnyAdmission<>(sessions);
    }

    @Override
    public void begin(RankingContext<T, C> context) {
        for (SlotAdmission<T, C> rule : rules) {
            rule.begin(context);
        }
    }

    @Override
    public void onSelected(RankingContext<T, C> context, RankedSlot<T> slot) {
        for (SlotAdmission<T, C> rule : rules) {
            rule.onSelected(context, slot);
        }
    }
}

final class NotAdmission<T, C> implements SlotAdmission<T, C> {

    private final SlotAdmission<T, C> rule;

    NotAdmission(SlotAdmission<T, C> rule) {
        this.rule = rule;
    }

    @Override
    public boolean admit(RankingContext<T, C> context, T candidate) {
        return !rule.admit(context, candidate);
    }

    @Override
    public SlotAdmission<T, C> newSession() {
        return new NotAdmission<>(rule.newSession());
    }

    @Override
    public void begin(RankingContext<T, C> context) {
        rule.begin(context);
    }

    @Override
    public void onSelected(RankingContext<T, C> context, RankedSlot<T> slot) {
        rule.onSelected(context, slot);
    }
}
