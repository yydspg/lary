// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.dag;

import io.lark.metric.StatsMonitor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

class OpTest {

    // ---- business ops (only name + execute are overridden) --------------

    static final class FetchOp extends Op {
        @Override
        public String name() {
            return "fetch";
        }

        @Override
        public void execute(IContext data) {
            int id = data.get("user_id", Integer.class);
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException(e);
            }
            data.set("user", "user_" + id);
        }
    }

    static final class EnrichOp extends Op {
        @Override
        public String name() {
            return "enrich";
        }

        @Override
        public void execute(IContext data) {
            String user = data.get("user", String.class);
            data.set("enriched", user + "_enriched");
        }
    }

    static final class ScoreOp extends Op {
        @Override
        public String name() {
            return "score";
        }

        @Override
        public void execute(IContext data) {
            data.set("score", 99);
        }
    }

    static final class BoomOp extends Op {
        @Override
        public String name() {
            return "boom";
        }

        @Override
        public void execute(IContext data) {
            throw new IllegalStateException("kaboom");
        }
    }

    static final class SkipSetAspect implements OpAspect {
        @Override
        public boolean shouldSkip(Op op, IContext data) {
            return data.has("skip", String.class)
                    && data.get("skip", String.class).equals(op.name());
        }
    }

    static final class CountingAspect implements OpAspect {
        final AtomicInteger before = new AtomicInteger();
        final AtomicInteger after = new AtomicInteger();

        @Override
        public void onBefore(Op op, IContext data) {
            before.incrementAndGet();
        }

        @Override
        public void onAfter(Op op, IContext data, OpStatus status) {
            after.incrementAndGet();
        }
    }

    // ---- tests ----------------------------------------------------------

    @Test
    void opGraphExecutesWithAspects() {
        StatsMonitor stats = new StatsMonitor();
        CountingAspect counter = new CountingAspect();

        try (OpExecutor executor = new OpExecutor(stats)) {
            OpGraph graph = new OpGraph()
                    .addOp(new FetchOp())
                    .addOp(new EnrichOp())
                    .addOp(new ScoreOp())
                    .dependsOn("fetch", "enrich")
                    .dependsOn("enrich", "score")
                    .addAspect(counter);

            DefaultContext data = new DefaultContext();
            data.set("user_id", 7);
            executor.execute(graph, data);

            assertEquals("user_7_enriched", data.get("enriched", String.class));
            assertEquals(99, data.get("score", Integer.class));
            assertEquals(3, counter.before.get());
            assertEquals(3, counter.after.get());
            assertTrue(executor.allSucceeded());
            assertEquals(OpStatus.SUCCESS, executor.status(graph.ops().get(0)));
            assertTrue(stats.eventCount() >= 3);
        }
    }

    @Test
    void aspectCanConditionallySkipAnOp() {
        try (OpExecutor executor = new OpExecutor()) {
            OpGraph graph = new OpGraph()
                    .addOp(new FetchOp())
                    .addOp(new EnrichOp())
                    .addOp(new ScoreOp())
                    .dependsOn("fetch", "enrich")
                    .dependsOn("enrich", "score")
                    .addAspect(new SkipSetAspect());

            DefaultContext data = new DefaultContext();
            data.set("user_id", 3);
            data.set("skip", "score");
            executor.execute(graph, data);

            assertEquals(OpStatus.SUCCESS, executor.status(graph.ops().get(0)));
            assertEquals(OpStatus.SUCCESS, executor.status(graph.ops().get(1)));
            assertEquals(OpStatus.SKIPPED, executor.status(graph.ops().get(2)));
            assertFalse(data.has("score", Integer.class));
            assertEquals("user_3_enriched", data.get("enriched", String.class));
            assertTrue(executor.allSucceeded());
        }
    }

    @Test
    void failedOpDoesNotPoisonDependents() {
        try (OpExecutor executor = new OpExecutor()) {
            OpGraph graph = new OpGraph()
                    .addOp(new BoomOp())
                    .addOp(new ScoreOp())
                    .dependsOn("boom", "score");

            DefaultContext data = new DefaultContext();
            executor.execute(graph, data);

            assertEquals(OpStatus.FAILED, executor.status(graph.ops().get(0)));
            assertEquals(OpStatus.SUCCESS, executor.status(graph.ops().get(1)));
            assertFalse(executor.allSucceeded());
            assertEquals(99, data.get("score", Integer.class));
        }
    }

    @Test
    void executeAsyncCompletesTheGraph() {
        try (OpExecutor executor = new OpExecutor()) {
            OpGraph graph = new OpGraph()
                    .addOp(new FetchOp())
                    .addOp(new ScoreOp())
                    .dependsOn("fetch", "score");

            DefaultContext data = new DefaultContext();
            data.set("user_id", 11);

            CompletableFuture<Void> done = executor.executeAsync(graph, data);
            done.join();

            assertTrue(executor.allSucceeded());
            assertEquals("user_11", data.get("user", String.class));
            assertEquals(99, data.get("score", Integer.class));
        }
    }

    @Test
    void opGraphValidationRejectsBadGraphs() {
        OpGraph graph = new OpGraph().addOp(new FetchOp());
        assertThrows(IllegalArgumentException.class,
                () -> graph.addOp(new FetchOp()));
        assertThrows(IllegalArgumentException.class,
                () -> graph.dependsOn("ghost", "fetch"));

        OpGraph cycle = new OpGraph()
                .addOp(new FetchOp())
                .addOp(new EnrichOp())
                .dependsOn("fetch", "enrich")
                .dependsOn("enrich", "fetch");
        assertThrows(IllegalStateException.class, cycle::compile);
    }
}
