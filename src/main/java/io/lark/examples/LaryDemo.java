// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples;

import io.lark.column.Cell;
import io.lark.column.DType;
import io.lark.column.Row;
import io.lark.column.Simd;
import io.lark.column.Tensor;
import io.lark.column.TensorStore;
import io.lark.column.biz.ColumnContext;
import io.lark.column.biz.ColumnNode;
import io.lark.column.biz.NodePipeline;
import io.lark.column.compute.Compute;
import io.lark.column.op.FloatAddTensor;
import io.lark.column.op.FloatMultiTensor;
import io.lark.column.op.OpContext;
import io.lark.column.op.OpSpec;
import io.lark.column.op.OpTensor;
import io.lark.column.op.TensorOps;
import io.lark.dag.DefaultContext;
import io.lark.dag.Executor;
import io.lark.dag.Graph;
import io.lark.dag.GraphBuilder;
import io.lark.dag.IContext;
import io.lark.dag.NodeRegistry;
import io.lark.dag.NodeStatus;
import io.lark.dag.TimeoutPolicy;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * End-to-end demo for the layered column engine.
 *
 * <p>Run with {@code ./gradlew runDemo} (or {@code ./gradlew run}).
 *
 * <p>Sections:
 * <ol>
 *   <li>Business layer: {@link ColumnNode} graph → feed / compute / fetch.</li>
 *   <li>Tensor-op layer: {@link FloatAddTensor} / {@link FloatMultiTensor}.</li>
 *   <li>Business fallback + DAG timeout / quarantine integration.</li>
 * </ol>
 */
public final class LaryDemo {

    // ─────────────────────────────────────────────────────────────────────
    // Section 1: business ColumnNode graph
    // ─────────────────────────────────────────────────────────────────────

    static final class FetchUserNode extends ColumnNode {
        @Override
        public String name() {
            return "fetch_user";
        }

        @Override
        public List<String> inputs() {
            return List.of("user_id");
        }

        @Override
        public List<String> outputs() {
            return List.of("user");
        }

        @Override
        public void execute(ColumnContext ctx) {
            ctx.output("user", Compute.addScalar(
                    Compute.mulScalar(ctx.input("user_id"), 10.0), 100.0));
        }
    }

    static final class ScoreNode extends ColumnNode {
        @Override
        public String name() {
            return "score";
        }

        @Override
        public List<String> inputs() {
            return List.of("user");
        }

        @Override
        public List<String> outputs() {
            return List.of("score");
        }

        @Override
        public void execute(ColumnContext ctx) {
            ctx.output("score", Compute.mulScalar(ctx.input("user"), 2.0));
        }
    }

    static final class HighScoreMaskNode extends ColumnNode {
        @Override
        public String name() {
            return "high_score_mask";
        }

        @Override
        public List<String> inputs() {
            return List.of("score");
        }

        @Override
        public List<String> outputs() {
            return List.of("high_mask");
        }

        @Override
        public void execute(ColumnContext ctx) {
            ctx.output("high_mask", Compute.gtScalar(ctx.input("score"), 250.0));
        }
    }

    static final class FilterHighScoresNode extends ColumnNode {
        @Override
        public String name() {
            return "filter_high_scores";
        }

        @Override
        public List<String> inputs() {
            return List.of("score", "high_mask");
        }

        @Override
        public List<String> outputs() {
            return List.of("high_scores");
        }

        @Override
        public void execute(ColumnContext ctx) {
            ctx.output("high_scores",
                    Compute.filter(ctx.input("score"), ctx.input("high_mask")));
        }
    }

    static final class AggregateNode extends ColumnNode {
        @Override
        public String name() {
            return "aggregate";
        }

        @Override
        public List<String> inputs() {
            return List.of("score");
        }

        @Override
        public List<String> outputs() {
            return List.of("total", "mean", "max");
        }

        @Override
        public void execute(ColumnContext ctx) {
            Tensor score = ctx.input("score");
            ctx.output("total", Compute.sumTensor(score));
            ctx.output("mean", Compute.meanTensor(score));
            ctx.output("max", Compute.maxTensor(score));
        }
    }

    static final class FailingNode extends ColumnNode {
        @Override
        public String name() {
            return "failing";
        }

        @Override
        public List<String> outputs() {
            return List.of("recovered");
        }

        @Override
        public void execute(ColumnContext ctx) {
            throw new IllegalStateException("simulated business failure");
        }

        @Override
        public boolean fallback(ColumnContext ctx, Throwable error) {
            ctx.output("recovered", Tensor.fromLongs(7));
            return true;
        }
    }

    static final class UseRecoveredNode extends ColumnNode {
        @Override
        public String name() {
            return "use_recovered";
        }

        @Override
        public List<String> inputs() {
            return List.of("recovered");
        }

        @Override
        public List<String> outputs() {
            return List.of("recovered_plus_one");
        }

        @Override
        public void execute(ColumnContext ctx) {
            ctx.output("recovered_plus_one",
                    Compute.addScalar(ctx.input("recovered"), 1.0));
        }
    }

    // ─────────────────────────────────────────────────────────────────────
    // Section 3: DAG timeout demo
    // ─────────────────────────────────────────────────────────────────────

    static final class SlowDagNode extends io.lark.dag.Node {
        @Override
        public void compute(IContext ctx) {
            try {
                Thread.sleep(2_000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("interrupted", e);
            }
        }

        @Override
        public boolean fallback(IContext ctx, Throwable error) {
            ctx.set("dag_fallback", 1);
            return true;
        }
    }

    private LaryDemo() {
    }

    public static void main(String[] args) {
        System.out.println("SIMD: " + Simd.summary());
        businessNodeDemo();
        tensorOpDemo();
        timeoutDemo();
        System.out.println("\nDemo complete. Business code never touched ComputeNode/ComputeGraph.");
    }

    private static void businessNodeDemo() {
        System.out.println("\n=== 1. Business ColumnNode pipeline ===");

        AtomicInteger fallbackEvents = new AtomicInteger();
        try (NodePipeline pipeline = new NodePipeline(4)) {
            pipeline.addNodes(
                    new FetchUserNode(),
                    new ScoreNode(),
                    new HighScoreMaskNode(),
                    new FilterHighScoresNode(),
                    new AggregateNode(),
                    new FailingNode(),
                    new UseRecoveredNode());
            pipeline.setMonitor(event -> {
                if ("node.fallback".equals(event.action())) {
                    fallbackEvents.incrementAndGet();
                }
            });
            pipeline.compile();

            List<Row> rows = new ArrayList<>();
            for (int i = 1; i <= 6; i++) {
                rows.add(Row.of(Cell.of(i)));
            }
            pipeline.feed(List.of("user_id"), rows);
            pipeline.compute();

            List<Row> scoreRows = pipeline.fetch(List.of("user", "score", "high_mask"));
            System.out.println("user  = " + longs(scoreRows, 0));
            System.out.println("score = " + longs(scoreRows, 1));
            System.out.println("mask  = " + longs(scoreRows, 2));
            System.out.println("high  = " + longs(pipeline.fetch(List.of("high_scores")), 0));
            System.out.println("total = " + pipeline.fetchScalar("total"));
            System.out.println("mean  = " + pipeline.fetchScalar("mean"));
            System.out.println("max   = " + pipeline.fetchScalar("max"));
            System.out.println("fallback output = " + pipeline.fetchScalar("recovered_plus_one"));
            System.out.println("fallback events = " + fallbackEvents.get());
            System.out.print(pipeline.stats().summary());
        }
    }

    private static void tensorOpDemo() {
        System.out.println("\n=== 2. Typed OpTensor layer ===");

        TensorStore store = new TensorStore();
        store.set("a", Tensor.fromFloats(1.0f, 2.0f, 3.0f));
        store.set("b", Tensor.fromFloats(10.0f, 20.0f, 30.0f));

        OpTensor add = new FloatAddTensor(List.of("a", "b"), List.of("sum"));
        add.compute(new OpContext(store));

        OpTensor multi = new FloatMultiTensor(List.of("a", "b"), List.of("product"));
        multi.compute(new OpContext(store));

        System.out.println("FloatAddTensor   sum     = " + floats(store, "sum"));
        System.out.println("FloatMultiTensor product = " + floats(store, "product"));

        // Runtime dtype dispatch: int64 + float64 -> Float64 add.
        TensorStore mixed = new TensorStore();
        mixed.set("i", Tensor.fromLongs(1, 2, 3));
        mixed.set("d", Tensor.fromDoubles(0.5, 1.5, 2.5));
        OpTensor dispatched = TensorOps.create(
                new OpSpec("add", List.of("i", "d"), List.of("r")));
        dispatched.compute(new OpContext(mixed));
        Tensor result = mixed.get("r");
        System.out.println("dispatched add: dtype=" + result.dtype()
                + ", values=" + doubles(result));
    }

    private static void timeoutDemo() {
        System.out.println("\n=== 3. DAG timeout + business fallback ===");

        SlowDagNode slow = new SlowDagNode();
        NodeRegistry registry = new NodeRegistry();
        registry.register("slow", () -> slow);
        Graph graph = new GraphBuilder(registry).build(
                new io.lark.dag.NodeDef("slow"));

        DefaultContext ctx = new DefaultContext();
        long start = System.nanoTime();
        try (Executor executor = new Executor(2, 2)) {
            executor.setTimeoutPolicy(TimeoutPolicy.builder()
                    .defaultTimeout(Duration.ofMillis(80))
                    .maxConsecutiveTimeouts(2)
                    .quarantineDuration(Duration.ofSeconds(30))
                    .build());
            executor.execute(graph, ctx);
        }
        long millis = (System.nanoTime() - start) / 1_000_000;

        System.out.println("status    = " + slow.status());
        System.out.println("fallback  = " + ctx.has("dag_fallback", Integer.class));
        System.out.println("elapsed   = " + millis + "ms");
        if (slow.status() == NodeStatus.FALLBACK) {
            System.out.println("timeout was converted to a degraded result.");
        }
    }

    private static List<Long> longs(List<Row> rows, int column) {
        return rows.stream().map(row -> row.get(column).asLong()).toList();
    }

    private static List<Float> floats(TensorStore store, String name) {
        Tensor tensor = store.get(name);
        List<Float> values = new ArrayList<>(tensor.size());
        for (int i = 0; i < tensor.size(); i++) {
            values.add(tensor.getFloat(i));
        }
        return values;
    }

    private static List<Double> doubles(Tensor tensor) {
        List<Double> values = new ArrayList<>(tensor.size());
        for (int i = 0; i < tensor.size(); i++) {
            values.add(tensor.getDouble(i));
        }
        return values;
    }
}
