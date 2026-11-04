// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.rank;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Read-only state for one ranking step: the slot being filled, all candidates,
 * the already-filled sequence and the caller's business context.
 *
 * <p>The context intentionally contains no tensor/column types. A business
 * admission rule or scorer can inspect the selected sequence, compute gaps and
 * then call into whatever subgraph it needs.
 */
public final class RankingContext<T, C> {

    private final int slot;
    private final int slots;
    private final List<T> candidates;
    private final List<RankedSlot<T>> selected;
    private final boolean[] selectedFlags;
    private final C businessContext;

    RankingContext(int slot, int slots, List<T> candidates,
                   List<RankedSlot<T>> selected, boolean[] selectedFlags,
                   C businessContext) {
        this.slot = slot;
        this.slots = slots;
        this.candidates = candidates;
        this.selected = selected;
        this.selectedFlags = selectedFlags;
        this.businessContext = businessContext;
    }

    /** Zero-based slot currently being filled. */
    public int slot() {
        return slot;
    }

    /** Target sequence length. */
    public int slots() {
        return slots;
    }

    public int candidateCount() {
        return candidates.size();
    }

    public List<T> candidates() {
        return candidates;
    }

    public T candidate(int index) {
        return candidates.get(index);
    }

    /** Already-filled slots, in order. */
    public List<RankedSlot<T>> selected() {
        return selected;
    }

    public int selectedCount() {
        return selected.size();
    }

    public RankedSlot<T> selectedAt(int filledSlot) {
        return selected.get(filledSlot);
    }

    public boolean isSelected(int candidateIndex) {
        return selectedFlags[candidateIndex];
    }

    public int remainingCount() {
        return candidates.size() - selected.size();
    }

    /** Business/request context supplied to {@link SequentialRanker#rank}. */
    public C businessContext() {
        return businessContext;
    }

    public <R> R requireBusinessContext(Class<R> type) {
        Objects.requireNonNull(type, "type");
        if (!type.isInstance(businessContext)) {
            throw new RankingException(
                    "business context is not " + type.getName());
        }
        return type.cast(businessContext);
    }

    /**
     * Number of slots since the last selected item matching {@code predicate}.
     * Returns {@link Integer#MAX_VALUE} when no selected item matches.
     */
    public int gapSince(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate, "predicate");
        for (int i = selected.size() - 1; i >= 0; i--) {
            RankedSlot<T> filled = selected.get(i);
            if (predicate.test(filled.item())) {
                return slot - filled.slot();
            }
        }
        return Integer.MAX_VALUE;
    }

    /** Number of selected items matching {@code predicate}. */
    public int selectedCount(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate, "predicate");
        int count = 0;
        for (RankedSlot<T> filled : selected) {
            if (predicate.test(filled.item())) {
                count++;
            }
        }
        return count;
    }
}
