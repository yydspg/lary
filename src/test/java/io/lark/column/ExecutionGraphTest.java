// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column;

import io.lark.column.backend.Backend;
import io.lark.column.backend.BackendFactory;
import io.lark.column.biz.ColumnDsl;
import io.lark.column.exec.ComputeGraph;
import io.lark.column.exec.ExecutionContext;
import io.lark.column.exec.OpContext;
import io.lark.column.exec.OpSpec;
import io.lark.column.exec.TensorOp;
import io.lark.column.exec.TensorOps;
import io.lark.column.monitor.ColumnStatsCollector;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExecutionGraphTest {

    private static Tensor run(TensorStore store, OpSpec spec) {
        TensorOp op = TensorOps.create(spec);
        op.compute(new OpContext(store));
        return store.get(spec.outputs().get(0));
    }

    @Test
    void executionLayerOps() {
        TensorStore store = new TensorStore();
        store.set("a", Tensor.fromLongs(1, 2, 3, 4));
        store.set("b", Tensor.fromLongs(10, 20, 30, 40));
        store.set("f", Tensor.fromDoubles(1.5, 2.5, 3.5, 4.5));

        Tensor sum = run(store, new OpSpec("add", List.of("a", "b"), List.of("s")));
        assertEquals(DType.INT64, sum.dtype());
        assertEquals(11, sum.getLong(0));
        assertEquals(44, sum.getLong(3));

        Tensor mixed = run(store, new OpSpec("mul", List.of("a", "f"), List.of("p")));
        assertEquals(DType.FLOAT64, mixed.dtype());
        assertEquals(1.5, mixed.getDouble(0), 1e-9);

        Tensor scaled = run(store, new OpSpec("mul_scalar", List.of("a"), List.of("sc"),
                Map.of("scalar", "2.0")));
        assertEquals(8, scaled.getLong(3));

        Tensor mask = run(store, new OpSpec("gt_scalar", List.of("a"), List.of("mask"),
                Map.of("scalar", "2")));
        assertEquals(DType.INT64, mask.dtype());
        assertEquals(0, mask.getLong(0));
        assertEquals(1, mask.getLong(2));

        Tensor selected = run(store, new OpSpec("select",
                List.of("mask", "a", "b"), List.of("sel")));
        assertEquals(10, selected.getLong(0));
        assertEquals(4, selected.getLong(3));

        Tensor filtered = run(store, new OpSpec("filter",
                List.of("a", "mask"), List.of("flt")));
        assertEquals(2, filtered.size());
        assertEquals(3, filtered.getLong(0));
        assertEquals(4, filtered.getLong(1));

        Tensor total = run(store, new OpSpec("sum", List.of("a"), List.of("total")));
        assertEquals(10.0, total.getDouble(0), 1e-9);
        Tensor count = run(store, new OpSpec("count", List.of("a"), List.of("cnt")));
        assertEquals(4, count.getLong(0));
        Tensor max = run(store, new OpSpec("max", List.of("b"), List.of("mx")));
        assertEquals(40, max.getLong(0));

        Tensor casted = run(store, new OpSpec("cast", List.of("a"), List.of("casted"),
                Map.of("dtype", "float32")));
        assertEquals(DType.FLOAT32, casted.dtype());
        assertEquals(1.0f, casted.getFloat(0), 1e-6f);

        Tensor qa = run(store, new OpSpec("quantize", List.of("f"), List.of("qa")));
        assertEquals(DType.Q8_0, qa.dtype());
        Tensor dqa = run(store, new OpSpec("dequantize", List.of("qa"), List.of("dqa")));
        assertEquals(DType.FLOAT64, dqa.dtype());
        Tensor dot = run(store, new OpSpec("dot", List.of("qa", "qa"), List.of("dotv")));
        double expected = 0.0;
        for (int i = 0; i < 4; i++) expected += Math.pow(1.5 + i, 2);
        assertTrue(Math.abs(dot.getDouble(0) - expected) / expected < 0.1);

        assertThrows(IllegalArgumentException.class,
                () -> TensorOps.create(new OpSpec("nope", List.of(), List.of("x"))));
    }

    @Test
    void backendFactory() {
        BackendFactory factory = BackendFactory.instance();
        assertTrue(factory.contains("cpu"));
        assertTrue(factory.available().contains("cpu"));

        Backend backend = factory.create("cpu");
        assertEquals("cpu", backend.name());
        assertTrue(backend.supports(DType.INT32));
        assertTrue(backend.supports(DType.Q8_0));

        TensorOp op = backend.createOp(new OpSpec("add",
                List.of("a", "b"), List.of("c")));
        assertEquals("add", op.opType());

        assertThrows(RuntimeException.class, () -> factory.create("gpu"));
    }

    @Test
    void computeGraphSequentialAndAsync() {
        ComputeGraph graph = new ComputeGraph();
        graph.addNode("n1", "m1", TensorOps.create(
                new OpSpec("mul_scalar", List.of("x"), List.of("x2"),
                        Map.of("scalar", "2"))));
        graph.addNode("n2", "m1", TensorOps.create(
                new OpSpec("add", List.of("x2", "x"), List.of("x3"))));
        graph.addNode("n3", "m2", TensorOps.create(
                new OpSpec("sum", List.of("x3"), List.of("total"))));
        graph.addDependency("n2", "n1");
        graph.addDependency("n3", "n2");
        graph.finalizeGraph();
        assertEquals(3, graph.size());

        ExecutionContext sync = new ExecutionContext();
        sync.store().set("x", Tensor.fromLongs(1, 2, 3));
        ColumnStatsCollector stats = new ColumnStatsCollector();
        sync.setMonitor(stats);
        graph.execute(sync);
        assertEquals(18.0, sync.store().get("total").getDouble(0), 1e-9);
        assertEquals(3, stats.stats().ops().size());
        assertEquals(2, stats.stats().modules().size());
        assertFalse(stats.stats().pressure().isEmpty());

        ExecutionContext async = new ExecutionContext();
        async.store().set("x", Tensor.fromLongs(1, 2, 3));
        try (ExecutorService pool = Executors.newThreadPerTaskExecutor(
                Thread.ofVirtual().name("column-test-", 0).factory())) {
            graph.executeAsync(pool, async);
        }
        assertEquals(18.0, async.store().get("total").getDouble(0), 1e-9);
    }

    @Test
    void computeGraphDetectsCycles() {
        ComputeGraph graph = new ComputeGraph();
        graph.addNode("a", "m", TensorOps.create(
                new OpSpec("neg", List.of("b"), List.of("a"))));
        graph.addNode("b", "m", TensorOps.create(
                new OpSpec("neg", List.of("a"), List.of("b"))));
        graph.addDependency("b", "a");
        graph.addDependency("a", "b");
        assertThrows(IllegalStateException.class, graph::finalizeGraph);
    }

    @Test
    void dslParsesExpressionsAndFoldsLiterals() {
        List<OpSpec> ops = ColumnDsl.parse(
                "scaled = x * 2\n"
                        + "score = scaled + y\n"
                        + "mask = x > 3\n"
                        + "filtered = filter(score, mask)\n"
                        + "total = sum(score)\n"
                        + "best = max(select(mask, score, 0))\n");

        assertTrue(ops.size() >= 6);
        assertTrue(ops.stream().anyMatch(op -> op.type().equals("mul_scalar")));
        assertTrue(ops.stream().anyMatch(op -> op.type().equals("sum")));
        assertTrue(ops.stream().anyMatch(op -> op.type().equals("filter")));

        List<OpSpec> folded = ColumnDsl.parse("v = a + 2");
        assertTrue(folded.stream().anyMatch(op -> op.type().equals("add_scalar")));

        // literal-left non-commutative ops stay correct
        List<OpSpec> leftSub = ColumnDsl.parse("v = 2 - a");
        assertTrue(leftSub.stream().anyMatch(op -> op.type().equals("sub")));
        assertTrue(leftSub.stream().anyMatch(op -> op.type().equals("const")));

        assertThrows(IllegalArgumentException.class, () -> ColumnDsl.parse("x = "));
        assertThrows(IllegalArgumentException.class,
                () -> ColumnDsl.parse("x = bogus_fn(a)"));
    }

    @Test
    void simdIsLoaded() {
        assertNotNull(Simd.summary());
        assertTrue(Simd.doubleLanes() > 0);
        assertTrue(Simd.byteLanes() > 0);
        assertTrue(Simd.shortLanes() > 0);
    }
}
