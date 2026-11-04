// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.rank;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Depth-first backtracking ranker for small/medium constrained slates.
 *
 * <p>It explores up to {@code branch} best candidates per node and stops after
 * {@code maxNodes} expansions or the latency budget. Unlike beam search it can
 * abandon a partial sequence and come back to a sibling, so it is useful when
 * constraints make an early choice invalidate a later high-value slot.
 */
public final class BacktrackingRanker<T, C> implements Ranker<T, C> {

    private final int slots;
    private final int branch;
    private final int maxNodes;
    private final List<SlotAdmission<T, C>> admissions;
    private final SlotScorer<T, C> scorer;
    private final IndexedSlotScorer<T, C> indexedScorer;
    private final BeamObjective<T, C> objective;
    private final long maxNanos;

    private BacktrackingRanker(int slots, int branch, int maxNodes,
                               List<SlotAdmission<T, C>> admissions,
                               SlotScorer<T, C> scorer,
                               IndexedSlotScorer<T, C> indexedScorer,
                               BeamObjective<T, C> objective, long maxNanos) {
        this.slots = slots;
        this.branch = branch;
        this.maxNodes = maxNodes;
        this.admissions = List.copyOf(admissions);
        this.scorer = scorer;
        this.indexedScorer = indexedScorer;
        this.objective = objective;
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

    public RankingResult<T> rankWithStats(List<T> candidates,
                                          C businessContext) {
        List<T> source = List.copyOf(Objects.requireNonNull(candidates, "candidates"));
        int count = source.size();
        long started = System.nanoTime();
        if (count == 0 || slots == 0) {
            return new RankingResult<>(List.of(), stats(count, 0, 0, 0, 0,
                    System.nanoTime() - started, false));
        }

        Search search = new Search(source, businessContext, started);
        search.visit(new SearchState<>(List.of(), new boolean[count], 0.0), 0);

        SearchState<T> best = search.best;
        return new RankingResult<>(best.selected,
                stats(count, best.selected.size(), search.admittedTotal,
                        search.scoreCalls, search.sortCalls,
                        System.nanoTime() - started, search.timedOut));
    }

    private RankingStats stats(int candidates, int filled, int admittedTotal,
                               int scoreCalls, int sortCalls, long elapsedNanos,
                               boolean timedOut) {
        return new RankingStats(candidates, slots, filled, admittedTotal,
                scoreCalls, sortCalls, elapsedNanos, timedOut, "backtracking");
    }

    private final class Search {

        private final List<T> source;
        private final C businessContext;
        private final long started;
        private final int[] admittedIndices;
        private final double[] scoreByIndex;
        private final RadixSorter.IndexSorter sorter;

        private int admittedTotal;
        private int scoreCalls;
        private int sortCalls;
        private int nodes;
        private boolean timedOut;
        private SearchState<T> best;

        private Search(List<T> source, C businessContext, long started) {
            this.source = source;
            this.businessContext = businessContext;
            this.started = started;
            this.admittedIndices = new int[source.size()];
            this.scoreByIndex = new double[source.size()];
            this.sorter = new RadixSorter.IndexSorter(source.size());
            this.best = new SearchState<>(List.of(),
                    new boolean[source.size()], 0.0);
        }

        private void visit(SearchState<T> state, int slot) {
            if (timedOut || nodes >= maxNodes) {
                timedOut = timedOut || timedOut(started);
                return;
            }
            nodes++;
            int limit = Math.min(slots, source.size());
            if (slot >= limit) {
                updateBest(state);
                return;
            }

            RankingContext<T, C> context = new RankingContext<>(
                    slot, slots, source, state.selected, state.selectedFlags,
                    businessContext);
            List<SlotAdmission<T, C>> sessions = sessionsFor(state.selected);
            int admittedCount = 0;
            List<T> admitted = indexedScorer == null
                    ? new ArrayList<>(source.size()) : null;
            for (int index = 0; index < source.size(); index++) {
                if (state.selectedFlags[index]) {
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
                updateBest(state);
                return;
            }
            admittedTotal += admittedCount;

            double[] scores;
            try {
                scoreCalls++;
                scores = indexedScorer != null
                        ? indexedScorer.score(context, admittedIndices,
                        admittedCount)
                        : scorer.score(context,
                        Collections.unmodifiableList(admitted));
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

            int valid = 0;
            for (int i = 0; i < scores.length; i++) {
                if (Double.isFinite(scores[i])) {
                    int index = admittedIndices[i];
                    scoreByIndex[index] = scores[i];
                    admittedIndices[valid++] = index;
                }
            }
            if (valid == 0) {
                updateBest(state);
                return;
            }
            sorter.sortDescending(scoreByIndex, admittedIndices, valid);
            sortCalls++;

            int take = Math.min(branch, valid);
            int[] topIndices = Arrays.copyOf(admittedIndices, take);
            double[] topScores = new double[take];
            for (int i = 0; i < take; i++) {
                topScores[i] = scoreByIndex[topIndices[i]];
            }

            for (int i = 0; i < take; i++) {
                if (timedOut || nodes >= maxNodes) {
                    return;
                }
                int index = topIndices[i];
                RankedSlot<T> filled = new RankedSlot<>(slot, index,
                        source.get(index), topScores[i]);
                List<RankedSlot<T>> selected = new ArrayList<>(state.selected);
                selected.add(filled);
                boolean[] flags = state.selectedFlags.clone();
                flags[index] = true;
                SearchState<T> child = new SearchState<>(
                        List.copyOf(selected), flags,
                        objective.score(selected, businessContext));
                visit(child, slot + 1);
            }
        }

        private void updateBest(SearchState<T> state) {
            if (state.selected.size() > best.selected.size()
                    || (state.selected.size() == best.selected.size()
                    && state.objective > best.objective)) {
                best = state;
            }
        }

        private List<SlotAdmission<T, C>> sessionsFor(
                List<RankedSlot<T>> selected) {
            List<SlotAdmission<T, C>> sessions =
                    new ArrayList<>(admissions.size());
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
            boolean[] flags = new boolean[source.size()];
            RankingContext<T, C> begin = new RankingContext<>(
                    0, slots, source, List.of(), flags, businessContext);
            for (SlotAdmission<T, C> session : sessions) {
                try {
                    session.begin(begin);
                } catch (RankingException failure) {
                    throw failure;
                } catch (RuntimeException failure) {
                    throw new RankingException(
                            "admission begin failed", failure);
                }
            }
            List<RankedSlot<T>> prefix = new ArrayList<>(selected.size());
            for (RankedSlot<T> filled : selected) {
                prefix.add(filled);
                flags[filled.index()] = true;
                RankingContext<T, C> context = new RankingContext<>(
                        filled.slot(), slots, source, List.copyOf(prefix), flags,
                        businessContext);
                for (SlotAdmission<T, C> session : sessions) {
                    try {
                        session.onSelected(context, filled);
                    } catch (RankingException failure) {
                        throw failure;
                    } catch (RuntimeException failure) {
                        throw new RankingException(
                                "admission onSelected failed at slot "
                                        + filled.slot(), failure);
                    }
                }
            }
            return sessions;
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
    }

    private boolean timedOut(long started) {
        return maxNanos != Long.MAX_VALUE
                && System.nanoTime() - started >= maxNanos;
    }

    private record SearchState<T>(List<RankedSlot<T>> selected,
                                  boolean[] selectedFlags,
                                  double objective) {
    }

    public static final class Builder<T, C> {

        private final int slots;
        private int branch = 4;
        private int maxNodes = 10_000;
        private final List<SlotAdmission<T, C>> admissions = new ArrayList<>();
        private SlotScorer<T, C> scorer;
        private IndexedSlotScorer<T, C> indexedScorer;
        private BeamObjective<T, C> objective = BeamObjective.sumOfScores();
        private long maxNanos = Long.MAX_VALUE;

        private Builder(int slots) {
            if (slots <= 0) {
                throw new IllegalArgumentException("slots must be positive");
            }
            this.slots = slots;
        }

        public Builder<T, C> admission(SlotAdmission<T, C> admission) {
            admissions.add(Objects.requireNonNull(admission, "admission"));
            return this;
        }

        @SafeVarargs
        public final Builder<T, C> admissions(
                SlotAdmission<T, C>... admissions) {
            Objects.requireNonNull(admissions, "admissions");
            for (SlotAdmission<T, C> admission : admissions) {
                admission(admission);
            }
            return this;
        }

        public Builder<T, C> admissions(
                Collection<? extends SlotAdmission<T, C>> admissions) {
            Objects.requireNonNull(admissions, "admissions");
            for (SlotAdmission<T, C> admission : admissions) {
                admission(admission);
            }
            return this;
        }

        public Builder<T, C> scorer(SlotScorer<T, C> scorer) {
            this.scorer = Objects.requireNonNull(scorer, "scorer");
            this.indexedScorer = null;
            return this;
        }

        public Builder<T, C> scorer(IndexedSlotScorer<T, C> scorer) {
            this.indexedScorer = Objects.requireNonNull(scorer, "scorer");
            this.scorer = null;
            return this;
        }

        public Builder<T, C> policy(SlotPolicy<T, C> policy) {
            Objects.requireNonNull(policy, "policy");
            admissions.add(policy);
            scorer = policy;
            indexedScorer = null;
            return this;
        }

        /** Candidate branches explored per node. */
        public Builder<T, C> branch(int branch) {
            if (branch <= 0) {
                throw new IllegalArgumentException(
                        "branch must be positive");
            }
            this.branch = branch;
            return this;
        }

        /** Hard cap on explored nodes. */
        public Builder<T, C> maxNodes(int maxNodes) {
            if (maxNodes <= 0) {
                throw new IllegalArgumentException(
                        "maxNodes must be positive");
            }
            this.maxNodes = maxNodes;
            return this;
        }

        public Builder<T, C> objective(BeamObjective<T, C> objective) {
            this.objective = Objects.requireNonNull(objective, "objective");
            return this;
        }

        public Builder<T, C> maxDuration(Duration budget) {
            Objects.requireNonNull(budget, "budget");
            if (budget.isNegative() || budget.isZero()) {
                throw new IllegalArgumentException(
                        "budget must be positive");
            }
            this.maxNanos = budget.toNanos();
            return this;
        }

        public BacktrackingRanker<T, C> build() {
            if (scorer == null && indexedScorer == null) {
                throw new IllegalStateException("scorer is required");
            }
            return new BacktrackingRanker<>(slots, branch, maxNodes,
                    admissions, scorer, indexedScorer, objective, maxNanos);
        }
    }
}
