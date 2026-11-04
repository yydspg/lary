// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import io.lark.column.ColumnDefinitionException;
import io.lark.column.ColumnExecutionException;
import io.lark.column.ColumnFeedException;
import io.lark.column.ColumnFetchException;
import io.lark.column.biz.op.AddOp;
import io.lark.column.biz.op.ScaleOp;
import io.lark.column.context.ColumnRunContext;
import io.lark.column.data.Cell;
import io.lark.column.tensor.DType;
import io.lark.column.data.Row;
import io.lark.column.tensor.Tensor;
import io.lark.column.tensor.TensorShape;
import io.lark.column.tensor.TensorStore;
import io.lark.column.compute.Compute;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ModulePipelineTest {

    // ---- column schema ---------------------------------------------------

    static final Column<Long> METRIC_VALUE =
            new Column<>("metric_value", DType.INT64);
    static final Column<Long> SOURCE =
            new Column<>("source", DType.INT64);
    static final Column<Long> RESULT =
            new Column<>("result", DType.INT64);
    static final Column<Long> RECOVERED =
            new Column<>("recovered", DType.INT64);
    static final Column<Long> RECOVERED_PLUS_ONE =
            new Column<>("recovered_plus_one", DType.INT64);
    static final Column<Long> USED =
            new Column<>("used", DType.INT64);
    static final Column<Long> UNUSED =
            new Column<>("unused", DType.INT64);
    static final Column<Long> LI =
            new Column<>("li", DType.INT64);
    static final Column<Long> PRODUCER_OUT =
            new Column<>("producer_out", DType.INT64);
    static final Column<Long> CONSUMER_OUT =
            new Column<>("consumer_out", DType.INT64);
    static final Column<Double> ZEROS =
            new Column<>("zeros", DType.FLOAT64);
    static final Column<Long> OPERATOR_LEFT =
            new Column<>("operator_left", DType.INT64);
    static final Column<Long> OPERATOR_RIGHT =
            new Column<>("operator_right", DType.INT64);
    static final Column<Double> OPERATOR_SUM =
            new Column<>("operator_sum", DType.FLOAT64);
    static final Column<Double> OPERATOR_SCALED =
            new Column<>("operator_scaled", DType.FLOAT64);
    static final Column<Double> OPERATOR_WEIGHTED =
            new Column<>("operator_weighted", DType.FLOAT64);
    static final Column<Long> TRADE_PRICE =
            new Column<>("trade_price", DType.INT64);
    static final Column<Double> TRADE_RATE =
            new Column<>("trade_rate", DType.FLOAT64);

    // ---- nodes -----------------------------------------------------------

    static final class PlusNode<O>
            extends UnaryNode<ColumnStream<Long>, O> {

        private final double delta;

        PlusNode(ColumnStream<Long> feed, Column<O> output, double delta) {
            super(feed, output);
            this.delta = delta;
        }

        PlusNode(Column<O> output, double delta) {
            super(output);
            this.delta = delta;
        }

        @Override
        protected Tensor compute(ColumnContext ctx, Tensor input) {
            return Compute.addScalar(input, delta);
        }
    }

    static final class ConsumerNode<O>
            extends UnaryNode<ColumnStream<Long>, O> {

        ConsumerNode(ColumnStream<Long> feed, Column<O> output) {
            super(feed, output);
        }

        ConsumerNode(Column<O> output) {
            super(output);
        }

        @Override
        protected Tensor compute(ColumnContext ctx, Tensor input) {
            return Compute.addScalar(input, 1.0);
        }
    }

    static final class TimesTenNode extends UnaryNode<ColumnStream<Long>, Long> {

        public static final NodeSchema<Long> SCHEMA = NodeSchema.of(LI);

        @Override
        protected Tensor compute(ColumnContext ctx, Tensor input) {
            return Compute.mulScalar(input, 10.0);
        }
    }

    static final class FailingNode extends SourceNode<Long> {

        public static final NodeSchema<Long> SCHEMA = NodeSchema.of(RECOVERED);

        FailingNode() {
            super();
        }

        @Override
        protected Tensor produce(ColumnContext ctx) {
            throw new IllegalStateException("boom");
        }

        @Override
        public boolean fallback(ColumnContext ctx, Throwable error) {
            ctx.write(out(), Tensor.fromLongs(7));
            return true;
        }
    }

    // ---- modules ---------------------------------------------------------

    static final class FeatureModule extends ColumnModule {
        ColumnStream<Long> metricValue;
        PlusNode<Long> source;
        ConsumerNode<Long> consumer;
        FailingNode failing;
        ConsumerNode<Long> recovered;

        @Override
        protected void define() {
            metricValue = input(METRIC_VALUE);
            source = node(new PlusNode<>(SOURCE, 1.0), metricValue);
            consumer = node(new ConsumerNode<>(RESULT), source.out());
            failing = node(new FailingNode());
            recovered = node(new ConsumerNode<>(RECOVERED_PLUS_ONE), failing.out());
            output(source.out(), consumer.out(), failing.out(), recovered.out());
        }
    }

    static final class PruneModule extends ColumnModule {
        ColumnStream<Long> metricValue;
        PlusNode<Long> used;
        PlusNode<Long> unused;
        ConsumerNode<Long> result;

        @Override
        protected void define() {
            metricValue = input(METRIC_VALUE);
            used = node(new PlusNode<>(USED, 1.0), metricValue);
            unused = node(new PlusNode<>(UNUSED, 100.0), used.out());
            result = node(new ConsumerNode<>(RESULT), used.out());
            output(used.out(), unused.out(), result.out());
        }
    }

    @Test
    void runConvenienceReturnsAllCompiledOutputs() {
        ComponentModule component = new ComponentModule();
        try (ColumnPipeline pipeline = new ColumnPipeline(2)) {
            pipeline.add(component);
            List<Row> rows = pipeline.run(
                    FeedStream.of(component.metricValue, rows(2)));

            assertEquals(2, rows.size());
            assertEquals(20L, rows.get(0).get(0).asLong());
            assertEquals(30L, rows.get(1).get(0).asLong());
            assertEquals(List.of(Cell.of(20), Cell.of(30)),
                    pipeline.fetchColumns().get(LI.name()));
        }
    }

    @Test
    void fluentColumnarPipelineReportsUsefulStats() {
        ComponentModule component = new ComponentModule();
        try (ColumnPipeline pipeline = new ColumnPipeline(2).addAll(component)) {
            pipeline.compile(FetchStream.of(component.out()))
                    .feedTensors(Map.of(METRIC_VALUE.name(),
                            Tensor.fromLongs(1, 2)))
                    .compute();

            Tensor result = pipeline.fetchTensor(component.out());
            assertEquals(List.of(20L, 30L), List.of(
                    result.getLong(0), result.getLong(1)));

            var stats = pipeline.stats();
            assertEquals(2, stats.feedRows());
            assertEquals(1, stats.feedColumns());
            assertEquals(2, stats.fetchRows());
            assertEquals(1, stats.fetchColumns());
            assertEquals(2, stats.computedNodes());
            assertEquals(2, stats.successCount());
            assertEquals(0, stats.failureCount());
            assertTrue(stats.totalElapsedNanos() > 0);
            assertTrue(stats.summary().contains("rows x"));
        }
    }

    @Test
    void moduleArraysAreSnapshotsAndMapsAreCached() {
        ComponentModule component = new ComponentModule();
        component.initialize();
        ColumnNode[] nodes = component.nodes();
        nodes[0] = null;

        assertNotNull(component.nodes()[0]);
        assertSame(component.feedMap(), component.feedMap());
        assertSame(component.fetchMap(), component.fetchMap());
    }

    @Test
    void failedDefinitionCanBeRetried() {
        FlakyModule module = new FlakyModule();
        assertThrows(IllegalStateException.class, module::feedMap);
        assertTrue(module.feedMap().containsKey(METRIC_VALUE.name()));
        assertEquals(1, module.feedMap().size());
        assertEquals(2, module.attempts);
    }

    @Test
    void failedRecompileKeepsThePreviousPlan() {
        ComponentModule component = new ComponentModule();
        try (ColumnPipeline pipeline = new ColumnPipeline()) {
            pipeline.add(component).compile();
            int compiled = pipeline.compiledNodeCount();
            assertThrows(ColumnFetchException.class,
                    () -> pipeline.compile(List.of("missing_output")));
            assertEquals(compiled, pipeline.compiledNodeCount());
        }
    }

    @Test
    void zerosNodeProducesAShapedTensor() {
        ZeroModule module = new ZeroModule();
        try (ColumnPipeline pipeline = new ColumnPipeline(2)) {
            pipeline.add(module).compile().compute();

            Tensor zeros = pipeline.fetchTensor(module.zeros.out());
            assertEquals(TensorShape.of(2, 3), zeros.shape());
            assertEquals(6, zeros.size());
            assertEquals(0.0, zeros.getDouble(5), 1e-9);
        }
    }

    @Test
    void columnDefinitionProvidesNameAndDtype() {
        assertEquals("metric_value", METRIC_VALUE.name());
        assertEquals(DType.INT64, METRIC_VALUE.dtype());
    }

    @Test
    void dtypeMismatchFailsAtTheBusinessBoundary() {
        ColumnContext ctx = new ColumnContext(new TensorStore());
        ColumnStream<Long> stream = new ColumnStream<>(METRIC_VALUE, null);
        assertThrows(ColumnExecutionException.class,
                () -> ctx.write(stream, Tensor.fromDoubles(1.0)));
    }

    @Test
    void streamWiringAndModulePipeline() {
        FeatureModule feature = new FeatureModule();
        try (ColumnPipeline pipeline = new ColumnPipeline(2)) {
            pipeline.add(feature);
            pipeline.compile();

            assertNotEquals(feature.source.id(), feature.consumer.id());
            assertTrue(feature.metricValue.isExternalFeed());
            assertEquals(List.of("metric_value"),
                    pipeline.requiredFeedColumns().stream().toList());

            pipeline.feed(FeedStream.of(feature.metricValue, rows(3)));
            pipeline.compute();

            List<Row> out = pipeline.fetch(FetchStream.of(feature.consumer.out()));
            assertEquals(List.of(3L, 4L, 5L),
                    out.stream().map(row -> row.get(0).asLong()).toList());
            assertEquals(8.0,
                    pipeline.fetchScalar(feature.recovered.out()), 1e-9);
        }
    }

    @Test
    void pruningKeepsOnlyRequiredBranch() {
        PruneModule module = new PruneModule();
        try (ColumnPipeline pipeline = new ColumnPipeline()) {
            pipeline.add(module);
            pipeline.compile(FetchStream.of(module.result.out()));

            assertEquals(3, pipeline.nodeCount());
            assertEquals(2, pipeline.compiledNodeCount());

            pipeline.feed(FeedStream.of(module.metricValue, rows(2)));
            pipeline.compute();
            assertEquals(List.of(3L, 4L),
                    pipeline.fetch(FetchStream.of(module.result.out())).stream()
                            .map(row -> row.get(0).asLong()).toList());
        }
    }

    @Test
    void missingFeedIsRejected() {
        FeatureModule feature = new FeatureModule();
        try (ColumnPipeline pipeline = new ColumnPipeline()) {
            pipeline.add(feature);
            pipeline.compile();
            assertThrows(ColumnFeedException.class, pipeline::compute);
        }
    }

    @Test
    void moduleCycleIsRejected() {
        try (ColumnPipeline pipeline = new ColumnPipeline()) {
            pipeline.add(new CycleA()).add(new CycleB());
            assertThrows(ColumnDefinitionException.class, pipeline::compile);
        }
    }

    static final class CycleA extends ColumnModule {
        @Override
        protected void define() {
            dependsOn(CycleB.class);
        }
    }

    static final class CycleB extends ColumnModule {
        @Override
        protected void define() {
            dependsOn(CycleA.class);
        }
    }

    @Test
    void moduleCanDependOnMultipleModulesButNotCycles() {
        MultiA a = new MultiA();
        MultiB b = new MultiB();
        MultiJoin join = new MultiJoin();
        try (ColumnPipeline pipeline = new ColumnPipeline()) {
            pipeline.addAll(a, b, join).compile();
            assertEquals(2, join.dependencies().length);
        }

        try (ColumnPipeline pipeline = new ColumnPipeline()) {
            pipeline.addAll(new ThreeCycleA(), new ThreeCycleB(),
                    new ThreeCycleC());
            assertThrows(ColumnDefinitionException.class, pipeline::compile);
        }
    }

    static final class MultiA extends ColumnModule {
        @Override
        protected void define() {
        }
    }

    static final class MultiB extends ColumnModule {
        @Override
        protected void define() {
        }
    }

    static final class MultiJoin extends ColumnModule {
        @Override
        protected void define() {
            dependsOn(MultiA.class, MultiB.class);
        }
    }

    static final class ThreeCycleA extends ColumnModule {
        @Override
        protected void define() {
            dependsOn(ThreeCycleB.class);
        }
    }

    static final class ThreeCycleB extends ColumnModule {
        @Override
        protected void define() {
            dependsOn(ThreeCycleC.class);
        }
    }

    static final class ThreeCycleC extends ColumnModule {
        @Override
        protected void define() {
            dependsOn(ThreeCycleA.class);
        }
    }

    @Test
    void componentsAndTypedNodes() {
        try (ColumnPipeline pipeline = new ColumnPipeline()) {
            pipeline.scan("io.lark.column.biz");
            ComponentModule component = pipeline.module(ComponentModule.class);
            pipeline.compile();

            pipeline.feed(FeedStream.of(component.metricValue, rows(2)));
            pipeline.compute();

            List<Row> out = pipeline.fetch(FetchStream.of(component.out()));
            assertEquals(20L, out.get(0).get(0).asLong());
            assertEquals(30L, out.get(1).get(0).asLong());
        }
    }

    @Test
    void moduleMapsAndPublishedOutputsStayTyped() {
        ComponentModule component = new ComponentModule();
        component.initialize();

        assertEquals(component.metricValue,
                component.feedMap().get(METRIC_VALUE.name()));
        assertEquals(component.out(),
                component.fetchMap().get(LI.name()));
        assertEquals(component.out(), component.published(LI));
    }

    @Test
    void virtualComputeThreadsKeepTheReadyQueueBehavior() {
        ComponentModule component = new ComponentModule();
        try (ColumnPipeline pipeline = new ColumnPipeline(1)) {
            pipeline.setVirtualComputeThreads(true);
            pipeline.add(component);
            pipeline.compile(FetchStream.of(component.out()));
            pipeline.feed(FeedStream.of(component.metricValue, rows(2)));
            pipeline.compute();

            assertTrue(pipeline.usesVirtualComputeThreads());
            assertEquals(List.of(20L, 30L),
                    pipeline.fetch(FetchStream.of(component.out())).stream()
                            .map(row -> row.get(0).asLong()).toList());
        }
    }

    @Test
    void feedRowsUsesCompiledColumnOrder() {
        ComponentModule component = new ComponentModule();
        try (ColumnPipeline pipeline = new ColumnPipeline()) {
            pipeline.add(component);
            pipeline.compile(FetchStream.of(component.out()));
            pipeline.feedRows(rows(2));
            pipeline.compute();

            assertEquals(List.of(20L, 30L),
                    pipeline.fetch(FetchStream.of(component.out())).stream()
                            .map(row -> row.get(0).asLong()).toList());
        }
    }

    @Test
    void feedAndFetchColumnsUseNames() {
        ComponentModule component = new ComponentModule();
        try (ColumnPipeline pipeline = new ColumnPipeline()) {
            pipeline.add(component);
            pipeline.compile(FetchStream.of(component.out()));
            pipeline.feed(Map.of(METRIC_VALUE.name(),
                    List.of(Cell.of(1), Cell.of(2))));
            pipeline.compute();

            Map<String, List<Cell>> columns =
                    pipeline.fetchColumns(component.out());
            assertEquals(List.of(Cell.of(20), Cell.of(30)),
                    columns.get(LI.name()));
        }
    }

    @Test
    void feedBeforeCompileIsCoercedAfterImplicitCompile() {
        ComponentModule component = new ComponentModule();
        try (ColumnPipeline pipeline = new ColumnPipeline()) {
            pipeline.add(component);
            pipeline.feed(Map.of(METRIC_VALUE.name(),
                    List.of(Cell.of(1.0), Cell.of(2.0))));
            pipeline.compute();

            assertEquals(List.of(20L, 30L),
                    pipeline.fetch(FetchStream.of(component.out())).stream()
                            .map(row -> row.get(0).asLong()).toList());
        }
    }

    @Test
    void feedColumnsAreCoercedToDeclaredDtype() {
        ComponentModule component = new ComponentModule();
        try (ColumnPipeline pipeline = new ColumnPipeline()) {
            pipeline.add(component);
            pipeline.compile(FetchStream.of(component.out()));
            pipeline.feed(Map.of(METRIC_VALUE.name(),
                    List.of(Cell.of(1.0), Cell.of(2.0))));
            pipeline.compute();

            assertEquals(List.of(20L, 30L),
                    pipeline.fetch(FetchStream.of(component.out())).stream()
                            .map(row -> row.get(0).asLong()).toList());
        }
    }

    @ColumnComponent
    public static final class ComponentModule extends ColumnModule {
        ColumnStream<Long> metricValue;
        PlusNode<Long> source;
        TimesTenNode li;

        @Override
        protected void define() {
            metricValue = input(METRIC_VALUE);
            source = node(new PlusNode<>(SOURCE, 1.0), metricValue);
            li = node(new TimesTenNode(), source.out());
            output(li.out());
        }

        ColumnStream<Long> out() {
            return li.out();
        }
    }

    @Test
    void modulesCanDependOnOtherModulesAndStreams() {
        ProducerModule producer = new ProducerModule();
        ConsumerModule consumer = new ConsumerModule(producer);

        try (ColumnPipeline pipeline = new ColumnPipeline()) {
            pipeline.add(producer).add(consumer);
            pipeline.compile();
            pipeline.feed(FeedStream.of(producer.metricValue, rows(2)));
            pipeline.compute();

            assertEquals(List.of(3L, 4L),
                    pipeline.fetch(FetchStream.of(consumer.out())).stream()
                            .map(row -> row.get(0).asLong()).toList());
        }
    }

    static final class ProducerModule extends ColumnModule {
        ColumnStream<Long> metricValue;
        PlusNode<Long> produced;

        @Override
        protected void define() {
            metricValue = input(METRIC_VALUE);
            produced = node(new PlusNode<>(PRODUCER_OUT, 1.0), metricValue);
            output(produced.out());
        }

        ColumnStream<Long> out() {
            return produced.out();
        }
    }

    static final class ConsumerModule extends ColumnModule {
        private final ProducerModule producer;
        ConsumerNode<Long> node;

        ConsumerModule(ProducerModule producer) {
            this.producer = producer;
        }

        @Override
        protected void define() {
            node = node(new ConsumerNode<>(CONSUMER_OUT),
                    producer.published(PRODUCER_OUT));
            dependsOn(ProducerModule.class);
            output(node.out());
        }

        ColumnStream<Long> out() {
            return node.out();
        }
    }

    static final class ZeroModule extends ColumnModule {
        ZerosNode<Double> zeros;

        @Override
        protected void define() {
            zeros = node(new ZerosNode<>(ZEROS, TensorShape.of(2, 3)));
            output(zeros.out());
        }
    }

    static final class FlakyModule extends ColumnModule {
        int attempts;

        @Override
        protected void define() {
            attempts++;
            input(METRIC_VALUE);
            if (attempts == 1) {
                throw new IllegalStateException("first definition fails");
            }
        }
    }

    static final class OperatorModule extends ColumnModule {
        ColumnStream<Long> left;
        ColumnStream<Long> right;
        ColumnStream<Double> sum;
        ColumnStream<Double> scaled;

        @Override
        protected void build() {
            left = feed(OPERATOR_LEFT);
            right = feed(OPERATOR_RIGHT);
            sum = node(OPERATOR_SUM, AddOp.class, left, right);
            scaled = node(OPERATOR_SCALED, new ScaleOp(10.0), sum);
            output(sum, scaled);
        }
    }

    static final class WeightedAddOp implements ColumnOperator {

        @Override
        public int arity() {
            return 2;
        }

        @Override
        public Tensor apply(Tensor... inputs) {
            throw new IllegalStateException("run context is required");
        }

        @Override
        public Tensor apply(ColumnOperatorContext context, Tensor... inputs) {
            double weight = context.runContext().require(Weights.class).value();
            return Compute.addScalar(
                    Compute.add(inputs[0], inputs[1]), weight);
        }
    }

    record Weights(double value) {
    }

    static final class ContextModule extends ColumnModule {
        ColumnStream<Long> left;
        ColumnStream<Long> right;
        ColumnStream<Double> weighted;

        @Override
        protected void build() {
            left = feed(OPERATOR_LEFT);
            right = feed(OPERATOR_RIGHT);
            weighted = node(OPERATOR_WEIGHTED, new WeightedAddOp(),
                    left, right);
            output(weighted);
        }
    }

    static final class ConfiguredOp implements ColumnOperator {

        private final double delta;

        ConfiguredOp(double delta) {
            this.delta = delta;
        }

        @Override
        public Tensor apply(Tensor... inputs) {
            return Compute.addScalar(inputs[0], delta);
        }
    }

    static final class MissingNoArgConstructorModule extends ColumnModule {
        @Override
        protected void build() {
            node(OPERATOR_SUM, ConfiguredOp.class,
                    feed(OPERATOR_LEFT), feed(OPERATOR_RIGHT));
        }
    }

    static final class WrongArityModule extends ColumnModule {
        @Override
        protected void build() {
            node(OPERATOR_SUM, AddOp.class, feed(OPERATOR_LEFT));
        }
    }

    static final class TradeModule extends ColumnModule {
        ColumnStream<Long> price;
        ColumnStream<Double> rate;

        @Override
        protected void build() {
            price = feed(TRADE_PRICE);
            rate = feed(TRADE_RATE);
            output(price, rate);
        }
    }

    record Trade(long price, double rate) {
    }

    @Test
    void feedMappingMapsBusinessObjectsToColumns() {
        TradeModule module = new TradeModule();
        module.initialize();
        FeedMapping<Trade> mapping = FeedMapping.<Trade>builder()
                .column(module.price, Trade::price)
                .column(module.rate, Trade::rate)
                .build();

        try (ColumnPipeline pipeline = new ColumnPipeline(2)) {
            pipeline.add(module)
                    .compile()
                    .feed(mapping.map(List.of(
                            new Trade(10, 0.5),
                            new Trade(20, 1.5))))
                    .compute();

            assertEquals(List.of(10L, 20L),
                    pipeline.fetch(FetchStream.of(module.price)).stream()
                            .map(row -> row.get(0).asLong()).toList());
            assertEquals(List.of(0.5, 1.5),
                    pipeline.fetch(FetchStream.of(module.rate)).stream()
                            .map(row -> row.get(0).asDouble()).toList());
        }
    }

    @Test
    void operatorClassesDefineNodesWithoutComputeCode() {
        OperatorModule module = new OperatorModule();
        try (ColumnPipeline pipeline = new ColumnPipeline(2)) {
            pipeline.add(module)
                    .compile(FetchStream.of(module.sum, module.scaled))
                    .feed(Map.of(
                            OPERATOR_LEFT.name(), List.of(Cell.of(1), Cell.of(2)),
                            OPERATOR_RIGHT.name(), List.of(Cell.of(3), Cell.of(4))))
                    .compute();

            // add produces INT64, then the declared FLOAT64 output coerces it.
            assertEquals(List.of(4.0, 6.0),
                    doubles(pipeline.fetchTensor(module.sum)));
            assertEquals(List.of(0.4, 0.6),
                    doubles(pipeline.fetchTensor(module.scaled)));
        }
    }

    @Test
    void runContextReachesContextualOperators() {
        ContextModule module = new ContextModule();
        try (ColumnPipeline pipeline = new ColumnPipeline(2)) {
            pipeline.add(module);
            pipeline.execute(
                    FeedStream.of(new ColumnStream<?>[]{
                                    module.left, module.right},
                            List.of(Row.of(Cell.of(1), Cell.of(2)))),
                    ColumnRunContext.of(new Weights(10.0)));

            assertEquals(List.of(13.0),
                    doubles(pipeline.fetchTensor(module.weighted)));
        }
    }

    @Test
    void operatorClassNeedsANoArgConstructor() {
        MissingNoArgConstructorModule module =
                new MissingNoArgConstructorModule();
        ColumnDefinitionException failure = assertThrows(
                ColumnDefinitionException.class, module::feedMap);
        assertTrue(failure.getMessage().contains("no-arg constructor"));
    }

    @Test
    void operatorArityIsCheckedWhileBuildingTheGraph() {
        WrongArityModule module = new WrongArityModule();
        ColumnDefinitionException failure = assertThrows(
                ColumnDefinitionException.class, module::feedMap);
        assertTrue(failure.getMessage().contains("requires 2 input(s), got 1"));
    }

    private static List<Double> doubles(Tensor tensor) {
        double[] values = tensor.doubleData();
        List<Double> result = new ArrayList<>(values.length);
        for (double value : values) {
            result.add(value);
        }
        return result;
    }

    private static List<Row> rows(int count) {
        List<Row> rows = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            rows.add(Row.of(Cell.of(i)));
        }
        return rows;
    }
}
