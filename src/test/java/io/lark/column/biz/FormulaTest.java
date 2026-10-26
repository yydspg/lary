// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import io.lark.column.context.ExperimentContext;
import io.lark.column.data.Cell;
import io.lark.column.data.Row;
import io.lark.column.tensor.DType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FormulaTest {

    static final Column<Long> RAW = new Column<>("raw", DType.INT64);
    static final Column<Double> SCORE = new Column<>("score", DType.FLOAT64);
    static final Column<Long> FLAG = new Column<>("flag", DType.INT64);

    static final class ConfigModule extends ColumnModule {
        ColumnStream<Long> raw;
        ColumnStream<Double> score;

        @Override
        protected void define() {
            raw = feed(RAW);
            FormulaConfig config = FormulaConfig.of(Map.of(
                    "op", "sum",
                    "terms", List.of(
                            Map.of("op", "column", "name", RAW.name()),
                            Map.of("op", "param", "key", "delta",
                                    "default", 2.0))));
            score = formula(SCORE, config.compileNum(Map.of(RAW.name(), raw)));
            output(score);
        }
    }

    static final class FormulaModule extends ColumnModule {
        ColumnStream<Long> raw;
        ColumnStream<Double> score;
        ColumnStream<Long> flag;

        @Override
        protected void define() {
            raw = feed(RAW);
            score = formula(SCORE, F.when(
                    F.and(
                            F.gt(F.colLong(raw), F.val(10)),
                            F.variant("rerank", "boost")),
                    F.add(F.colLong(raw), F.param("boost", 5)),
                    F.colLong(raw)));
            flag = formula(FLAG, F.and(
                    F.variant("rerank", "boost"),
                    F.gt(F.colLong(raw), F.val(0))));
            output(score, flag);
        }
    }

    @Test
    void configuredFormulaCompilesAgainstTypedColumns() {
        ConfigModule module = new ConfigModule();
        try (ColumnPipeline pipeline = new ColumnPipeline(2)) {
            pipeline.add(module)
                    .compile()
                    .feed(FeedStream.of(module.raw, List.of(
                            Row.of(Cell.of(3L)),
                            Row.of(Cell.of(4L)))))
                    .compute();

            List<Row> rows = pipeline.fetch(FetchStream.of(module.score));
            assertEquals(5.0, rows.get(0).get(0).asDouble(), 1e-9);
            assertEquals(6.0, rows.get(1).get(0).asDouble(), 1e-9);
        }
    }

    @Test
    void formulaEvaluatesConditionsAndExperimentVariants() {
        FormulaModule module = new FormulaModule();
        List<Row> rows = List.of(
                Row.of(Cell.of(20L)),
                Row.of(Cell.of(5L)));

        ExperimentContext control = ExperimentContext.builder()
                .variant("rerank", "control")
                .build();
        ExperimentContext boost = ExperimentContext.builder()
                .variant("rerank", "boost")
                .param("boost", 5.0)
                .build();

        try (ColumnPipeline pipeline = new ColumnPipeline(2)) {
            pipeline.add(module)
                    .setExperiment(control)
                    .compile()
                    .feed(FeedStream.of(module.raw, rows))
                    .compute();

            List<Row> controlRows = pipeline.fetch(
                    FetchStream.of(module.score, module.flag));
            assertEquals(20.0, controlRows.get(0).get(0).asDouble(), 1e-9);
            assertEquals(5.0, controlRows.get(1).get(0).asDouble(), 1e-9);
            assertEquals(0L, controlRows.get(0).get(1).asLong());

            pipeline.setExperiment(boost).compute();
            List<Row> boostRows = pipeline.fetch(
                    FetchStream.of(module.score, module.flag));
            assertEquals(25.0, boostRows.get(0).get(0).asDouble(), 1e-9);
            assertEquals(5.0, boostRows.get(1).get(0).asDouble(), 1e-9);
            assertEquals(1L, boostRows.get(0).get(1).asLong());
            assertEquals(1L, boostRows.get(1).get(1).asLong());
        }
    }
}
