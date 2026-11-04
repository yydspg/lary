// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.rank;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Business-facing sequential (greedy) ranking engine.
 *
 * <p>For every slot the engine:
 *
 * <ol>
 *   <li>applies every registered {@link SlotAdmission};</li>
 *   <li>asks the scorer to recompute scores for the admitted candidates; the
 *       scorer can execute a column subgraph or any other computation;</li>
 *   <li>sorts the new scores with stable radix sort and fills the next slot
 *       with the best candidate;</li>
 *   <li>repeats until the requested length is reached, no candidate is
 *       admissible, or the latency budget is exhausted.</li>
 * </ol>
 *
 * <p>Equal scores keep the original list order, so a pre-ordered input list is
 * a deterministic tie-breaker. The input list is never reordered: the engine
 * keeps an int[] index array and radix-sorts only that array. The engine has
 * no dependency on the column engine; the scorer is the only bridge to it.
 */
public final class SequentialRanker<T, C> implements Ranker<T, C> {

    private final int slots;
    private final List<SlotAdmission<T, C>> admissions;
    private final SlotScorer<T, C> scorer;
    private final IndexedSlotScorer<T, C> indexedScorer;
    private final long maxNanos;

    private SequentialRanker(int slots, List<SlotAdmission<T, C>> admissions,
                             SlotScorer<T, C> scorer,
                             IndexedSlotScorer<T, C> indexedScorer,
                             long maxNanos) {
        this.slots = slots;
        this.admissions = List.copyOf(admissions);
        this.scorer = scorer;
        this.indexedScorer = indexedScorer;
        this.maxNanos = maxNanos;
    }

    public static <T, C> Builder<T, C> builder(int slots) {
        return new Builder<>(slots);
    }

    public List<RankedSlot<T>> rank(List<T> candidates) {
        return rank(candidates, null);
    }

    public List<RankedSlot<T>> rank(List<T> candidates, C businessContext) {
        return rankWithStats(candidates, businessContext).slots();
    }

    /**
     * Fill up to {@link #slots()} slots and return execution counters too.
     *
     * @param candidates input list; its order is the deterministic tie-breaker
     * @param businessContext caller context passed to admission/scorer
     */
    public RankingResult<T> rankWithStats(List<T> candidates,
                                          C businessContext) {
        List<T> source = List.copyOf(Objects.requireNonNull(candidates, "candidates"));
        int count = source.size();
        long started = System.nanoTime();
        if (count == 0 || slots == 0) {
            return new RankingResult<>(List.of(), stats(count, 0, 0, 0, 0,
                    System.nanoTime() - started, false));
        }

        boolean[] selectedFlags = new boolean[count];
        List<RankedSlot<T>> selected = new ArrayList<>(Math.min(slots, count));
        int[] admittedIndices = new int[count];
        double[] scoreByIndex = new double[count];
        RadixSorter.IndexSorter sorter = new RadixSorter.IndexSorter(count);
        int limit = Math.min(slots, count);

        List<SlotAdmission<T, C>> sessions = new ArrayList<>(admissions.size());
        for (SlotAdmission<T, C> admission : admissions) {
            SlotAdmission<T, C> session;
            try {
                session = admission.newSession();
            } catch (RankingException failure) {
                throw failure;
            } catch (RuntimeException failure) {
                throw new RankingException(
                        "failed to create admission session", failure);
            }
            if (session == null) {
                throw new RankingException(
                        "admission returned a null session");
            }
            sessions.add(session);
        }
        RankingContext<T, C> beginContext = new RankingContext<>(
                0, slots, source, List.of(), selectedFlags, businessContext);
        for (SlotAdmission<T, C> session : sessions) {
            try {
                session.begin(beginContext);
            } catch (RankingException failure) {
                throw failure;
            } catch (RuntimeException failure) {
                throw new RankingException(
                        "admission begin failed", failure);
            }
        }

        int admittedTotal = 0;
        int scoreCalls = 0;
        int sortCalls = 0;
        boolean timedOut = false;

        for (int slot = 0; slot < limit; slot++) {
            if (timedOut(started)) {
                timedOut = true;
                break;
            }
            RankingContext<T, C> context = new RankingContext<>(
                    slot, slots, source, List.copyOf(selected), selectedFlags,
                    businessContext);

            int admittedCount = 0;
            List<T> admitted = indexedScorer == null
                    ? new ArrayList<>(count) : null;
            for (int index = 0; index < count; index++) {
                if (selectedFlags[index]) {
                    continue;
                }
                T candidate = source.get(index);
                if (admitAll(sessions, context, candidate, slot)) {
                    admittedIndices[admittedCount++] = index;
                    if (admitted != null) {
                        admitted.add(candidate);
                    }
                }
            }
            if (admittedCount == 0) {
                break;
            }
            admittedTotal += admittedCount;

            double[] scores;
            try {
                scoreCalls++;
                scores = indexedScorer != null
                        ? indexedScorer.score(context, admittedIndices,
                        admittedCount)
                        : scorer.score(context, Collections.unmodifiableList(admitted));
            } catch (RankingException failure) {
                throw failure;
            } catch (RuntimeException failure) {
                throw new RankingException(
                        "scorer failed at slot " + slot, failure);
            }
            if (scores == null || scores.length != admittedCount) {
                throw new RankingException(
                        "scorer returned "
                                + (scores == null ? "null" : scores.length)
                                + " score(s) for " + admittedCount
                                + " admitted candidate(s) at slot " + slot);
            }

            // Drop non-finite scores and compact the index array, then
            // radix-sort the indexes only. Candidate objects never move.
            int valid = 0;
            for (int i = 0; i < scores.length; i++) {
                if (Double.isFinite(scores[i])) {
                    int index = admittedIndices[i];
                    scoreByIndex[index] = scores[i];
                    admittedIndices[valid++] = index;
                }
            }
            if (valid == 0) {
                break;
            }

            sorter.sortDescending(scoreByIndex, admittedIndices, valid);
            sortCalls++;
            int bestIndex = admittedIndices[0];
            double bestScore = scoreByIndex[bestIndex];

            selectedFlags[bestIndex] = true;
            RankedSlot<T> filled = new RankedSlot<>(slot, bestIndex,
                    source.get(bestIndex), bestScore);
            selected.add(filled);

            RankingContext<T, C> selectedContext = new RankingContext<>(
                    slot, slots, source, List.copyOf(selected), selectedFlags,
                    businessContext);
            for (SlotAdmission<T, C> session : sessions) {
                try {
                    session.onSelected(selectedContext, filled);
                } catch (RankingException failure) {
                    throw failure;
                } catch (RuntimeException failure) {
                    throw new RankingException(
                            "admission onSelected failed at slot " + slot,
                            failure);
                }
            }
        }
        return new RankingResult<>(List.copyOf(selected),
                stats(count, selected.size(), admittedTotal, scoreCalls,
                        sortCalls, System.nanoTime() - started, timedOut));
    }

    private RankingStats stats(int candidates, int filled, int admittedTotal,
                               int scoreCalls, int sortCalls, long elapsedNanos,
                               boolean timedOut) {
        return new RankingStats(candidates, slots, filled, admittedTotal,
                scoreCalls, sortCalls, elapsedNanos, timedOut, "greedy");
    }

    private boolean timedOut(long started) {
        return maxNanos != Long.MAX_VALUE
                && System.nanoTime() - started >= maxNanos;
    }

    private boolean admitAll(List<SlotAdmission<T, C>> sessions,
                             RankingContext<T, C> context, T candidate,
                             int slot) {
        for (SlotAdmission<T, C> admission : sessions) {
            boolean allowed;
            try {
                allowed = admission.admit(context, candidate);
            } catch (RankingException failure) {
                throw failure;
            } catch (RuntimeException failure) {
                throw new RankingException(
                        "admission failed at slot " + slot, failure);
            }
            if (!allowed) {
                return false;
            }
        }
        return true;
    }

    public static final class Builder<T, C> {

        private final int slots;
        private final List<SlotAdmission<T, C>> admissions = new ArrayList<>();
        private SlotScorer<T, C> scorer;
        private IndexedSlotScorer<T, C> indexedScorer;
        private long maxNanos = Long.MAX_VALUE;

        private Builder(int slots) {
            if (slots <= 0) {
                throw new IllegalArgumentException("slots must be positive");
            }
            this.slots = slots;
        }

        /** Add one admission rule; all added rules must admit (AND). */
        public Builder<T, C> admission(SlotAdmission<T, C> admission) {
            admissions.add(Objects.requireNonNull(admission, "admission"));
            return this;
        }

        /** Add multiple admission rules; all must admit (AND). */
        @SafeVarargs
        public final Builder<T, C> admissions(
                SlotAdmission<T, C>... admissions) {
            Objects.requireNonNull(admissions, "admissions");
            for (SlotAdmission<T, C> admission : admissions) {
                admission(admission);
            }
            return this;
        }

        /** Add multiple admission rules; all must admit (AND). */
        public Builder<T, C> admissions(
                Collection<? extends SlotAdmission<T, C>> admissions) {
            Objects.requireNonNull(admissions, "admissions");
            for (SlotAdmission<T, C> admission : admissions) {
                admission(admission);
            }
            return this;
        }

        /** Recompute scores for the admitted candidates of each slot. */
        public Builder<T, C> scorer(SlotScorer<T, C> scorer) {
            this.scorer = Objects.requireNonNull(scorer, "scorer");
            this.indexedScorer = null;
            return this;
        }

        /**
         * Index-based scorer: avoids the intermediate candidate list and is
         * the preferred form for columnar subgraphs.
         */
        public Builder<T, C> scorer(IndexedSlotScorer<T, C> scorer) {
            this.indexedScorer = Objects.requireNonNull(scorer, "scorer");
            this.scorer = null;
            return this;
        }

        /** Use one object for both admission and scoring. */
        public Builder<T, C> policy(SlotPolicy<T, C> policy) {
            Objects.requireNonNull(policy, "policy");
            admissions.add(policy);
            scorer = policy;
            indexedScorer = null;
            return this;
        }

        /** Stop filling slots when the wall-clock budget is exhausted. */
        public Builder<T, C> maxDuration(Duration budget) {
            Objects.requireNonNull(budget, "budget");
            if (budget.isNegative() || budget.isZero()) {
                throw new IllegalArgumentException(
                        "budget must be positive");
            }
            this.maxNanos = budget.toNanos();
            return this;
        }

        public SequentialRanker<T, C> build() {
            if (scorer == null && indexedScorer == null) {
                throw new IllegalStateException("scorer is required");
            }
            return new SequentialRanker<>(slots, admissions, scorer,
                    indexedScorer, maxNanos);
        }
    }
}
