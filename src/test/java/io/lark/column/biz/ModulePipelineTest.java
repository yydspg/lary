// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import io.lark.column.Cell;
import io.lark.column.Row;
import io.lark.column.Tensor;
import io.lark.column.compute.Compute;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ModulePipelineTest {

    static final class PlusNode extends ColumnNode {
        final ColumnStream out;

        PlusNode(ColumnStream feed, String output, double delta) {
            super(feed);
            this.out = output(output);
            this.delta = delta;
        }

        private final double delta;

        @Override
        public void compute(ColumnContext ctx) {
            ctx.write(out, Compute.addScalar(ctx.read(feeds()[0]), delta));
        }
    }

    static final class FailingNode extends ColumnNode {
        final ColumnStream out = output("recovered");

        @Override
        public void compute(ColumnContext ctx) {
            throw new IllegalStateException("boom");
        }

        @Override
        public boolean fallback(ColumnContext ctx, Throwable error) {
            ctx.write(out, Tensor.fromLongs(7));
            return true;
        }
    }

    static final class ConsumerNode extends ColumnNode {
        final ColumnStream out;

        ConsumerNode(ColumnStream feed, String output) {
            super(feed);
            this.out = output(output);
        }

        @Override
        public void compute(ColumnContext ctx) {
            ctx.write(out, Compute.addScalar(ctx.read(feeds()[0]), 1.0));
        }
    }

    static final class FeatureModule extends ColumnModule {
        final ColumnStream userId = input("user_id");
        final PlusNode source = add(new PlusNode(userId, "source", 1.0));
        final ConsumerNode consumer = add(new ConsumerNode(source.out, "result"));
        final FailingNode failing = add(new FailingNode());
        final ConsumerNode recovered = add(new ConsumerNode(failing.out, "recovered_plus_one"));

        {
            output(source.out, consumer.out, failing.out, recovered.out);
        }
    }

    static final class PruneModule extends ColumnModule {
        final ColumnStream userId = input("user_id");
        final PlusNode used = add(new PlusNode(userId, "used", 1.0));
        final PlusNode unused = add(new PlusNode(used.out, "unused", 100.0));
        final ConsumerNode result = add(new ConsumerNode(used.out, "result"));

        {
            output(used.out, unused.out, result.out);
        }
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

            List<Row> out = pipeline.fetch(FetchStream.of(feature.consumer.out));
            assertEquals(List.of(3L, 4L, 5L),
                    out.stream().map(row -> row.get(0).asLong()).toList());
            assertEquals(8.0, pipeline.fetchScalar("recovered_plus_one"), 1e-9);
        }
    }

    @Test
    void pruningKeepsOnlyRequiredBranch() {
        PruneModule module = new PruneModule();
        try (ColumnPipeline pipeline = new ColumnPipeline()) {
            pipeline.add(module);
            pipeline.compile(Set.of("result"));

            assertEquals(3, pipeline.nodeCount());
            assertEquals(2, pipeline.compiledNodeCount());

            pipeline.feed(FeedStream.of(module.userId, rows(2)));
            pipeline.compute();
            assertEquals(List.of(3L, 4L),
                    pipeline.fetch(FetchStream.of(module.result.out)).stream()
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

            List<Row> out = pipeline.fetch(FetchStream.of(component.out));
            assertEquals(20L, out.get(0).get(0).asLong());
            assertEquals(30L, out.get(1).get(0).asLong());
        }
    }

    @ColumnComponent
    public static final class ComponentModule extends ColumnModule {
        final ColumnStream userId = input("user_id");
        final PlusNode source = add(new PlusNode(userId, "source", 1.0));
        final UnitConvertNode li = add(new UnitConvertNode(source.out, "li", 10.0));
        final ColumnStream out = li.out;

        {
            output(li.out);
        }
    }

    @Test
    void modulesCanDependOnOtherModulesAndStreams() {
        ProducerModule producer = new ProducerModule();
        ConsumerModule consumer = new ConsumerModule(producer.out);

        try (ColumnPipeline pipeline = new ColumnPipeline()) {
            pipeline.add(producer).add(consumer);
            pipeline.compile();
            pipeline.feed(FeedStream.of(producer.userId, rows(2)));
            pipeline.compute();

            assertEquals(List.of(3L, 4L),
                    pipeline.fetch(FetchStream.of(consumer.out)).stream()
                            .map(row -> row.get(0).asLong()).toList());
        }
    }

    static final class ProducerModule extends ColumnModule {
        final ColumnStream userId = input("user_id");
        final PlusNode node = add(new PlusNode(userId, "producer_out", 1.0));
        final ColumnStream out = node.out;

        {
            output(out);
        }
    }

    static final class ConsumerModule extends ColumnModule {
        final ColumnStream out;
        final ConsumerNode node;

        ConsumerModule(ColumnStream feed) {
            this.node = add(new ConsumerNode(feed, "consumer_out"));
            this.out = node.out;
            dependsOn(ProducerModule.class);
            output(out);
        }
    }

    private static List<Row> rows(int count) {
        java.util.List<Row> rows = new java.util.ArrayList<>();
        for (int i = 1; i <= count; i++) {
            rows.add(Row.of(Cell.of(i)));
        }
        return rows;
    }
}
