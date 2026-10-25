// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples;

import io.lark.column.Cell;
import io.lark.column.Row;
import io.lark.column.Simd;
import io.lark.column.Tensor;
import io.lark.column.TensorStore;
import io.lark.column.biz.BinaryNode;
import io.lark.column.biz.BoolColumn;
import io.lark.column.biz.ColumnComponent;
import io.lark.column.biz.ColumnContext;
import io.lark.column.biz.ColumnModule;
import io.lark.column.biz.ColumnNode;
import io.lark.column.biz.ColumnPipeline;
import io.lark.column.biz.DoubleColumn;
import io.lark.column.biz.FeedStream;
import io.lark.column.biz.FetchStream;
import io.lark.column.biz.LongColumn;
import io.lark.column.biz.ScaleNode;
import io.lark.column.biz.SourceNode;
import io.lark.column.biz.UnaryNode;
import io.lark.column.biz.UnitConvertNode;
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
 * End-to-end demo for the typed module / stream API.
 *
 * <p>Business nodes have no string outputs or positional feeds. Column classes
 * define the data contract, nodes are typed unary/binary classes, and modules
 * expose typed fetch streams.
 *
 * <p>Run with {@code ./gradlew :examples:runDemo}.
 */
public final class LaryDemo {

    // ---- typed schema ----------------------------------------------------

    static final class UserId extends LongColumn {
    }

    static final class User extends LongColumn {
    }

    static final class Score extends LongColumn {
    }

    static final class HighMask extends BoolColumn {
    }

    static final class HighScores extends LongColumn {
    }

    static final class Total extends DoubleColumn {
    }

    static final class Mean extends DoubleColumn {
    }

    static final class Max extends LongColumn {
    }

    static final class Recovered extends LongColumn {
    }

    static final class RecoveredPlusOne extends LongColumn {
    }

    static final class UserLi extends LongColumn {
    }

    static final class Weighted extends LongColumn {
    }

    static final class Bonus extends LongColumn {
    }

    // ---- nodes -----------------------------------------------------------

    static final class FetchUserNode extends UnaryNode<UserId, User> {

        FetchUserNode(UserId userId) {
            super(userId, User.class);
        }

        @Override
        protected Tensor compute(ColumnContext ctx, Tensor userId) {
            return Compute.addScalar(Compute.mulScalar(userId, 10.0), 100.0);
        }
    }

    static final class ScoreNode extends UnaryNode<User, Score> {

        ScoreNode(User user) {
            super(user, Score.class);
        }

        @Override
        protected Tensor compute(ColumnContext ctx, Tensor user) {
            return Compute.mulScalar(user, 2.0);
        }
    }

    static final class HighScoreMaskNode extends UnaryNode<Score, HighMask> {

        HighScoreMaskNode(Score score) {
            super(score, HighMask.class);
        }

        @Override
        protected Tensor compute(ColumnContext ctx, Tensor score) {
            return Compute.gtScalar(score, 250.0);
        }
    }

    static final class FilterHighScoresNode
            extends BinaryNode<Score, HighMask, HighScores> {

        FilterHighScoresNode(Score score, HighMask mask) {
            super(score, mask, HighScores.class);
        }

        @Override
        protected Tensor compute(ColumnContext ctx, Tensor score, Tensor mask) {
            return Compute.filter(score, mask);
        }
    }

    static final class AggregateNode extends ColumnNode {

        private final Score score;
        private final Total total = fetch(Total.class);
        private final Mean mean = fetch(Mean.class);
        private final Max max = fetch(Max.class);

        AggregateNode(Score score) {
            super(score);
            this.score = score;
        }

        @Override
        public void compute(ColumnContext ctx) {
            Tensor scores = ctx.read(score);
            ctx.write(total, Compute.sumTensor(scores));
            ctx.write(mean, Compute.meanTensor(scores));
            ctx.write(max, Compute.maxTensor(scores));
        }
    }

    static final class FailingNode extends SourceNode<Recovered> {

        FailingNode() {
            super(Recovered.class);
        }

        @Override
        protected Tensor produce(ColumnContext ctx) {
            throw new IllegalStateException("simulated business failure");
        }

        @Override
        public boolean fallback(ColumnContext ctx, Throwable error) {
            ctx.write(out(), Tensor.fromLongs(7));
            return true;
        }
    }

    static final class UseRecoveredNode
            extends UnaryNode<Recovered, RecoveredPlusOne> {

        UseRecoveredNode(Recovered recovered) {
            super(recovered, RecoveredPlusOne.class);
        }

        @Override
        protected Tensor compute(ColumnContext ctx, Tensor recovered) {
            return Compute.addScalar(recovered, 1.0);
        }
    }

    static final class BonusNode extends UnaryNode<Weighted, Bonus> {

        BonusNode(Weighted weighted) {
            super(weighted, Bonus.class);
        }

        @Override
        protected Tensor compute(ColumnContext ctx, Tensor weighted) {
            return Compute.addScalar(weighted, 1.0);
        }
    }

    /** A business module: typed nodes wired by column classes. */
    @ColumnComponent
    static final class FeatureModule extends ColumnModule {
        final UserId userId = input(UserId.class);
        final FetchUserNode fetch = add(new FetchUserNode(userId));
        final ScoreNode score = add(new ScoreNode(fetch.out()));
        final HighScoreMaskNode mask = add(new HighScoreMaskNode(score.out()));
        final FilterHighScoresNode filter =
                add(new FilterHighScoresNode(score.out(), mask.out()));
        final AggregateNode aggregate = add(new AggregateNode(score.out()));
        final FailingNode failing = add(new FailingNode());
        final UseRecoveredNode recovered =
                add(new UseRecoveredNode(failing.out()));
        final UnitConvertNode<UserLi> li =
                add(new UnitConvertNode<>(fetch.out(), UserLi.class, 10.0));
        final ScaleNode<Weighted> weighted =
                add(new ScaleNode<>(li.out(), Weighted.class, 2.0));
        final BonusNode bonus = add(new BonusNode(weighted.out()));

        {
            output(fetch.out(), score.out(), mask.out(), filter.out(),
                    aggregate.total, aggregate.mean, aggregate.max,
                    failing.out(), recovered.out(), li.out(), weighted.out(),
                    bonus.out());
        }
    }

    /** A module with an unused branch, used to demonstrate pruning. */
    static final class PruneModule extends ColumnModule {
        final UserId userId = input(UserId.class);
        final FetchUserNode fetch = add(new FetchUserNode(userId));
        final ScoreNode score = add(new ScoreNode(fetch.out()));
        final HighScoreMaskNode unused = add(new HighScoreMaskNode(score.out()));

        {
            output(score.out(), unused.out());
        }
    }

    // ---- DAG timeout demo ------------------------------------------------

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
        businessModuleDemo();
        pruningAndCycleDemo();
        tensorOpDemo();
        timeoutDemo();
        System.out.println("\nDemo complete. Business code never touched ComputeNode/ComputeGraph.");
    }

    private static void businessModuleDemo() {
        System.out.println("\n=== 1. Business module / typed stream pipeline ===");

        AtomicInteger fallbackEvents = new AtomicInteger();
        try (ColumnPipeline pipeline = new ColumnPipeline(4)) {
            // Spring-style @ColumnComponent discovery.
            pipeline.scan("io.lark.examples");
            FeatureModule feature = pipeline.module(FeatureModule.class);
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
            pipeline.feed(FeedStream.of(feature.userId, rows));
            pipeline.compute();

            List<Row> scoreRows = pipeline.fetch(FetchStream.of(
                    feature.fetch.out(), feature.score.out(), feature.mask.out(),
                    feature.li.out(), feature.weighted.out(), feature.bonus.out()));
            System.out.println("user     = " + longs(scoreRows, 0));
            System.out.println("score    = " + longs(scoreRows, 1));
            System.out.println("mask     = " + longs(scoreRows, 2));
            System.out.println("user_li  = " + longs(scoreRows, 3));
            System.out.println("weighted = " + longs(scoreRows, 4));
            System.out.println("bonus    = " + longs(scoreRows, 5));
            System.out.println("high     = " + longs(pipeline.fetch(
                    FetchStream.of(feature.filter.out())), 0));
            System.out.println("total    = "
                    + pipeline.fetchScalar(feature.aggregate.total));
            System.out.println("mean     = "
                    + pipeline.fetchScalar(feature.aggregate.mean));
            System.out.println("max      = "
                    + pipeline.fetchScalar(feature.aggregate.max));
            System.out.println("fallback output = "
                    + pipeline.fetchScalar(feature.recovered.out()));
            System.out.println("fallback events = " + fallbackEvents.get());
            System.out.print(pipeline.stats().summary());
        }
    }

    private static void pruningAndCycleDemo() {
        System.out.println("\n=== 2. Graph pruning + cycle detection ===");

        PruneModule module = new PruneModule();
        try (ColumnPipeline pipeline = new ColumnPipeline()) {
            pipeline.add(module);
            // Only score is requested; the unused mask branch is pruned.
            pipeline.compile(FetchStream.of(module.score.out()));
            System.out.println("registered nodes = " + pipeline.nodeCount()
                    + ", compiled nodes = " + pipeline.compiledNodeCount());

            pipeline.feed(FeedStream.of(module.userId,
                    List.of(Row.of(Cell.of(7)))));
            pipeline.compute();
            System.out.println("pruned score = "
                    + pipeline.fetchScalar(module.score.out()));
        }

        // Module-level cycle: A depends on B, B depends on A.
        try (ColumnPipeline cycle = new ColumnPipeline()) {
            cycle.add(new CycleA()).add(new CycleB());
            cycle.compile();
            System.out.println("module cycle: NOT detected (unexpected)");
        } catch (IllegalStateException e) {
            System.out.println("module cycle detected: " + e.getMessage());
        }
    }

    static final class CycleA extends ColumnModule {
        {
            dependsOn(CycleB.class);
        }
    }

    static final class CycleB extends ColumnModule {
        {
            dependsOn(CycleA.class);
        }
    }

    private static void tensorOpDemo() {
        System.out.println("\n=== 3. Typed OpTensor layer ===");

        TensorStore store = new TensorStore();
        store.set("a", Tensor.fromFloats(1.0f, 2.0f, 3.0f));
        store.set("b", Tensor.fromFloats(10.0f, 20.0f, 30.0f));

        OpTensor add = new FloatAddTensor(List.of("a", "b"), List.of("sum"));
        add.compute(new OpContext(store));

        OpTensor multi = new FloatMultiTensor(List.of("a", "b"), List.of("product"));
        multi.compute(new OpContext(store));

        System.out.println("FloatAddTensor   sum     = " + floats(store, "sum"));
        System.out.println("FloatMultiTensor product = " + floats(store, "product"));

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
        System.out.println("\n=== 4. DAG timeout + business fallback ===");

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
