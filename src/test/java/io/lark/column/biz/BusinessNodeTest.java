// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import io.lark.column.Cell;
import io.lark.column.Row;
import io.lark.column.Tensor;
import io.lark.column.compute.Compute;
import io.lark.column.monitor.RunStats;
import io.lark.metric.Event;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BusinessNodeTest {

    static final class FetchNode extends ColumnNode {
        @Override
        public String name() {
            return "fetch";
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
            ctx.output("user", Compute.addScalar(ctx.input("user_id"), 100.0));
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

    static final class TotalNode extends ColumnNode {
        @Override
        public String name() {
            return "total";
        }

        @Override
        public List<String> inputs() {
            return List.of("score");
        }

        @Override
        public List<String> outputs() {
            return List.of("total");
        }

        @Override
        public void execute(ColumnContext ctx) {
            ctx.output("total", Compute.sumTensor(ctx.input("score")));
        }
    }

    static final class BrokenNode extends ColumnNode {
        @Override
        public String name() {
            return "broken";
        }

        @Override
        public List<String> outputs() {
            return List.of("recovered");
        }

        @Override
        public void execute(ColumnContext ctx) {
            throw new IllegalStateException("boom");
        }

        @Override
        public boolean fallback(ColumnContext ctx, Throwable error) {
            ctx.output("recovered", Tensor.fromLongs(7));
            return true;
        }
    }

    static final class ReadRecoveredNode extends ColumnNode {
        @Override
        public String name() {
            return "reader";
        }

        @Override
        public List<String> inputs() {
            return List.of("recovered");
        }

        @Override
        public List<String> outputs() {
            return List.of("out");
        }

        @Override
        public void execute(ColumnContext ctx) {
            ctx.output("out", Compute.addScalar(ctx.input("recovered"), 1.0));
        }
    }

    static final class OutputNode extends ColumnNode {
        private final String name;
        private final String output;

        OutputNode(String name, String output) {
            this.name = name;
            this.output = output;
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
            ctx.output(output, Tensor.fromLongs(1));
        }
    }

    @Test
    void businessPipelineDerivesDependenciesAndHidesOpTensor() {
        List<Event> events = new CopyOnWriteArrayList<>();
        try (NodePipeline pipeline = new NodePipeline(2)) {
            pipeline.addNodes(new FetchNode(), new ScoreNode(), new TotalNode());
            pipeline.setMonitor(events::add);
            pipeline.compile();
            assertEquals(3, pipeline.nodeCount());

            List<Row> rows = new ArrayList<>();
            for (int i = 1; i <= 5; i++) {
                rows.add(Row.of(Cell.of(i)));
            }
            pipeline.feed(List.of("user_id"), rows);
            pipeline.compute();

            List<Row> scores = pipeline.fetch(List.of("user", "score"));
            assertEquals(5, scores.size());
            assertEquals(101L, scores.get(0).get(0).asLong());
            assertEquals(202L, scores.get(0).get(1).asLong());
            assertEquals(105L, scores.get(4).get(0).asLong());
            assertEquals(210L, scores.get(4).get(1).asLong());
            assertEquals(1030.0, pipeline.fetchScalar("total"), 1e-9);

            RunStats stats = pipeline.stats();
            assertFalse(stats.modules().isEmpty());
            assertFalse(stats.pressure().isEmpty());
            assertTrue(events.stream().anyMatch(e -> e.action().equals("node.end")));
        }
    }

    @Test
    void failedBusinessNodeUsesFallbackBeforeDependents() {
        List<Event> events = new CopyOnWriteArrayList<>();
        try (NodePipeline pipeline = new NodePipeline()) {
            pipeline.addNodes(new BrokenNode(), new ReadRecoveredNode());
            pipeline.setMonitor(events::add);
            pipeline.compile();

            pipeline.feed(List.of(), List.of());
            pipeline.compute();

            List<Row> out = pipeline.fetch(List.of("out"));
            assertEquals(8L, out.get(0).get(0).asLong());
            assertTrue(events.stream().anyMatch(e -> e.action().equals("node.fallback")));
        }
    }

    @Test
    void businessGraphValidation() {
        ColumnGraph duplicateName = new ColumnGraph()
                .add(new OutputNode("a", "x"));
        assertThrows(IllegalArgumentException.class,
                () -> duplicateName.add(new OutputNode("a", "z")));

        ColumnGraph duplicateColumn = new ColumnGraph()
                .add(new OutputNode("a", "x"))
                .add(new OutputNode("b", "x"));
        assertThrows(IllegalArgumentException.class, duplicateColumn::compile);

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
        ColumnGraph cycle = new ColumnGraph().add(cycleA).add(cycleB);
        assertThrows(IllegalStateException.class, cycle::compile);

        ColumnGraph self = new ColumnGraph().add(new ColumnNode() {
            @Override
            public String name() {
                return "self";
            }

            @Override
            public List<String> inputs() {
                return List.of("self_out");
            }

            @Override
            public List<String> outputs() {
                return List.of("self_out");
            }

            @Override
            public void execute(ColumnContext ctx) {
            }
        });
        assertThrows(IllegalArgumentException.class, self::compile);

        ColumnGraph unknownDepends = new ColumnGraph()
                .add(new OutputNode("a", "x"))
                .dependsOn("a", "missing");
        assertThrows(IllegalArgumentException.class, unknownDepends::compile);
    }

    @Test
    void explicitOrderingEdgesRunBeforeTheNode() {
        AtomicBoolean firstRan = new AtomicBoolean(false);

        ColumnNode first = new ColumnNode() {
            @Override
            public String name() {
                return "first";
            }

            @Override
            public List<String> outputs() {
                return List.of("first_out");
            }

            @Override
            public void execute(ColumnContext ctx) {
                firstRan.set(true);
                ctx.output("first_out", Tensor.fromLongs(1));
            }
        };
        ColumnNode second = new ColumnNode() {
            @Override
            public String name() {
                return "second";
            }

            @Override
            public List<String> outputs() {
                return List.of("second_out");
            }

            @Override
            public void execute(ColumnContext ctx) {
                assertTrue(firstRan.get());
                ctx.output("second_out", Tensor.fromLongs(2));
            }
        };

        try (NodePipeline pipeline = new NodePipeline()) {
            pipeline.addNode(second).addNode(first).dependsOn("second", "first");
            pipeline.compile();
            pipeline.feed(List.of(), List.of());
            pipeline.compute();
            assertEquals(2.0, pipeline.fetchScalar("second_out"), 1e-9);
        }
    }
}
