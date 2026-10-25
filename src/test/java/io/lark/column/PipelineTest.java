// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column;

import io.lark.column.biz.Module;
import io.lark.column.biz.Ops;
import io.lark.column.biz.Pipeline;
import io.lark.column.compute.Compute;
import io.lark.column.exec.ComputeGraph;
import io.lark.column.exec.ExecutionContext;
import io.lark.column.exec.OpContext;
import io.lark.column.exec.OpSpec;
import io.lark.column.exec.TensorOps;
import io.lark.column.monitor.RunStats;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PipelineTest {

    @Test
    void pipelineFeedComputeFetch() {
        Module feature = new Module("feature");
        feature.input("x").input("y")
                .output("scaled").output("score").output("filtered")
                .output("total").output("max_score")
                .fromDsl(
                        "scaled = x * 2\n"
                                + "score = scaled + y\n"
                                + "mask = x > 3\n"
                                + "filtered = filter(score, mask)\n"
                                + "total = sum(score)\n"
                                + "max_score = max(score)\n");

        try (Pipeline pipeline = new Pipeline()) {
            pipeline.addModule(feature);
            pipeline.compile();
            assertTrue(pipeline.nodeCount() >= 12);

            List<Row> rows = new ArrayList<>();
            for (int i = 1; i <= 5; i++) {
                rows.add(Row.of(Cell.of(i), Cell.of(i * 10)));
            }

            pipeline.feed(List.of("x", "y"), rows);
            assertTrue(pipeline.context().store().has("x"));
            assertEquals(5, pipeline.context().store().get("x").size());

            pipeline.compute();

            List<Row> scoreRows = pipeline.fetch(List.of("scaled", "score"));
            assertEquals(5, scoreRows.size());
            assertEquals(12L, scoreRows.get(0).get(1).asLong());
            assertEquals(10L, scoreRows.get(4).get(0).asLong());

            List<Row> filtered = pipeline.fetch(List.of("filtered"));
            assertEquals(2, filtered.size());
            assertEquals(48L, filtered.get(0).get(0).asLong());
            assertEquals(60L, filtered.get(1).get(0).asLong());

            List<Row> totals = pipeline.fetch(List.of("total", "max_score"));
            assertEquals(180.0, totals.get(0).get(0).asDouble(), 1e-9);
            assertEquals(60L, totals.get(0).get(1).asLong());
            assertEquals(180.0, pipeline.fetchScalar("total"), 1e-9);

            RunStats stats = pipeline.stats();
            assertTrue(stats.feedElapsedNanos() >= 0);
            assertTrue(stats.computeElapsedNanos() >= 0);
            assertTrue(stats.fetchElapsedNanos() >= 0);
            assertFalse(stats.modules().isEmpty());
            assertFalse(stats.pressure().isEmpty());
            assertTrue(stats.modules().stream()
                    .anyMatch(module -> module.module().equals("feature")
                            && module.nodeCount() >= 12));
            RunStats.CpuPressure pressure = stats.pressure().get(0);
            assertEquals("compute", pressure.pool());
            assertTrue(pressure.utilization() >= 0.0 && pressure.utilization() <= 1.0);
            assertTrue(pressure.workers() > 0);
        }
    }

    @Test
    void crossModuleWiringAndValidation() {
        Module a = new Module("a");
        a.input("x").output("scaled").fromDsl("scaled = x * 2");

        Module b = new Module("b");
        b.input("x").input("scaled").output("final").fromDsl("final = scaled + x");

        Module c = new Module("c");
        c.input("x").output("sumx").dependsOn("a").fromDsl("sumx = sum(x)");

        try (Pipeline pipeline = new Pipeline()) {
            pipeline.addModule(a).addModule(b).addModule(c);
            pipeline.compile();

            List<Row> rows = List.of(
                    Row.of(Cell.of(1)),
                    Row.of(Cell.of(2)),
                    Row.of(Cell.of(3)));
            pipeline.feed(List.of("x"), rows);
            pipeline.compute();

            List<Row> out = pipeline.fetch(List.of("final"));
            assertEquals(3L, out.get(0).get(0).asLong());
            assertEquals(6L, out.get(1).get(0).asLong());
            assertEquals(6.0, pipeline.fetchScalar("sumx"), 1e-9);
        }

        Module a2 = new Module("a2");
        a2.input("x").output("dup").fromDsl("dup = x + 1");
        Module b2 = new Module("b2");
        b2.input("x").output("dup").fromDsl("dup = x + 2");
        try (Pipeline bad = new Pipeline()) {
            bad.addModule(a2).addModule(b2);
            assertThrows(IllegalArgumentException.class, bad::compile);
        }

        Module d = new Module("d");
        d.input("x").output("v").dependsOn("nope").fromDsl("v = x");
        try (Pipeline bad2 = new Pipeline()) {
            bad2.addModule(d);
            assertThrows(IllegalArgumentException.class, bad2::compile);
        }
    }

    @Test
    void quantizationInsidePipeline() {
        Module quant = new Module("quant");
        quant.input("f").output("max_err").output("sim")
                .fromDsl(
                        "q = quantize(f)\n"
                                + "dq = dequantize(q)\n"
                                + "err = dq - f\n"
                                + "aerr = abs(err)\n"
                                + "max_err = max(aerr)\n"
                                + "sim = dot(quantize(f), quantize(f))\n");

        try (Pipeline pipeline = new Pipeline()) {
            pipeline.addModule(quant);
            pipeline.compile();

            List<Row> rows = new ArrayList<>();
            for (int i = 0; i < 40; i++) {
                rows.add(Row.of(Cell.of((double) i)));
            }
            pipeline.feed(List.of("f"), rows);
            pipeline.compute();

            double maxError = pipeline.fetchScalar("max_err");
            double similarity = pipeline.fetchScalar("sim");
            assertTrue(maxError < 0.5, "max error " + maxError);
            double expected = 0.0;
            for (int i = 0; i < 40; i++) expected += (double) i * i;
            assertTrue(Math.abs(similarity - expected) / expected < 0.05);
        }
    }

    @Test
    void pureCodeOrchestration() {
        Module module = new Module("code");
        module.input("x").output("out");
        module.op(Ops.mulScalar("scaled", "x", 3.0));
        module.op(Ops.addScalar("out", "scaled", 1.0));

        try (Pipeline pipeline = new Pipeline()) {
            pipeline.addModule(module);
            pipeline.compile();

            pipeline.feed(List.of("x"), List.of(
                    Row.of(Cell.of(1)),
                    Row.of(Cell.of(2))));
            pipeline.compute();

            List<Row> out = pipeline.fetch(List.of("out"));
            assertEquals(4L, out.get(0).get(0).asLong());
            assertEquals(7L, out.get(1).get(0).asLong());
        }
    }

    @Test
    void runConvenienceAndGraphReuse() {
        Module module = new Module("linear");
        module.input("x").output("y").fromDsl("y = x * 2 + 1");

        try (Pipeline pipeline = new Pipeline()) {
            pipeline.addModule(module);
            pipeline.compile();

            List<Row> first = pipeline.run(List.of("x"),
                    List.of(Row.of(Cell.of(1)), Row.of(Cell.of(2))),
                    List.of("y"));
            assertEquals(3L, first.get(0).get(0).asLong());
            assertEquals(5L, first.get(1).get(0).asLong());

            List<Row> second = pipeline.run(List.of("x"),
                    List.of(Row.of(Cell.of(10))), List.of("y"));
            assertEquals(21L, second.get(0).get(0).asLong());
        }
    }

    @Test
    void edgeCases() {
        TensorStore store = new TensorStore();
        store.set("empty", Tensor.zeros(0, DType.INT64));

        TensorOps.create(new OpSpec("max", List.of("empty"), List.of("mx")))
                .compute(new OpContext(store));
        assertEquals(1, store.get("mx").size());
        assertEquals(0, store.get("mx").getLong(0));

        TensorOps.create(new OpSpec("min", List.of("empty"), List.of("mn")))
                .compute(new OpContext(store));
        assertEquals(0, store.get("mn").getLong(0));

        TensorOps.create(new OpSpec("sum", List.of("empty"), List.of("sm")))
                .compute(new OpContext(store));
        assertEquals(0.0, store.get("sm").getDouble(0), 1e-9);

        store.set("a3", Tensor.fromLongs(1, 2, 3));
        store.set("b2", Tensor.fromLongs(1, 2));
        assertThrows(IllegalArgumentException.class,
                () -> TensorOps.create(new OpSpec("add",
                        List.of("a3", "b2"), List.of("bad")))
                        .compute(new OpContext(store)));
        assertThrows(IllegalArgumentException.class,
                () -> Compute.gt(store.get("a3"), store.get("b2")));

        Tensor zeroQ8 = Tensor.zeros(40, DType.Q8_0);
        Tensor dequantized = zeroQ8.dequantize();
        for (int i = 0; i < dequantized.size(); i++) {
            assertEquals(0.0, dequantized.getDouble(i), 1e-9);
        }

        Module duplicate = new Module("duplicate");
        duplicate.input("x").output("o");
        duplicate.op(Ops.addScalar("o", "x", 1.0));
        duplicate.op(Ops.addScalar("o", "x", 2.0));
        try (Pipeline bad = new Pipeline()) {
            bad.addModule(duplicate);
            assertThrows(IllegalArgumentException.class, bad::compile);
        }
    }

    @Test
    void placeholderForwardReferencesAndCycles() {
        Module a = new Module("a");
        a.input("x").input("b_out").output("final").fromDsl("final = b_out + x");
        Module b = new Module("b");
        b.input("x").output("b_out").fromDsl("b_out = x * 2");

        try (Pipeline pipeline = new Pipeline()) {
            pipeline.addModule(a).addModule(b);
            pipeline.compile();

            assertFalse(pipeline.graph().hasPlaceholders());
            assertTrue(pipeline.graph().find("@placeholder:b_out").isEmpty());

            pipeline.feed(List.of("x"), List.of(Row.of(Cell.of(3))));
            pipeline.compute();
            assertEquals(9.0, pipeline.fetchScalar("final"), 1e-9);
        }

        Module a2 = new Module("a2");
        a2.input("x").output("out").dependsOn("b2").fromDsl("out = x + 1");
        Module b2 = new Module("b2");
        b2.input("x").output("marker").fromDsl("marker = x * 100");

        try (Pipeline pipeline = new Pipeline()) {
            pipeline.addModule(a2).addModule(b2);
            pipeline.compile();

            assertFalse(pipeline.graph().hasPlaceholders());
            var dependencyNode = pipeline.graph().find("a2/@dep:b2");
            assertTrue(dependencyNode.isPresent());
            assertFalse(dependencyNode.orElseThrow().dependencies().isEmpty());

            pipeline.feed(List.of("x"), List.of(Row.of(Cell.of(2))));
            pipeline.compute();
            assertEquals(3.0, pipeline.fetchScalar("out"), 1e-9);
            assertEquals(200.0, pipeline.fetchScalar("marker"), 1e-9);
        }

        Module ca = new Module("ca");
        ca.input("b_out").output("a_out").fromDsl("a_out = b_out + 1");
        Module cb = new Module("cb");
        cb.input("a_out").output("b_out").fromDsl("b_out = a_out + 1");
        try (Pipeline cycle = new Pipeline()) {
            cycle.addModule(ca).addModule(cb);
            assertThrows(IllegalStateException.class, cycle::compile);
        }
    }

    @Test
    void computeGraphCanBeBuiltDirectly() {
        ComputeGraph graph = new ComputeGraph();
        graph.addNode("sqrt", "m", TensorOps.create(
                new OpSpec("sqrt", List.of("x"), List.of("r"))));
        graph.finalizeGraph();

        ExecutionContext ctx = new ExecutionContext();
        ctx.store().set("x", Tensor.fromDoubles(4.0, 9.0, 16.0));
        graph.execute(ctx);
        assertNotNull(ctx.store().get("r"));
        assertEquals(2.0, ctx.store().get("r").getDouble(0), 1e-9);
        assertEquals(4.0, ctx.store().get("r").getDouble(2), 1e-9);
    }
}
