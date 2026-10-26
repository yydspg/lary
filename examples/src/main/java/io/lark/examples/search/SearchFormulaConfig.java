// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.search;

import io.lark.column.biz.FormulaConfig;
import io.lark.examples.search.schema.SearchMetrics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** External, JSON/YAML-friendly rerank formula configuration for the demo. */
public final class SearchFormulaConfig {

    private SearchFormulaConfig() {
    }

    public static FormulaConfig ranking() {
        Map<String, Object> control = sum(
                weighted(SearchMetrics.CTR.name(), "rerank.ctr_w", 100.0),
                weighted(SearchMetrics.CVR.name(), "rerank.cvr_w", 50.0),
                weighted(SearchMetrics.GMV.name(), "rerank.gmv_w", 0.01),
                weighted(SearchMetrics.COUPON_ROI.name(),
                        "rerank.coupon_roi_w", 0.5),
                neg(weighted(SearchMetrics.REBUY_SAME_LOSS.name(),
                        "rerank.rebuy_w", 0.5)));

        Map<String, Object> couponBoost = when(
                and(
                        variant("rerank", "coupon_boost"),
                        gt(column(SearchMetrics.COUPON_ROI.name()),
                                constant(0.0))),
                weighted(SearchMetrics.GMV.name(),
                        "rerank.coupon_boost_w", 0.02),
                constant(0.0));

        Map<String, Object> couponVariant = add(control, couponBoost);
        return FormulaConfig.of(Map.of(
                "op", "chooseVariant",
                "layer", "rerank",
                "variants", Map.of(
                        "control", control,
                        "coupon_boost", couponVariant),
                "default", couponVariant));
    }

    private static Map<String, Object> column(String name) {
        return Map.of("op", "column", "name", name);
    }

    private static Map<String, Object> constant(double value) {
        return Map.of("op", "const", "value", value);
    }

    private static Map<String, Object> weighted(String columnName, String key,
                                                 double fallback) {
        return Map.of("op", "weighted", "value", column(columnName),
                "key", key, "default", fallback);
    }

    @SafeVarargs
    private static Map<String, Object> sum(Map<String, Object>... terms) {
        List<Map<String, Object>> list = new ArrayList<>(terms.length);
        for (Map<String, Object> term : terms) {
            list.add(term);
        }
        return Map.of("op", "sum", "terms", list);
    }

    private static Map<String, Object> add(Map<String, Object> left,
                                           Map<String, Object> right) {
        return Map.of("op", "add", "left", left, "right", right);
    }

    private static Map<String, Object> neg(Map<String, Object> value) {
        return Map.of("op", "neg", "value", value);
    }

    private static Map<String, Object> gt(Map<String, Object> left,
                                          Map<String, Object> right) {
        return Map.of("op", "gt", "left", left, "right", right);
    }

    private static Map<String, Object> variant(String layer, String name) {
        return Map.of("op", "variant", "layer", layer, "name", name);
    }

    @SafeVarargs
    private static Map<String, Object> and(Map<String, Object>... conditions) {
        List<Map<String, Object>> list = new ArrayList<>(conditions.length);
        for (Map<String, Object> condition : conditions) {
            list.add(condition);
        }
        return Map.of("op", "and", "conditions", list);
    }

    private static Map<String, Object> when(Map<String, Object> condition,
                                            Map<String, Object> then,
                                            Map<String, Object> otherwise) {
        return Map.of("op", "when", "condition", condition,
                "then", then, "else", otherwise);
    }
}
