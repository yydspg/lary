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

    // ---- typed column schema ---------------------------------------------

    static final class UserId extends LongColumn {
    }

    static final class Source extends LongColumn {
    }

    static final class Result extends LongColumn {
    }

    static final class Recovered extends LongColumn {
    }

    static final class RecoveredPlusOne extends LongColumn {
    }

    static final class Used extends LongColumn {
    }

    static final class Unused extends LongColumn {
    }

    static final class Li extends LongColumn {
    }

    static final class ProducerOut extends LongColumn {
    }

    static final class ConsumerOut extends LongColumn {
    }

    // ---- nodes -----------------------------------------------------------

    static final class PlusNode<O extends LongColumn>
            extends UnaryNode<LongColumn, O> {

        private final double delta;

        PlusNode(LongColumn feed, Class<O> outputType, double delta) {
            super(feed, outputType);
            this.delta = delta;
        }

        @Override
        protected Tensor compute(ColumnContext ctx, Tensor input) {
            return Compute.addScalar(input, delta);
        }
    }

    static final class ConsumerNode<O extends LongColumn>
            extends UnaryNode<LongColumn, O> {

        ConsumerNode(LongColumn feed, Class<O> outputType) {
            super(feed, outputType);
        }

        @Override
        protected Tensor compute(ColumnContext ctx, Tensor input) {
            return Compute.addScalar(input, 1.0);
        }
    }

    static final class FailingNode extends SourceNode<Recovered> {

        FailingNode() {
            super(Recovered.class);
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
        final UserId userId = input(UserId.class);
        final PlusNode<Source> source = add(new PlusNode<>(userId, Source.class, 1.0));
        final ConsumerNode<Result> consumer =
                add(new ConsumerNode<>(source.out(), Result.class));
        final FailingNode failing = add(new FailingNode());
        final ConsumerNode<RecoveredPlusOne> recovered =
                add(new ConsumerNode<>(failing.out(), RecoveredPlusOne.class));

        {
            output(source.out(), consumer.out(), failing.out(), recovered.out());
        }
    }

    static final class PruneModule extends ColumnModule {
        final UserId userId = input(UserId.class);
        final PlusNode<Used> used = add(new PlusNode<>(userId, Used.class, 1.0));
        final PlusNode<Unused> unused = add(new PlusNode<>(used.out(), Unused.class, 100.0));
        final ConsumerNode<Result> result =
                add(new ConsumerNode<>(used.out(), Result.class));

        {
            output(used.out(), unused.out(), result.out());
        }
    }

    @Test
    void columnClassDefinesNameAndDtype() {
        UserId userId = new UserId();
        assertEquals("user_id", userId.name());
        assertEquals(DType.INT64, userId.dtype());
    }

    @Test
    void dtypeMismatchFailsAtTheBusinessBoundary() {
        ColumnContext ctx = new ColumnContext(new TensorStore());
        assertThrows(IllegalArgumentException.class,
                () -> ctx.write(new UserId(), Tensor.fromDoubles(1.0)));
    }

    @Test
    void streamWiringAndModulePipeline() {
        FeatureModule feature = new FeatureModule();
        try (ColumnPipeline pipeline = new ColumnPipeline(2)) {
            pipeline.add(feature);
            pipeline.compile();

            assertNotEquals(feature.source.id(), feature.consumer.id());
            assertTrue(feature.userId.isExternalFeed());
            assertEquals(List.of("user_id"), pipeline.requiredFeedColumns().stream().toList());

            pipeline.feed(FeedStream.of(feature.userId, rows(3)));
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

            pipeline.feed(FeedStream.of(module.userId, rows(2)));
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
    void builtInBusinessTransformsAndComponents() {
        try (ColumnPipeline pipeline = new ColumnPipeline()) {
            pipeline.scan("io.lark.column.biz");
            ComponentModule component = pipeline.module(ComponentModule.class);
            pipeline.compile();

            pipeline.feed(FeedStream.of(component.userId, rows(2)));
            pipeline.compute();

            List<Row> out = pipeline.fetch(FetchStream.of(component.out()));
            assertEquals(20L, out.get(0).get(0).asLong());
            assertEquals(30L, out.get(1).get(0).asLong());
        }
    }

    @ColumnComponent
    public static final class ComponentModule extends ColumnModule {
        final UserId userId = input(UserId.class);
        final PlusNode<Source> source = add(new PlusNode<>(userId, Source.class, 1.0));
        final UnitConvertNode<Li> li =
                add(new UnitConvertNode<>(source.out(), Li.class, 10.0));

        {
            output(li.out());
        }

        Li out() {
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
            pipeline.feed(FeedStream.of(producer.userId, rows(2)));
            pipeline.compute();

            assertEquals(List.of(3L, 4L),
                    pipeline.fetch(FetchStream.of(consumer.out())).stream()
                            .map(row -> row.get(0).asLong()).toList());
        }
    }

    static final class ProducerModule extends ColumnModule {
        final UserId userId = input(UserId.class);
        final PlusNode<ProducerOut> node =
                add(new PlusNode<>(userId, ProducerOut.class, 1.0));

        {
            output(node.out());
        }

        ProducerOut out() {
            return node.out();
        }
    }

    static final class ConsumerModule extends ColumnModule {
        final ConsumerNode<ConsumerOut> node;

        ConsumerModule(LongColumn feed) {
            this.node = add(new ConsumerNode<>(feed, ConsumerOut.class));
            dependsOn(ProducerModule.class);
            output(node.out());
        }

        ConsumerOut out() {
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
