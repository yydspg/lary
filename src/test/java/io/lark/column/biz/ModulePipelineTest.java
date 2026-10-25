// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import io.lark.column.Cell;
import io.lark.column.DType;
import io.lark.column.Row;
import io.lark.column.Tensor;
import io.lark.column.TensorStore;
import io.lark.column.compute.Compute;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
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

    // ---- nodes -----------------------------------------------------------

    static final class PlusNode<O>
            extends UnaryNode<ColumnStream<Long>, O> {

        private final double delta;

        PlusNode(ColumnStream<Long> feed, Column<O> output, double delta) {
            super(feed, output);
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

        @Override
        protected Tensor compute(ColumnContext ctx, Tensor input) {
            return Compute.addScalar(input, 1.0);
        }
    }

    static final class TimesTenNode extends UnaryNode<ColumnStream<Long>, Long> {

        TimesTenNode(ColumnStream<Long> input) {
            super(input, LI);
        }

        @Override
        protected Tensor compute(ColumnContext ctx, Tensor input) {
            return Compute.mulScalar(input, 10.0);
        }
    }

    static final class FailingNode extends SourceNode<Long> {

        FailingNode() {
            super(RECOVERED);
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
        final ColumnStream<Long> metricValue = input(METRIC_VALUE);
        final PlusNode<Long> source =
                add(new PlusNode<>(metricValue, SOURCE, 1.0));
        final ConsumerNode<Long> consumer =
                add(new ConsumerNode<>(source.out(), RESULT));
        final FailingNode failing = add(new FailingNode());
        final ConsumerNode<Long> recovered =
                add(new ConsumerNode<>(failing.out(), RECOVERED_PLUS_ONE));

        {
            output(source.out(), consumer.out(), failing.out(), recovered.out());
        }
    }

    static final class PruneModule extends ColumnModule {
        final ColumnStream<Long> metricValue = input(METRIC_VALUE);
        final PlusNode<Long> used =
                add(new PlusNode<>(metricValue, USED, 1.0));
        final PlusNode<Long> unused =
                add(new PlusNode<>(used.out(), UNUSED, 100.0));
        final ConsumerNode<Long> result =
                add(new ConsumerNode<>(used.out(), RESULT));

        {
            output(used.out(), unused.out(), result.out());
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
        assertThrows(IllegalArgumentException.class,
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
            assertThrows(IllegalArgumentException.class, pipeline::compute);
        }
    }

    @Test
    void moduleCycleIsRejected() {
        try (ColumnPipeline pipeline = new ColumnPipeline()) {
            pipeline.add(new CycleA()).add(new CycleB());
            assertThrows(IllegalStateException.class, pipeline::compile);
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

    @ColumnComponent
    public static final class ComponentModule extends ColumnModule {
        final ColumnStream<Long> metricValue = input(METRIC_VALUE);
        final PlusNode<Long> source =
                add(new PlusNode<>(metricValue, SOURCE, 1.0));
        final TimesTenNode li = add(new TimesTenNode(source.out()));

        {
            output(li.out());
        }

        ColumnStream<Long> out() {
            return li.out();
        }
    }

    @Test
    void modulesCanDependOnOtherModulesAndStreams() {
        ProducerModule producer = new ProducerModule();
        ConsumerModule consumer = new ConsumerModule(producer.out());

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
        final ColumnStream<Long> metricValue = input(METRIC_VALUE);
        final PlusNode<Long> node =
                add(new PlusNode<>(metricValue, PRODUCER_OUT, 1.0));

        {
            output(node.out());
        }

        ColumnStream<Long> out() {
            return node.out();
        }
    }

    static final class ConsumerModule extends ColumnModule {
        final ConsumerNode<Long> node;

        ConsumerModule(ColumnStream<Long> feed) {
            this.node = add(new ConsumerNode<>(feed, CONSUMER_OUT));
            dependsOn(ProducerModule.class);
            output(node.out());
        }

        ColumnStream<Long> out() {
            return node.out();
        }
    }

    private static List<Row> rows(int count) {
        List<Row> rows = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            rows.add(Row.of(Cell.of(i)));
        }
        return rows;
    }
}
