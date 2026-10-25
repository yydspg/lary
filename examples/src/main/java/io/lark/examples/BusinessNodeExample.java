// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples;

import io.lark.column.Cell;
import io.lark.column.Row;
import io.lark.column.Simd;
import io.lark.column.Tensor;
import io.lark.column.biz.ColumnContext;
import io.lark.column.biz.ColumnModule;
import io.lark.column.biz.ColumnPipeline;
import io.lark.column.biz.FeedStream;
import io.lark.column.biz.FetchStream;
import io.lark.column.biz.LongColumn;
import io.lark.column.biz.UnaryNode;
import io.lark.column.compute.Compute;

import java.util.ArrayList;
import java.util.List;

/**
 * Minimal typed business-node quick start. Column classes define stream names
 * and dtypes; node inputs are typed fields and outputs are fetch streams.
 *
 * <p>Run with {@code ./gradlew :examples:runNodes}.
 */
public final class BusinessNodeExample {

    static final class UserId extends LongColumn {
    }

    static final class User extends LongColumn {
    }

    static final class Score extends LongColumn {
    }

    static final class FetchNode extends UnaryNode<UserId, User> {

        FetchNode(UserId userId) {
            super(userId, User.class);
        }

        @Override
        protected Tensor compute(ColumnContext ctx, Tensor userId) {
            return Compute.addScalar(userId, 100.0);
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

    static final class FeatureModule extends ColumnModule {
        final UserId userId = input(UserId.class);
        final FetchNode fetch = add(new FetchNode(userId));
        final ScoreNode score = add(new ScoreNode(fetch.out()));

        {
            output(score.out());
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

            List<Row> scores = pipeline.fetch(FetchStream.of(feature.score.out()));
            System.out.println("scores = " + scores.stream()
                    .map(row -> row.get(0).asLong()).toList());
            System.out.print(pipeline.stats().summary());
        }
    }
}
