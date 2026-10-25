// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples;

import io.lark.column.Cell;
import io.lark.column.Row;
import io.lark.column.Simd;
import io.lark.column.biz.ColumnContext;
import io.lark.column.biz.ColumnModule;
import io.lark.column.biz.ColumnNode;
import io.lark.column.biz.ColumnPipeline;
import io.lark.column.biz.ColumnStream;
import io.lark.column.biz.FeedStream;
import io.lark.column.biz.FetchStream;
import io.lark.column.compute.Compute;

import java.util.ArrayList;
import java.util.List;

/**
 * Minimal business-node quick start: node ids are generated, streams are
 * passed by reference, and the module exposes fetch outputs.
 *
 * <p>Run with {@code ./gradlew runNodes}.
 */
public final class BusinessNodeExample {

    static final class FetchNode extends ColumnNode {
        final ColumnStream out = output("user");

        FetchNode(ColumnStream userId) {
            super(userId);
        }

        @Override
        public void compute(ColumnContext ctx) {
            ctx.write(out, Compute.addScalar(ctx.read(feeds()[0]), 100.0));
        }
    }

    static final class ScoreNode extends ColumnNode {
        final ColumnStream out = output("score");

        ScoreNode(ColumnStream user) {
            super(user);
        }

        @Override
        public void compute(ColumnContext ctx) {
            ctx.write(out, Compute.mulScalar(ctx.read(feeds()[0]), 2.0));
        }
    }

    static final class FeatureModule extends ColumnModule {
        final ColumnStream userId = input("user_id");
        final FetchNode fetch = add(new FetchNode(userId));
        final ScoreNode score = add(new ScoreNode(fetch.out));

        {
            output(score.out);
        }
    }

    private BusinessNodeExample() {
    }

    public static void main(String[] args) {
        System.out.println("SIMD: " + Simd.summary());

        FeatureModule feature = new FeatureModule();
        try (ColumnPipeline pipeline = new ColumnPipeline(2)) {
            pipeline.add(feature);
            pipeline.compile();

            List<Row> rows = new ArrayList<>();
            for (int i = 1; i <= 5; i++) {
                rows.add(Row.of(Cell.of(i)));
            }
            pipeline.feed(FeedStream.of(feature.userId, rows));
            pipeline.compute();

            List<Row> scores = pipeline.fetch(FetchStream.of(feature.score.out));
            System.out.println("scores = " + scores.stream()
                    .map(row -> row.get(0).asLong()).toList());
            System.out.print(pipeline.stats().summary());
        }
    }
}
