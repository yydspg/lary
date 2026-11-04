// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.rank;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BeamSearchRankerTest {

    @Test
    void beamFindsASequenceGreedyMisses() {
        List<Item> items = List.of(
                new Item("A", "c1", 100.0),
                new Item("B", "c2", 99.0),
                new Item("C", "c1", 98.0),
                new Item("D", "c2", 97.0));

        SequentialRanker<Item, Object> greedy =
                SequentialRanker.<Item, Object>builder(2)
                        .admission(new BlockCat2AfterCat1())
                        .scorer((context, admitted) -> admitted.stream()
                                .mapToDouble(Item::base).toArray())
                        .build();
        List<RankedSlot<Item>> greedySlots = greedy.rank(items);

        BeamSearchRanker<Item, Object> beam =
                BeamSearchRanker.<Item, Object>builder(2, 2)
                        .admission(new BlockCat2AfterCat1())
                        .scorer((context, admitted) -> admitted.stream()
                                .mapToDouble(Item::base).toArray())
                        .build();
        RankingResult<Item> beamResult = beam.rankWithStats(items, null);

        assertEquals(List.of("A", "C"),
                greedySlots.stream().map(slot -> slot.item().id()).toList());
        assertEquals(198.0, total(greedySlots), 1e-9);

        assertEquals(List.of("B", "A"), beamResult.slots().stream()
                .map(slot -> slot.item().id()).toList());
        assertEquals(199.0, total(beamResult.slots()), 1e-9);
        assertEquals("beam", beamResult.stats().strategy());
        assertTrue(beamResult.stats().scoreCalls() > 1);
    }

    @Test
    void beamSupportsIndexedScorerAndCustomObjective() {
        List<Item> items = List.of(
                new Item("A", "c1", 10.0),
                new Item("B", "c2", 9.0),
                new Item("C", "c1", 8.0));

        BeamSearchRanker<Item, Object> beam =
                BeamSearchRanker.<Item, Object>builder(2, 2)
                        .scorer((context, indices, count) -> {
                            double[] scores = new double[count];
                            for (int i = 0; i < count; i++) {
                                scores[i] = items.get(indices[i]).base();
                            }
                            return scores;
                        })
                        // Prefer the last slot's score instead of the sum.
                        .objective((selected, context) -> selected.isEmpty()
                                ? 0.0
                                : selected.get(selected.size() - 1).score())
                        .build();

        RankingResult<Item> result = beam.rankWithStats(items, null);

        assertEquals(2, result.slots().size());
        assertTrue(result.stats().scoreCalls() > 0);
    }

    @Test
    void backtrackingFindsTheSameBetterSequence() {
        List<Item> items = List.of(
                new Item("A", "c1", 100.0),
                new Item("B", "c2", 99.0),
                new Item("C", "c1", 98.0),
                new Item("D", "c2", 97.0));

        BacktrackingRanker<Item, Object> backtracking =
                BacktrackingRanker.<Item, Object>builder(2)
                        .branch(2)
                        .maxNodes(100)
                        .admission(new BlockCat2AfterCat1())
                        .scorer((context, admitted) -> admitted.stream()
                                .mapToDouble(Item::base).toArray())
                        .build();

        RankingResult<Item> result = backtracking.rankWithStats(items, null);

        assertEquals(List.of("B", "A"), result.slots().stream()
                .map(slot -> slot.item().id()).toList());
        assertEquals(199.0, total(result.slots()), 1e-9);
        assertEquals("backtracking", result.stats().strategy());
    }

    private static double total(List<RankedSlot<Item>> slots) {
        double total = 0.0;
        for (RankedSlot<Item> slot : slots) {
            total += slot.score();
        }
        return total;
    }

    private static final class BlockCat2AfterCat1
            implements SlotAdmission<Item, Object> {

        private boolean cat1Selected;

        @Override
        public SlotAdmission<Item, Object> newSession() {
            return new BlockCat2AfterCat1();
        }

        @Override
        public boolean admit(RankingContext<Item, Object> context,
                             Item candidate) {
            return !cat1Selected || !candidate.category().equals("c2");
        }

        @Override
        public void onSelected(RankingContext<Item, Object> context,
                               RankedSlot<Item> slot) {
            if (slot.item().category().equals("c1")) {
                cat1Selected = true;
            }
        }
    }

    private record Item(String id, String category, double base) {
    }
}
