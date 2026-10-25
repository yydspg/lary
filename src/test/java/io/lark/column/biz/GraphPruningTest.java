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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GraphPruningTest {

    static final class SourceNode extends ColumnNode {
        private final String name;
        private final String output;
        private final AtomicInteger executions;
        private final double add;

        SourceNode(String name, String output, double add, AtomicInteger executions) {
            this.name = name;
            this.output = output;
            this.add = add;
            this.executions = executions;
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public List<String> outputs() {
            return List.of(output);
        }

        @Override
        public void execute(ColumnContext ctx) {
            executions.incrementAndGet();
            ctx.output(output, Compute.addScalar(ctx.input("user_id"), add));
        }
    }

    static final class ConsumerNode extends ColumnNode {
        private final String name;
        private final String input;
        private final String output;

        ConsumerNode(String name, String input, String output) {
            this.name = name;
            this.input = input;
            this.output = output;
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public List<String> inputs() {
            return List.of(input);
        }

        @Override
        public List<String> outputs() {
            return List.of(output);
        }

        @Override
        public void execute(ColumnContext ctx) {
            ctx.output(output, Compute.addScalar(ctx.input(input), 1.0));
        }
    }

    private static List<Row> rows(int count) {
        return java.util.stream.IntStream.rangeClosed(1, count)
                .mapToObj(i -> Row.of(Cell.of(i)))
                .toList();
    }

    @Test
    void pruneRemovesUnusedBranches() {
        AtomicInteger usedRuns = new AtomicInteger();
        AtomicInteger unusedRuns = new AtomicInteger();

        try (NodePipeline pipeline = new NodePipeline()) {
            pipeline.addNodes(
                    new SourceNode("used", "used_out", 1.0, usedRuns),
                    new SourceNode("unused", "unused_out", 100.0, unusedRuns),
                    new ConsumerNode("consumer", "used_out", "needed"));
            pipeline.compile(Set.of("needed"));

            assertEquals(2, pipeline.compiledNodeCount());
            pipeline.feed(List.of("user_id"), rows(3));
            pipeline.compute();

            assertEquals(1, usedRuns.get());
            assertEquals(0, unusedRuns.get());
            assertEquals(List.of(3L, 4L, 5L),
                    pipeline.fetch(List.of("needed")).stream()
                            .map(row -> row.get(0).asLong()).toList());
        }
    }

    @Test
    void runWithFetchStreamPrunesBeforeExecution() {
        AtomicInteger usedRuns = new AtomicInteger();
        AtomicInteger unusedRuns = new AtomicInteger();

        try (NodePipeline pipeline = new NodePipeline()) {
            pipeline.addNodes(
                    new SourceNode("used", "used_out", 1.0, usedRuns),
                    new SourceNode("unused", "unused_out", 100.0, unusedRuns),
                    new ConsumerNode("consumer", "used_out", "needed"));

            List<Row> out = pipeline.run(
                    FeedStream.of(List.of("user_id"), rows(2)),
                    FetchStream.of("needed"));

            assertEquals(1, usedRuns.get());
            assertEquals(0, unusedRuns.get());
            assertEquals(4L, out.get(1).get(0).asLong());
        }
    }

    @Test
    void feedAndNodeInputSourcesAreValidated() {
        ColumnNode producer = new ColumnNode() {
            @Override
            public String name() {
                return "producer";
            }

            @Override
            public List<String> outputs() {
                return List.of("x");
            }

            @Override
            public void execute(ColumnContext ctx) {
                ctx.output("x", Tensor.fromLongs(1));
            }
        };

        ColumnNode conflictsWithProducer = new ColumnNode() {
            @Override
            public String name() {
                return "feeder";
            }

            @Override
            public List<String> inputs() {
                return List.of("x");
            }

            @Override
            public ColumnSource inputSource(String column) {
                return ColumnSource.FEED;
            }

            @Override
            public List<String> outputs() {
                return List.of("y");
            }

            @Override
            public void execute(ColumnContext ctx) {
            }
        };

        ColumnGraph conflict = new ColumnGraph().add(producer).add(conflictsWithProducer);
        assertThrows(IllegalArgumentException.class, conflict::compile);

        ColumnNode missingNodeInput = new ColumnNode() {
            @Override
            public String name() {
                return "missing";
            }

            @Override
            public List<String> inputs() {
                return List.of("not_produced");
            }

            @Override
            public ColumnSource inputSource(String column) {
                return ColumnSource.NODE;
            }

            @Override
            public List<String> outputs() {
                return List.of("z");
            }

            @Override
            public void execute(ColumnContext ctx) {
            }
        };
        ColumnGraph missing = new ColumnGraph().add(missingNodeInput);
        assertThrows(IllegalArgumentException.class, missing::compile);
    }

    @Test
    void missingFeedColumnIsRejectedBeforeCompute() {
        ColumnNode feedConsumer = new ColumnNode() {
            @Override
            public String name() {
                return "feed_consumer";
            }

            @Override
            public List<String> inputs() {
                return List.of("amount_fen");
            }

            @Override
            public ColumnSource inputSource(String column) {
                return ColumnSource.FEED;
            }

            @Override
            public List<String> outputs() {
                return List.of("amount_li");
            }

            @Override
            public void execute(ColumnContext ctx) {
                ctx.output("amount_li",
                        Compute.mulScalar(ctx.input("amount_fen"), 10.0));
            }
        };

        try (NodePipeline pipeline = new NodePipeline()) {
            pipeline.addNode(feedConsumer);
            pipeline.compile();
            assertThrows(IllegalArgumentException.class, pipeline::compute);
        }
    }

    @Test
    void scaleAndUnitConvertNodesHandleBusinessTransforms() {
        try (NodePipeline pipeline = new NodePipeline()) {
            pipeline.addNodes(
                    new ScaleNode("weight", "user_id", "weighted", 2.0),
                    new UnitConvertNode("fen_to_li", "weighted", "li", 10.0));
            pipeline.compile();
            pipeline.feed(FeedStream.of(List.of("user_id"), rows(2)));
            pipeline.compute();

            List<Row> out = pipeline.fetch(FetchStream.of("weighted", "li"));
            assertEquals(2.0, out.get(0).get(0).asDouble(), 1e-9);
            assertEquals(20.0, out.get(0).get(1).asDouble(), 1e-9);
            assertEquals(4.0, out.get(1).get(0).asDouble(), 1e-9);
            assertEquals(40.0, out.get(1).get(1).asDouble(), 1e-9);
        }
    }

    @Test
    void annotatedComponentsAreDiscoveredByScan() {
        try (NodePipeline pipeline = new NodePipeline()) {
            pipeline.scan("io.lark.column.biz.components");
            pipeline.compile();
            pipeline.feed(FeedStream.of(List.of("user_id"), rows(2)));
            pipeline.compute();

            List<Row> out = pipeline.fetch(FetchStream.of("component_score"));
            assertEquals(20L, out.get(0).get(0).asLong());
            assertEquals(30L, out.get(1).get(0).asLong());
        }
    }

    @Test
    void fullGraphCycleIsDetectedEvenWhenPruned() {
        ColumnNode cycleA = new ColumnNode() {
            @Override
            public String name() {
                return "a";
            }

            @Override
            public List<String> inputs() {
                return List.of("b_out");
            }

            @Override
            public List<String> outputs() {
                return List.of("a_out");
            }

            @Override
            public void execute(ColumnContext ctx) {
            }
        };
        ColumnNode cycleB = new ColumnNode() {
            @Override
            public String name() {
                return "b";
            }

            @Override
            public List<String> inputs() {
                return List.of("a_out");
            }

            @Override
            public List<String> outputs() {
                return List.of("b_out");
            }

            @Override
            public void execute(ColumnContext ctx) {
            }
        };

        ColumnGraph graph = new ColumnGraph().add(cycleA).add(cycleB);
        assertThrows(IllegalStateException.class, () -> graph.compile(Set.of("a_out")));
    }
}
