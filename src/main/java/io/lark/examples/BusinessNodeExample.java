// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples;

import io.lark.column.Cell;
import io.lark.column.Row;
import io.lark.column.Simd;
import io.lark.column.Tensor;
import io.lark.column.biz.ColumnContext;
import io.lark.column.biz.ColumnNode;
import io.lark.column.biz.NodePipeline;
import io.lark.column.compute.Compute;

import java.util.ArrayList;
import java.util.List;

/**
 * Business-node quick start: only {@link ColumnNode} is visible; the hidden
 * {@code OpTensor} execution layer is compiled by {@link NodePipeline}.
 *
 * <p>Run with {@code ./gradlew runNodes}.
 */
public final class BusinessNodeExample {

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
            Tensor total = Compute.sumTensor(ctx.input("score"));
            ctx.output("total", total);
        }
    }

    private BusinessNodeExample() {
    }

    public static void main(String[] args) {
        System.out.println("SIMD: " + Simd.summary());

        try (NodePipeline pipeline = new NodePipeline(2)) {
            pipeline.addNodes(new FetchNode(), new ScoreNode(), new TotalNode());
            pipeline.compile();

            List<Row> rows = new ArrayList<>();
            for (int i = 1; i <= 5; i++) {
                rows.add(Row.of(Cell.of(i)));
            }

            pipeline.feed(List.of("user_id"), rows);
            pipeline.compute();

            List<Row> scores = pipeline.fetch(List.of("user", "score"));
            System.out.println("scores = " + scores.stream()
                    .map(row -> row.get(1).asLong()).toList());
            System.out.println("total  = " + pipeline.fetchScalar("total"));
            System.out.print(pipeline.stats().summary());
        }
    }
}
