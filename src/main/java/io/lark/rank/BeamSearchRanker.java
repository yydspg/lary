// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.rank;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Beam-search sequential ranker for constrained slates.
 *
 * <p>Instead of committing to the best candidate of one partial sequence, the
 * engine keeps the best {@code width} partial sequences, expands the top
 * {@code branch} candidates of each, and ranks the children with a
 * {@link BeamObjective}. This finds sequences that plain greedy misses when a
 * high-scoring early slot blocks better later slots.
 *
 * <p>Admission sessions are replayed for every beam state, so stateful limiters
 * (sliding windows, quotas) remain correct on every branch.
 */
public final class BeamSearchRanker<T, C> implements Ranker<T, C> {

    private final int slots;
    private final int width;
    private final int branch;
    private final List<SlotAdmission<T, C>> admissions;
    private final SlotScorer<T, C> scorer;
    private final IndexedSlotScorer<T, C> indexedScorer;
    private final BeamObjective<T, C> objective;
    private final long maxNanos;

    private BeamSearchRanker(int slots, int width, int branch,
                             List<SlotAdmission<T, C>> admissions,
                             SlotScorer<T, C> scorer,
                             IndexedSlotScorer<T, C> indexedScorer,
                             BeamObjective<T, C> objective, long maxNanos) {
        this.slots = slots;
        this.width = width;
        this.branch = branch;
        this.admissions = List.copyOf(admissions);
        this.scorer = scorer;
        this.indexedScorer = indexedScorer;
        this.objective = objective;
        this.maxNanos = maxNanos;
    }

    public static <T, C> Builder<T, C> builder(int slots, int width) {
        return new Builder<>(slots, width);
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

        int limit = Math.min(slots, count);
        int[] admittedIndices = new int[count];
        double[] scoreByIndex = new double[count];
        RadixSorter.IndexSorter sorter = new RadixSorter.IndexSorter(count);

        List<BeamState<T>> beams = List.of(new BeamState<>(
                List.of(), new boolean[count], 0.0));
        int admittedTotal = 0;
        int scoreCalls = 0;
        int sortCalls = 0;
        boolean timedOut = false;

        for (int slot = 0; slot < limit; slot++) {
            if (timedOut(started)) {
                timedOut = true;
                break;
            }
            List<BeamState<T>> children = new ArrayList<>();
            for (BeamState<T> state : beams) {
                if (timedOut(started)) {
                    timedOut = true;
                    break;
                }
                RankingContext<T, C> context = new RankingContext<>(
                        slot, slots, source, state.selected, state.selectedFlags,
                        businessContext);
                List<SlotAdmission<T, C>> sessions = sessionsFor(
                        source, state.selected, businessContext);

                int admittedCount = 0;
                List<T> admitted = indexedScorer == null
                        ? new ArrayList<>(count) : null;
                for (int index = 0; index < count; index++) {
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
                    continue;
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
                    continue;
                }
                sorter.sortDescending(scoreByIndex, admittedIndices, valid);
                sortCalls++;

                int take = Math.min(branch, valid);
                for (int i = 0; i < take; i++) {
                    int bestIndex = admittedIndices[i];
                    RankedSlot<T> filled = new RankedSlot<>(slot, bestIndex,
                            source.get(bestIndex), scoreByIndex[bestIndex]);
                    List<RankedSlot<T>> selected =
                            new ArrayList<>(state.selected);
                    selected.add(filled);
                    boolean[] flags = state.selectedFlags.clone();
                    flags[bestIndex] = true;
                    children.add(new BeamState<>(List.copyOf(selected), flags,
                            objective.score(selected, businessContext)));
                }
            }
            if (timedOut || children.isEmpty()) {
                break;
            }
            children.sort(Comparator
                    .comparingDouble(BeamState<T>::objective).reversed());
            int keep = Math.min(width, children.size());
            beams = List.copyOf(children.subList(0, keep));
        }

        BeamState<T> best = beams.isEmpty()
                ? new BeamState<>(List.of(), new boolean[count], 0.0)
                : beams.get(0);
        return new RankingResult<>(best.selected,
                stats(count, best.selected.size(), admittedTotal, scoreCalls,
                        sortCalls, System.nanoTime() - started, timedOut));
    }

    private List<SlotAdmission<T, C>> sessionsFor(
            List<T> source, List<RankedSlot<T>> selected, C businessContext) {
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

    private RankingStats stats(int candidates, int filled, int admittedTotal,
                               int scoreCalls, int sortCalls, long elapsedNanos,
                               boolean timedOut) {
        return new RankingStats(candidates, slots, filled, admittedTotal,
                scoreCalls, sortCalls, elapsedNanos, timedOut, "beam");
    }

    private boolean timedOut(long started) {
        return maxNanos != Long.MAX_VALUE
                && System.nanoTime() - started >= maxNanos;
    }

    public int slots() {
        return slots;
    }

    public int width() {
        return width;
    }

    private record BeamState<T>(List<RankedSlot<T>> selected,
                                boolean[] selectedFlags,
                                double objective) {
    }

    public static final class Builder<T, C> {

        private final int slots;
        private final int width;
        private int branch;
        private final List<SlotAdmission<T, C>> admissions = new ArrayList<>();
        private SlotScorer<T, C> scorer;
        private IndexedSlotScorer<T, C> indexedScorer;
        private BeamObjective<T, C> objective = BeamObjective.sumOfScores();
        private long maxNanos = Long.MAX_VALUE;

        private Builder(int slots, int width) {
            if (slots <= 0) {
                throw new IllegalArgumentException("slots must be positive");
            }
            if (width <= 0) {
                throw new IllegalArgumentException("width must be positive");
            }
            this.slots = slots;
            this.width = width;
            this.branch = width;
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

        /** Candidate branches kept per beam state; defaults to width. */
        public Builder<T, C> branch(int branch) {
            if (branch <= 0) {
                throw new IllegalArgumentException(
                        "branch must be positive");
            }
            this.branch = branch;
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

        public BeamSearchRanker<T, C> build() {
            if (scorer == null && indexedScorer == null) {
                throw new IllegalStateException("scorer is required");
            }
            return new BeamSearchRanker<>(slots, width, branch, admissions,
                    scorer, indexedScorer, objective, maxNanos);
        }
    }
}
