// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.demo;

import io.lark.examples.demo.model.DemoResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FullRankingDemoTest {

    @Test
    void completePipelineRunsEndToEnd() {
        DemoResult result = FullRankingDemo.run(120);

        assertEquals(120, result.prepared().request().goods().size());
        assertEquals(3, result.prepared().dagStats().nodes().size());
        assertEquals(3, result.prepared().dagStats().successCount());

        // The column stage selects top-k per PV; ranking consumes exactly the
        // selected candidates.
        assertEquals(30, result.column().selectedCount());
        assertEquals(result.column().selectedCount(),
                result.column().candidates().size());
        assertTrue(result.column().stats().failureCount() == 0);

        assertFalse(result.greedy().slots().isEmpty());
        assertFalse(result.beam().slots().isEmpty());
        assertTrue(result.greedy().stats().slotsFilled()
                <= result.greedy().stats().slotsRequested());
        assertTrue(result.beam().stats().slotsFilled()
                <= result.beam().stats().slotsRequested());
        assertFalse(result.beam().stats().timedOut());

        for (var slot : result.beam().slots()) {
            assertTrue(Double.isFinite(slot.score()));
            assertTrue(slot.index() >= 0);
        }
    }

    @Test
    void smallRequestSelectsEveryCandidate() {
        DemoResult result = FullRankingDemo.run(7);

        assertEquals(7, result.column().selectedCount());
        assertEquals(7, result.column().candidates().size());
    }
}
