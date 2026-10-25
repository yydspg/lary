// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.dag;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TimeoutTest {

    private static final Duration TIMEOUT = Duration.ofMillis(80);

    static final class SlowNode extends Node {
        private final boolean recover;
        final AtomicInteger executions = new AtomicInteger();
        final AtomicBoolean fallbackCalled = new AtomicBoolean();

        SlowNode(boolean recover) {
            this.recover = recover;
        }

        @Override
        public void compute(IContext ctx) {
            executions.incrementAndGet();
            sleep();
        }

        @Override
        public boolean fallback(IContext ctx, Throwable error) {
            fallbackCalled.set(error instanceof DagTimeoutException);
            if (recover) {
                ctx.set("recovered", 1);
            }
            return recover;
        }
    }

    static final class SlowOp extends Op {
        private final boolean recover;
        final AtomicInteger executions = new AtomicInteger();
        final AtomicBoolean fallbackCalled = new AtomicBoolean();

        SlowOp(boolean recover) {
            this.recover = recover;
        }

        @Override
        public String name() {
            return "slow_op";
        }

        @Override
        public void execute(IContext data) {
            executions.incrementAndGet();
            sleep();
        }

        @Override
        public boolean fallback(IContext data, Throwable error) {
            fallbackCalled.set(error instanceof DagTimeoutException);
            if (recover) {
                data.set("recovered", 1);
            }
            return recover;
        }
    }

    private static void sleep() {
        try {
            Thread.sleep(5_000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("interrupted", e);
        }
    }

    private static Graph graph(SlowNode node) {
        NodeRegistry registry = new NodeRegistry();
        registry.register("slow", () -> node);
        return new GraphBuilder(registry).build(new NodeDef("slow"));
    }

    @Test
    void nodeTimeoutInvokesFallbackAndDoesNotBlockTheRun() {
        SlowNode node = new SlowNode(true);
        Graph graph = graph(node);
        DefaultContext ctx = new DefaultContext();

        long start = System.nanoTime();
        try (Executor executor = new Executor(2, 2)) {
            executor.setTimeoutPolicy(TimeoutPolicy.builder()
                    .defaultTimeout(TIMEOUT)
                    .maxConsecutiveTimeouts(3)
                    .quarantineDuration(Duration.ofMinutes(1))
                    .build());
            executor.execute(graph, ctx);

            assertEquals(NodeStatus.FALLBACK, node.status());
            assertTrue(node.fallbackCalled.get());
            assertInstanceOf(DagTimeoutException.class, node.error());
        }
        long millis = (System.nanoTime() - start) / 1_000_000;

        assertTrue(millis < 2_000, "timeout should return quickly, took " + millis + "ms");
        assertTrue(ctx.has("recovered", Integer.class));
    }

    @Test
    void nodeTimeoutWithoutFallbackIsTimedOut() {
        SlowNode node = new SlowNode(false);
        Graph graph = graph(node);

        try (Executor executor = new Executor(2, 2)) {
            executor.setTimeoutPolicy(TimeoutPolicy.of(TIMEOUT));
            executor.execute(graph, new DefaultContext());

            assertEquals(NodeStatus.TIMED_OUT, node.status());
            assertInstanceOf(DagTimeoutException.class, node.error());
        }
    }

    @Test
    void repeatedNodeTimeoutsQuarantineTheNode() {
        SlowNode node = new SlowNode(false);
        Graph graph = graph(node);

        try (Executor executor = new Executor(2, 2)) {
            executor.setTimeoutPolicy(TimeoutPolicy.builder()
                    .defaultTimeout(TIMEOUT)
                    .maxConsecutiveTimeouts(1)
                    .quarantineDuration(Duration.ofMinutes(1))
                    .repeatedAction(TimeoutAction.QUARANTINE)
                    .build());

            executor.execute(graph, new DefaultContext());
            assertEquals(NodeStatus.TIMED_OUT, node.status());
            assertTrue(executor.timeoutTracker().isQuarantined("slow"));

            executor.execute(graph, new DefaultContext());
            assertEquals(NodeStatus.SKIPPED, node.status());
            assertEquals(1, node.executions.get());
            assertTrue(executor.timeoutTracker().isQuarantined("slow"));
        }
    }

    @Test
    void repeatedNodeTimeoutsCanAbortTheGraph() {
        SlowNode node = new SlowNode(true);
        Graph graph = graph(node);

        try (Executor executor = new Executor(2, 2)) {
            executor.setTimeoutPolicy(TimeoutPolicy.builder()
                    .defaultTimeout(TIMEOUT)
                    .maxConsecutiveTimeouts(1)
                    .quarantineDuration(Duration.ofMinutes(1))
                    .repeatedAction(TimeoutAction.ABORT_GRAPH)
                    .build());
            executor.execute(graph, new DefaultContext());

            assertTrue(executor.lastRunAborted());
            assertTrue(executor.lastAbortReason().contains("slow"));
        }
    }

    @Test
    void graphDeadlineAbortsTheRun() {
        SlowNode node = new SlowNode(false);
        Graph graph = graph(node);

        long start = System.nanoTime();
        try (Executor executor = new Executor(2, 2)) {
            executor.setTimeoutPolicy(TimeoutPolicy.builder()
                    .graphTimeout(TIMEOUT)
                    .build());
            executor.execute(graph, new DefaultContext());

            assertTrue(executor.lastRunAborted());
        }
        long millis = (System.nanoTime() - start) / 1_000_000;

        assertTrue(millis < 2_000, "graph deadline should return quickly, took " + millis + "ms");
    }

    @Test
    void opTimeoutInvokesFallback() {
        SlowOp op = new SlowOp(true);
        OpGraph graph = new OpGraph().addOp(op);
        DefaultContext data = new DefaultContext();

        long start = System.nanoTime();
        try (OpExecutor executor = new OpExecutor()) {
            executor.setTimeoutPolicy(TimeoutPolicy.of(TIMEOUT));
            executor.execute(graph, data);

            assertEquals(OpStatus.FALLBACK, executor.status(op));
            assertTrue(op.fallbackCalled.get());
            assertTrue(data.has("recovered", Integer.class));
        }
        long millis = (System.nanoTime() - start) / 1_000_000;

        assertTrue(millis < 2_000, "op timeout should return quickly, took " + millis + "ms");
    }

    @Test
    void repeatedOpTimeoutsQuarantineTheOp() {
        SlowOp op = new SlowOp(false);
        OpGraph graph = new OpGraph().addOp(op);

        try (OpExecutor executor = new OpExecutor()) {
            executor.setTimeoutPolicy(TimeoutPolicy.builder()
                    .defaultTimeout(TIMEOUT)
                    .maxConsecutiveTimeouts(1)
                    .quarantineDuration(Duration.ofMinutes(1))
                    .build());

            executor.execute(graph, new DefaultContext());
            assertEquals(OpStatus.TIMED_OUT, executor.status(op));
            assertTrue(executor.timeoutTracker().isQuarantined("slow_op"));

            executor.execute(graph, new DefaultContext());
            assertEquals(OpStatus.SKIPPED, executor.status(op));
            assertEquals(1, op.executions.get());
        }
    }

    @Test
    void timedOutOpStillDoesNotPoisonDependents() {
        SlowOp failing = new SlowOp(false);
        Op next = new Op() {
            @Override
            public String name() {
                return "next";
            }

            @Override
            public void execute(IContext data) {
                data.set("next_ran", 1);
            }
        };
        OpGraph graph = new OpGraph()
                .addOp(failing)
                .addOp(next)
                .dependsOn("slow_op", "next");

        try (OpExecutor executor = new OpExecutor()) {
            executor.setTimeoutPolicy(TimeoutPolicy.of(TIMEOUT));
            executor.execute(graph, new DefaultContext());

            assertEquals(OpStatus.TIMED_OUT, executor.status(failing));
            assertEquals(OpStatus.SUCCESS, executor.status(next));
        }
    }
}
