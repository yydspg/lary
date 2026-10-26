// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column;

import io.lark.column.data.*;
import io.lark.column.tensor.*;
import io.lark.column.compute.Simd;

import io.lark.column.backend.Backend;
import io.lark.column.backend.BackendFactory;
import io.lark.column.exec.ComputeGraph;
import io.lark.column.exec.ComputeNode;
import io.lark.column.exec.ExecutionContext;
import io.lark.column.op.OpContext;
import io.lark.column.op.OpSpec;
import io.lark.column.op.OpTensor;
import io.lark.column.op.TensorOps;
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

@SuppressWarnings("deprecation")
class ExecutionGraphTest {

    private static Tensor run(TensorStore store, OpSpec spec) {
        OpTensor op = TensorOps.create(spec);
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

        OpTensor op = backend.createOp(new OpSpec("add",
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
        assertEquals(List.of("m1", "m1", "m2"), stats.stats().ops().stream()
                .map(op -> op.module()).toList());
        assertEquals(2, stats.stats().modules().size());
        assertFalse(stats.stats().pressure().isEmpty());

        ExecutionContext async = new ExecutionContext();
        async.store().set("x", Tensor.fromLongs(1, 2, 3));
        ColumnStatsCollector asyncStats = new ColumnStatsCollector();
        async.setMonitor(asyncStats);
        try (ExecutorService pool = Executors.newFixedThreadPool(2)) {
            graph.executeAsync(pool, async);
        }
        assertEquals(18.0, async.store().get("total").getDouble(0), 1e-9);
        assertEquals(2, asyncStats.stats().computeWorkers());
        assertEquals(2, asyncStats.stats().ops().stream()
                .map(op -> op.module()).distinct().count());
        assertTrue(asyncStats.stats().ops().stream()
                .allMatch(op -> op.module().equals("m1") || op.module().equals("m2")));
    }

    @Test
    void computeGraphAsyncHandlesDeepAndWideGraphs() {
        ComputeGraph graph = new ComputeGraph();
        graph.addNode("deep0", "m", TensorOps.create(
                new OpSpec("add_scalar", List.of("x"), List.of("deep0"),
                        Map.of("scalar", "1"))));
        for (int i = 1; i < 50; i++) {
            String previous = "deep" + (i - 1);
            String current = "deep" + i;
            graph.addNode(current, "m", TensorOps.create(
                    new OpSpec("add_scalar", List.of(previous), List.of(current),
                            Map.of("scalar", "1"))));
            graph.addDependency(current, previous);
        }
        for (int i = 0; i < 32; i++) {
            String output = "wide" + i;
            graph.addNode(output, "m", TensorOps.create(
                    new OpSpec("mul_scalar", List.of("x"), List.of(output),
                            Map.of("scalar", "2"))));
            graph.addDependency(output, "deep0");
        }

        ExecutionContext ctx = new ExecutionContext();
        ctx.store().set("x", Tensor.fromLongs(1, 2, 3));
        try (ExecutorService pool = Executors.newFixedThreadPool(2)) {
            graph.executeAsync(pool, ctx);
        }

        assertEquals(51, ctx.store().get("deep49").getLong(0));
        for (int i = 0; i < 32; i++) {
            assertEquals(2, ctx.store().get("wide" + i).getLong(0));
        }
        assertTrue(graph.nodes().stream()
                .allMatch(node -> node.status() == ComputeNode.Status.SUCCESS));
    }

    @Test
    void opSpecAndKernelLayersAreSeparated() {
        ComputeGraph graph = new ComputeGraph();
        OpSpec source = new OpSpec("const", List.of(), List.of("a"),
                Map.of("value", "2", "dtype", "float64", "shape", "2x2"));
        graph.addNode("a", "m", source, TensorKernels.create(source));

        OpSpec add = new OpSpec("add", List.of("a", "a"), List.of("b"));
        graph.addNode("b", "m", add, TensorKernels.create(add));
        graph.addDependency("b", "a");

        ExecutionContext ctx = new ExecutionContext();
        try (ExecutorService pool = Executors.newFixedThreadPool(2)) {
            graph.executeAsync(pool, ctx);
        }

        assertEquals(2.0, ctx.store().get("a").getDouble(0), 1e-9);
        assertEquals(4.0, ctx.store().get("b").getDouble(0), 1e-9);
        assertEquals("const", graph.nodes().get(0).opSpec().type());
        assertNotNull(graph.nodes().get(0).kernel());
    }

    @Test
    void tensorKernelsCoverScalarUnaryAndReductionOps() {
        TensorStore store = new TensorStore();
        store.set("a", Tensor.fromDoubles(1, 2, 3));

        runKernel(store, new OpSpec("mul_scalar", List.of("a"), List.of("b"),
                Map.of("scalar", "2")));
        assertEquals(6.0, store.get("b").getDouble(2), 1e-9);

        runKernel(store, new OpSpec("square", List.of("b"), List.of("c")));
        assertEquals(36.0, store.get("c").getDouble(2), 1e-9);

        runKernel(store, new OpSpec("sum", List.of("c"), List.of("total")));
        assertEquals(56.0, store.get("total").getDouble(0), 1e-9);

        runKernel(store, new OpSpec("gt_scalar", List.of("b"), List.of("mask"),
                Map.of("scalar", "4")));
        assertEquals(1L, store.get("mask").getLong(2));

        runKernel(store, new OpSpec("mul_add", List.of("a", "a", "a"),
                List.of("adjusted")));
        assertEquals(2.0, store.get("adjusted").getDouble(0), 1e-9);
        assertEquals(12.0, store.get("adjusted").getDouble(2), 1e-9);
    }

    private static void runKernel(TensorStore store, OpSpec spec) {
        TensorKernel kernel = TensorKernels.create(spec);
        Tensor[] inputs = new Tensor[spec.inputs().size()];
        for (int i = 0; i < inputs.length; i++) {
            inputs[i] = store.get(spec.inputs().get(i));
        }
        KernelContext ctx = new KernelContext(store, inputs,
                tensor -> store.set(spec.outputs().get(0), tensor));
        kernel.compute(ctx);
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
    void simdIsLoaded() {
        assertNotNull(Simd.summary());
        assertTrue(Simd.doubleLanes() > 0);
        assertTrue(Simd.byteLanes() > 0);
        assertTrue(Simd.shortLanes() > 0);
    }
}
