// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.dag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

class DagTest {

    // ---- test domains / nodes -------------------------------------------

    static final class Bag {
        final AtomicInteger counter = new AtomicInteger();
    }

    static final class IncNode extends Node {
        @Override
        public void compute(IContext ctx) {
            ctx.requireDomain(Bag.class).counter.incrementAndGet();
        }
    }

    static final class SleepNode extends Node {
        @Override
        public void compute(IContext ctx) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException(e);
            }
        }
    }

    static final class SourceNode extends Node {
        @Override
        public void compute(IContext ctx) {
            ctx.set("x", 7);
        }
    }

    static final class SinkNode extends Node {
        @Override
        public void compute(IContext ctx) {
            if (ctx.has("x", Integer.class)) {
                int x = ctx.get("x", Integer.class);
                if (x == 7) {
                    ctx.set("y", 2 * x);
                }
            }
        }
    }

    static final class BoomNode extends Node {
        @Override
        public void compute(IContext ctx) {
            throw new IllegalStateException("boom");
        }

        @Override
        public boolean fallback(IContext ctx, Throwable error) {
            ctx.set("recovered", 1);
            return true;
        }
    }

    static final class BoomNoFallbackNode extends Node {
        @Override
        public void compute(IContext ctx) {
            throw new IllegalStateException("boom");
        }
    }

    static final class PoolHopNode extends Node {
        @Override
        public void run(IContext ctx) {
            ctx.set("driver_tid", Thread.currentThread());
            Schedule.run(ctx, PoolKind.COMPUTE,
                    () -> ctx.set("compute_tid", Thread.currentThread()));
            Schedule.run(ctx, PoolKind.IO,
                    () -> ctx.set("io_tid", Thread.currentThread()));
        }
    }

    private static NodeRegistry registry() {
        NodeRegistry registry = new NodeRegistry();
        registry.register("inc", IncNode::new);
        registry.register("sleep", SleepNode::new);
        registry.register("source", SourceNode::new);
        registry.register("sink", SinkNode::new);
        registry.register("boom", BoomNode::new);
        registry.register("boom_nf", BoomNoFallbackNode::new);
        registry.register("pool_hop", PoolHopNode::new);
        return registry;
    }

    // ---- tests ----------------------------------------------------------

    @Test
    void contextStoresTypedValuesAndDomains() {
        DefaultContext ctx = new DefaultContext();
        ctx.set("n", 42);

        assertTrue(ctx.has("n", Integer.class));
        assertFalse(ctx.has("n", String.class));
        assertEquals(42, ctx.get("n", Integer.class));
        assertThrows(NoSuchElementException.class, () -> ctx.get("n", Double.class));
        assertThrows(NoSuchElementException.class, () -> ctx.get("missing", Integer.class));

        List<String> declared = new ArrayList<>();
        declared.add("a");
        ctx.set("declared", declared, List.class);
        assertTrue(ctx.has("declared", List.class));
        assertEquals(List.of("a"), ctx.get("declared", List.class));

        Bag bag = ctx.provideDomain(new Bag());
        assertSame(bag, ctx.domain(Bag.class));
        bag.counter.set(5);
        assertEquals(5, ctx.domain(Bag.class).counter.get());

        Bag created = ctx.provideDomain(Bag.class, Bag::new);
        assertSame(created, ctx.domain(Bag.class));

        ctx.erase("n");
        assertFalse(ctx.has("n", Integer.class));
        assertEquals(1, ctx.size());
    }

    @Test
    void builderValidatesUnknownDuplicateMissingAndCycle() {
        GraphBuilder builder = new GraphBuilder(registry());

        assertThrows(IllegalArgumentException.class,
                () -> builder.build(new NodeDef("ghost")));
        assertThrows(IllegalArgumentException.class,
                () -> builder.build(new NodeDef("inc"), new NodeDef("inc")));
        assertThrows(IllegalArgumentException.class,
                () -> builder.build(new NodeDef("inc", List.of("nope"))));
        assertThrows(IllegalArgumentException.class,
                () -> builder.build(
                        new NodeDef("inc", List.of("b"), "a"),
                        new NodeDef("inc", List.of("a"), "b")));

        Graph graph = builder.build(
                new NodeDef("source"),
                new NodeDef("sink", List.of("source")));
        assertEquals(2, graph.size());
        assertTrue(graph.find("sink").isPresent());
        assertEquals(1, graph.find("sink").orElseThrow().dependencies().size());
    }

    @Test
    void diamondExecutesEveryNode() {
        Graph graph = new GraphBuilder(registry()).build(
                new NodeDef("inc", List.of(), "a"),
                new NodeDef("inc", List.of("a"), "b"),
                new NodeDef("inc", List.of("a"), "c"),
                new NodeDef("inc", List.of("b", "c"), "d"));

        DefaultContext ctx = new DefaultContext();
        ctx.provideDomain(new Bag());
        try (Executor executor = new Executor(4, 4)) {
            executor.execute(graph, ctx);
        }

        assertEquals(4, ctx.domain(Bag.class).counter.get());
        for (Node node : graph.nodes()) {
            assertEquals(NodeStatus.SUCCESS, node.status(), node.id());
        }
    }

    @Test
    void orderingAndDataFlowFollowDependencies() {
        Graph graph = new GraphBuilder(registry()).build(
                new NodeDef("source"),
                new NodeDef("sink", List.of("source")));

        DefaultContext ctx = new DefaultContext();
        try (Executor executor = new Executor(2, 2)) {
            executor.execute(graph, ctx);
        }

        assertTrue(ctx.has("y", Integer.class));
        assertEquals(14, ctx.get("y", Integer.class));
    }

    @Test
    void independentNodesRunInParallel() {
        Graph graph = new GraphBuilder(registry()).build(
                new NodeDef("sleep", List.of(), "s1"),
                new NodeDef("sleep", List.of(), "s2"),
                new NodeDef("sleep", List.of(), "s3"),
                new NodeDef("sleep", List.of(), "s4"));

        DefaultContext ctx = new DefaultContext();
        long start = System.nanoTime();
        try (Executor executor = new Executor(4, 4)) {
            executor.execute(graph, ctx);
        }
        long millis = (System.nanoTime() - start) / 1_000_000;

        assertTrue(millis < 300, "expected parallel execution, took " + millis + "ms");
    }

    @Test
    void failureUsesFallbackAndDoesNotPoisonDependents() {
        Graph graph = new GraphBuilder(registry()).build(
                new NodeDef("boom"),
                new NodeDef("boom_nf"),
                new NodeDef("sink", List.of("boom")));

        DefaultContext ctx = new DefaultContext();
        try (Executor executor = new Executor(2, 2)) {
            executor.execute(graph, ctx);
        }

        assertEquals(NodeStatus.FALLBACK, graph.find("boom").orElseThrow().status());
        assertEquals(NodeStatus.FAILED, graph.find("boom_nf").orElseThrow().status());
        assertNotNull(graph.find("boom_nf").orElseThrow().error());
        assertTrue(ctx.has("recovered", Integer.class));
        assertEquals(NodeStatus.SUCCESS, graph.find("sink").orElseThrow().status());
    }

    @Test
    void graphCanBeReusedWithFreshContexts() {
        Graph graph = new GraphBuilder(registry()).build(
                new NodeDef("inc", List.of(), "a"),
                new NodeDef("inc", List.of("a"), "b"));

        try (Executor executor = new Executor(2, 2)) {
            DefaultContext first = new DefaultContext();
            first.provideDomain(new Bag());
            executor.execute(graph, first);
            assertEquals(2, first.domain(Bag.class).counter.get());

            DefaultContext second = new DefaultContext();
            second.provideDomain(new Bag());
            executor.execute(graph, second);
            assertEquals(2, second.domain(Bag.class).counter.get());
        }
    }

    @Test
    void wideGraphOnSmallPoolDoesNotDeadlock() {
        List<NodeDef> defs = new ArrayList<>();
        for (int i = 0; i < 500; i++) {
            defs.add(new NodeDef("inc", List.of(), "n" + i));
        }
        Graph graph = new GraphBuilder(registry()).build(defs);

        DefaultContext ctx = new DefaultContext();
        ctx.provideDomain(new Bag());
        try (Executor executor = new Executor(2, 2)) {
            executor.execute(graph, ctx);
        }
        assertEquals(500, ctx.domain(Bag.class).counter.get());
    }

    // ---- custom IContext -------------------------------------------------

    static final class CountingContext implements IContext {
        private final DefaultContext delegate = new DefaultContext();
        final AtomicInteger reads = new AtomicInteger();
        final AtomicInteger writes = new AtomicInteger();

        @Override
        public void setVoid(String key, Object value, Class<?> type) {
            writes.incrementAndGet();
            delegate.setVoid(key, value, type);
        }

        @Override
        public Object getVoid(String key, Class<?> type) {
            reads.incrementAndGet();
            return delegate.getVoid(key, type);
        }

        @Override
        public boolean has(String key, Class<?> type) {
            return delegate.has(key, type);
        }

        @Override
        public void erase(String key) {
            delegate.erase(key);
        }

        @Override
        public void setDomain(Class<?> type, Object value) {
            delegate.setDomain(type, value);
        }

        @Override
        public Object getDomain(Class<?> type) {
            return delegate.getDomain(type);
        }

        @Override
        public ExecutorService pool(PoolKind kind) {
            return delegate.pool(kind);
        }

        @Override
        public void installPool(PoolKind kind, ExecutorService pool) {
            delegate.installPool(kind, pool);
        }
    }

    @Test
    void customContextCanReplaceStorage() {
        Graph graph = new GraphBuilder(registry()).build(
                new NodeDef("source"),
                new NodeDef("sink", List.of("source")));

        CountingContext ctx = new CountingContext();
        try (Executor executor = new Executor(2, 2)) {
            executor.execute(graph, ctx);
        }

        assertTrue(ctx.writes.get() >= 2);
        assertTrue(ctx.reads.get() >= 1);
        assertEquals(14, ctx.get("y", Integer.class));
    }

    @Test
    void executorInstallsPoolsAndScheduleHops() {
        Graph graph = new GraphBuilder(registry()).build(
                new NodeDef("pool_hop"));

        DefaultContext ctx = new DefaultContext();
        try (Executor executor = new Executor(3, 5)) {
            assertEquals(3, executor.computeWorkerCount());
            assertEquals(5, executor.ioWorkerCount());
            executor.execute(graph, ctx);
        }

        assertTrue(ctx.has("driver_tid", Thread.class));
        assertTrue(ctx.has("compute_tid", Thread.class));
        assertTrue(ctx.has("io_tid", Thread.class));
        assertNotEquals(ctx.get("driver_tid", Thread.class),
                ctx.get("compute_tid", Thread.class));
        assertNotEquals(ctx.get("compute_tid", Thread.class),
                ctx.get("io_tid", Thread.class));
    }

    @Test
    void statsAndBatchDisable() {
        Graph graph = new GraphBuilder(registry()).build(
                new NodeDef("source", List.of(), "src"),
                new NodeDef("sink", List.of("src"), "snk"));

        DefaultContext ctx = new DefaultContext();
        StatsCollector stats = new StatsCollector();
        try (Executor executor = new Executor(2, 2)) {
            executor.setMonitor(stats);
            executor.execute(graph, ctx, List.of("src"));
        }

        assertEquals(NodeStatus.SKIPPED, graph.find("src").orElseThrow().status());
        assertEquals(NodeStatus.SUCCESS, graph.find("snk").orElseThrow().status());
        assertFalse(ctx.has("y", Integer.class));
        assertEquals(0, graph.find("src").orElseThrow().elapsedNanos());
        assertEquals(0, graph.find("src").orElseThrow().startedAtNanos());

        assertEquals(2, stats.stats().nodes().size());
        boolean sawSkipped = false;
        boolean sawSuccess = false;
        for (NodeRunStats record : stats.stats().nodes()) {
            if (record.id().equals("src")) {
                assertEquals(NodeStatus.SKIPPED, record.status());
                sawSkipped = true;
            } else {
                assertEquals(NodeStatus.SUCCESS, record.status());
                assertTrue(record.elapsedNanos() >= 0);
                assertTrue(record.startedAtNanos() >= 0);
                sawSuccess = true;
            }
        }
        assertTrue(sawSkipped && sawSuccess);
        assertTrue(stats.stats().summary().contains("nodes=2"));

        Graph wide = new GraphBuilder(registry()).build(
                new NodeDef("inc", List.of(), "a"),
                new NodeDef("inc", List.of(), "b"),
                new NodeDef("inc", List.of("a", "b"), "c"));
        DefaultContext ctx2 = new DefaultContext();
        ctx2.provideDomain(new Bag());
        try (Executor executor = new Executor(2, 2)) {
            executor.execute(wide, ctx2, List.of("a", "b"));
        }
        assertEquals(NodeStatus.SKIPPED, wide.find("a").orElseThrow().status());
        assertEquals(NodeStatus.SKIPPED, wide.find("b").orElseThrow().status());
        assertEquals(NodeStatus.SUCCESS, wide.find("c").orElseThrow().status());
        assertEquals(1, ctx2.domain(Bag.class).counter.get());
    }
}
