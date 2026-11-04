// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.rank;

import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SequentialRankerTest {

    @Test
    void reScoresAndAppliesSlotAndGapAdmission() {
        List<Item> items = List.of(
                new Item("A", "c1", 10.0, 100),
                new Item("B", "c1", 9.0, 100),
                new Item("C", "c2", 8.0, 100),
                new Item("D", "c1", 7.0, 100));

        SequentialRanker<Item, Request> ranker =
                SequentialRanker.<Item, Request>builder(3)
                        .admission((context, item) -> context.gapSince(
                                other -> other.category().equals(item.category()))
                                >= 2)
                        .scorer((context, admitted) -> admitted.stream()
                                .mapToDouble(item -> item.base()
                                        - 0.25 * context.selectedCount(
                                        other -> other.category()
                                                .equals(item.category())))
                                .toArray())
                        .build();

        List<RankedSlot<Item>> selected = ranker.rank(items,
                new Request(1_000));

        assertEquals(List.of("A", "C", "B"),
                selected.stream().map(slot -> slot.item().id()).toList());
        assertEquals(List.of(0, 1, 2),
                selected.stream().map(RankedSlot::slot).toList());
        assertEquals(10.0, selected.get(0).score(), 1e-9);
        assertEquals(8.0, selected.get(1).score(), 1e-9);
        assertEquals(8.75, selected.get(2).score(), 1e-9);
    }

    @Test
    void scorerOnlySeesAdmittedCandidatesAndBusinessContext() {
        List<Item> items = List.of(
                new Item("A", "c1", 10.0, 100),
                new Item("B", "c1", 9.0, 200),
                new Item("C", "c2", 8.0, 50));
        List<List<String>> seen = new ArrayList<>();

        SequentialRanker<Item, Request> ranker =
                SequentialRanker.<Item, Request>builder(2)
                        .admission((context, item) ->
                                item.price() <= context.businessContext().maxPrice())
                        .scorer((context, admitted) -> {
                            seen.add(admitted.stream().map(Item::id).toList());
                            return admitted.stream()
                                    .mapToDouble(Item::base).toArray();
                        })
                        .build();

        List<RankedSlot<Item>> selected = ranker.rank(items,
                new Request(100));

        assertEquals(List.of("A", "C"), selected.stream()
                .map(slot -> slot.item().id()).toList());
        assertEquals(List.of(List.of("A", "C"), List.of("C")), seen);
    }

    @Test
    void stopsAtTheRequestedLengthOrWhenNoCandidateIsAdmissible() {
        List<Item> items = List.of(
                new Item("A", "c1", 10.0, 100),
                new Item("B", "c1", 9.0, 100));

        SequentialRanker<Item, Request> twoSlots =
                SequentialRanker.<Item, Request>builder(10)
                        .scorer((context, admitted) -> admitted.stream()
                                .mapToDouble(Item::base).toArray())
                        .build();
        assertEquals(2, twoSlots.rank(items, new Request(0)).size());

        SequentialRanker<Item, Request> rejected =
                SequentialRanker.<Item, Request>builder(3)
                        .admission((context, item) -> false)
                        .scorer((context, admitted) -> new double[admitted.size()])
                        .build();
        assertTrue(rejected.rank(items, new Request(0)).isEmpty());
    }

    @Test
    void appliesEveryAdmissionRule() {
        List<Item> items = List.of(
                new Item("A", "c1", 10.0, 100),
                new Item("B", "c1", 9.0, 100),
                new Item("C", "c2", 8.0, 200));

        SequentialRanker<Item, Request> ranker =
                SequentialRanker.<Item, Request>builder(1)
                        .admission((context, item) -> item.price() <= 100)
                        .admission((context, item) -> !item.id().equals("A"))
                        .scorer((context, admitted) -> admitted.stream()
                                .mapToDouble(Item::base).toArray())
                        .build();

        List<RankedSlot<Item>> selected = ranker.rank(items,
                new Request(100));

        assertEquals(List.of("B"), selected.stream()
                .map(slot -> slot.item().id()).toList());
    }

    @Test
    void radixSortIsDescendingAndStable() {
        double[] scores = {3.5, -2.0, 3.5, 0.0, 10.0, -2.0};

        int[] order = RadixSorter.orderDescending(scores, scores.length);

        org.junit.jupiter.api.Assertions.assertArrayEquals(
                new int[]{4, 0, 2, 3, 1, 5}, order);
    }

    @Test
    void statefulAdmissionKeepsStateAcrossSlotsAndResetsPerRun() {
        List<Item> items = List.of(
                new Item("A", "c1", 10.0, 100),
                new Item("B", "c1", 9.0, 100),
                new Item("C", "c2", 8.0, 100),
                new Item("D", "c1", 7.0, 100),
                new Item("E", "c2", 6.0, 100));

        SequentialRanker<Item, Request> ranker =
                SequentialRanker.<Item, Request>builder(4)
                        .admission(new SlidingCategoryLimit(1))
                        .scorer((context, admitted) -> admitted.stream()
                                .mapToDouble(Item::base).toArray())
                        .build();

        List<RankedSlot<Item>> first = ranker.rank(items, new Request(100));
        List<RankedSlot<Item>> second = ranker.rank(items, new Request(100));

        assertEquals(List.of("A", "C", "B", "E"),
                first.stream().map(slot -> slot.item().id()).toList());
        assertEquals(first.stream().map(slot -> slot.item().id()).toList(),
                second.stream().map(slot -> slot.item().id()).toList());
    }

    @Test
    void statefulAdmissionCanEnforceATotalQuota() {
        List<Item> items = List.of(
                new Item("A", "c1", 10.0, 100),
                new Item("B", "c2", 9.0, 100),
                new Item("C", "c3", 8.0, 100));

        SequentialRanker<Item, Request> ranker =
                SequentialRanker.<Item, Request>builder(5)
                        .admission(new TotalQuotaLimit(2))
                        .scorer((context, admitted) -> admitted.stream()
                                .mapToDouble(Item::base).toArray())
                        .build();

        assertEquals(2, ranker.rank(items, new Request(100)).size());
        assertEquals(2, ranker.rank(items, new Request(100)).size());
    }

    @Test
    void compositeAdmissionsPropagateStatefulSessionLifecycle() {
        List<Item> items = List.of(
                new Item("A", "c1", 10.0, 100),
                new Item("B", "c2", 9.0, 100),
                new Item("C", "c3", 8.0, 100));

        SlotAdmission<Item, Request> composite = SlotAdmission.all(
                new TotalQuotaLimit(1),
                new TotalQuotaLimit(1));
        SequentialRanker<Item, Request> ranker =
                SequentialRanker.<Item, Request>builder(3)
                        .admission(composite)
                        .scorer((context, admitted) -> admitted.stream()
                                .mapToDouble(Item::base).toArray())
                        .build();

        assertEquals(1, ranker.rank(items, new Request(100)).size());
    }

    @Test
    void scorerMustReturnOneScorePerAdmittedCandidate() {
        SequentialRanker<Item, Request> ranker =
                SequentialRanker.<Item, Request>builder(2)
                        .scorer((context, admitted) -> new double[0])
                        .build();

        RankingException failure = assertThrows(RankingException.class,
                () -> ranker.rank(List.of(
                        new Item("A", "c1", 10.0, 100)), new Request(100)));
        assertTrue(failure.getMessage().contains("score"));
    }

    private static final class SlidingCategoryLimit
            implements SlotAdmission<Item, Request> {

        private final int window;
        private final Deque<String> recent = new ArrayDeque<>();

        private SlidingCategoryLimit(int window) {
            this.window = window;
        }

        @Override
        public SlotAdmission<Item, Request> newSession() {
            return new SlidingCategoryLimit(window);
        }

        @Override
        public boolean admit(RankingContext<Item, Request> context,
                             Item candidate) {
            return !recent.contains(candidate.category());
        }

        @Override
        public void onSelected(RankingContext<Item, Request> context,
                               RankedSlot<Item> slot) {
            recent.addLast(slot.item().category());
            while (recent.size() > window) {
                recent.removeFirst();
            }
        }
    }

    private static final class TotalQuotaLimit
            implements SlotAdmission<Item, Request> {

        private final int quota;
        private int selected;

        private TotalQuotaLimit(int quota) {
            this.quota = quota;
        }

        @Override
        public SlotAdmission<Item, Request> newSession() {
            return new TotalQuotaLimit(quota);
        }

        @Override
        public boolean admit(RankingContext<Item, Request> context,
                             Item candidate) {
            return selected < quota;
        }

        @Override
        public void onSelected(RankingContext<Item, Request> context,
                               RankedSlot<Item> slot) {
            selected++;
        }
    }

    private record Item(String id, String category, double base, int price) {
    }

    private record Request(int maxPrice) {
    }
}
