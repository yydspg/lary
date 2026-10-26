// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import io.lark.column.tensor.DType;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * External representation of a typed formula tree.
 *
 * <p>The root is a JSON/YAML-friendly {@code Map} (or a plain column name /
 * number). Applications can deserialize configuration with their preferred
 * parser and compile it against a set of typed column handles:
 *
 * <pre>{@code
 * Map<String, Object> config = Map.of(
 *     "op", "sum",
 *     "terms", List.of(
 *         Map.of("op", "weighted", "column", "ctr",
 *                 "key", "rerank.ctr_w", "default", 100.0),
 *         Map.of("op", "weighted", "column", "cvr",
 *                 "key", "rerank.cvr_w", "default", 50.0)));
 *
 * NumExpr score = FormulaConfig.of(config).compileNum(columns);
 * }</pre>
 *
 * <p>Supported ops: column, const, param, variant, sum, add, sub, mul, div,
 * neg, abs, sqrt, exp, log, gt, ge, lt, le, eq, neq, and, or, not, when,
 * chooseVariant, caseWhen, weighted, clamp.
 */
public final class FormulaConfig {

    private final Object root;

    private FormulaConfig(Object root) {
        this.root = Objects.requireNonNull(root, "root");
    }

    public static FormulaConfig of(Object root) {
        return new FormulaConfig(root);
    }

    public Object root() {
        return root;
    }

    public NumExpr compileNum(Map<String, ColumnStream<?>> columns) {
        Expr<?> expression = compile(columns);
        if (!(expression instanceof NumExpr number)) {
            throw new IllegalArgumentException(
                    "formula config root must be numeric, got bool");
        }
        return number;
    }

    public BoolExpr compileBool(Map<String, ColumnStream<?>> columns) {
        Expr<?> expression = compile(columns);
        if (!(expression instanceof BoolExpr condition)) {
            throw new IllegalArgumentException(
                    "formula config root must be boolean, got numeric");
        }
        return condition;
    }

    public Expr<?> compile(Map<String, ColumnStream<?>> columns) {
        Objects.requireNonNull(columns, "columns");
        return expression(root, columns);
    }

    private static Expr<?> expression(Object node,
                                      Map<String, ColumnStream<?>> columns) {
        if (node instanceof Number number) {
            return F.val(number.doubleValue());
        }
        if (node instanceof String name) {
            return column(name, columns);
        }
        if (!(node instanceof Map<?, ?> map)) {
            throw new IllegalArgumentException(
                    "formula node must be a number, column name or object: " + node);
        }

        String op = string(map.get("op"));
        return switch (op) {
            case "column" -> column(string(map.get("name")), columns);
            case "const" -> F.val(number(map.get("value")));
            case "param" -> F.param(string(map.get("key")),
                    number(map.get("default")));
            case "variant" -> F.variant(string(map.get("layer")),
                    string(map.get("name")));
            case "sum" -> F.sum(terms(map.get("terms"), columns));
            case "add" -> F.add(num(map.get("left"), columns),
                    num(map.get("right"), columns));
            case "sub" -> F.sub(num(map.get("left"), columns),
                    num(map.get("right"), columns));
            case "mul" -> F.mul(num(map.get("left"), columns),
                    num(map.get("right"), columns));
            case "div" -> F.div(num(map.get("left"), columns),
                    num(map.get("right"), columns));
            case "neg" -> F.neg(num(map.get("value"), columns));
            case "abs" -> F.abs(num(map.get("value"), columns));
            case "sqrt" -> F.sqrt(num(map.get("value"), columns));
            case "exp" -> F.exp(num(map.get("value"), columns));
            case "log" -> F.log(num(map.get("value"), columns));
            case "gt" -> F.gt(num(map.get("left"), columns),
                    num(map.get("right"), columns));
            case "ge" -> F.ge(num(map.get("left"), columns),
                    num(map.get("right"), columns));
            case "lt" -> F.lt(num(map.get("left"), columns),
                    num(map.get("right"), columns));
            case "le" -> F.le(num(map.get("left"), columns),
                    num(map.get("right"), columns));
            case "eq" -> F.eq(num(map.get("left"), columns),
                    num(map.get("right"), columns));
            case "neq" -> F.neq(num(map.get("left"), columns),
                    num(map.get("right"), columns));
            case "and" -> F.and(conditions(map.get("conditions"), columns));
            case "or" -> F.or(conditions(map.get("conditions"), columns));
            case "not" -> F.not(bool(map.get("value"), columns));
            case "when" -> F.when(bool(map.get("condition"), columns),
                    num(map.get("then"), columns),
                    num(map.get("else"), columns));
            case "chooseVariant" -> variantChoice(map, columns);
            case "caseWhen" -> caseChoice(map, columns);
            case "weighted" -> F.weighted(num(map.get("value"), columns),
                    string(map.get("key")), number(map.get("default")));
            case "clamp" -> F.clamp(num(map.get("value"), columns),
                    num(map.get("low"), columns),
                    num(map.get("high"), columns));
            default -> throw new IllegalArgumentException(
                    "unknown formula op '" + op + "'");
        };
    }

    private static NumExpr variantChoice(Map<?, ?> map,
                                         Map<String, ColumnStream<?>> columns) {
        String layer = string(map.get("layer"));
        Object variantsNode = map.get("variants");
        if (!(variantsNode instanceof Map<?, ?> variants)) {
            throw new IllegalArgumentException("chooseVariant.variants must be an object");
        }
        Map<String, NumExpr> compiled = new LinkedHashMap<>();
        for (Map.Entry<?, ?> entry : variants.entrySet()) {
            compiled.put(string(entry.getKey()), num(entry.getValue(), columns));
        }
        Object defaultNode = map.containsKey("default")
                ? map.get("default") : map.get("otherwise");
        if (defaultNode == null) {
            throw new IllegalArgumentException(
                    "chooseVariant requires default/otherwise");
        }
        return F.chooseVariant(layer, compiled,
                num(defaultNode, columns));
    }

    private static NumExpr caseChoice(Map<?, ?> map,
                                      Map<String, ColumnStream<?>> columns) {
        Object branchesNode = map.get("branches");
        if (!(branchesNode instanceof List<?> branches) || branches.isEmpty()) {
            throw new IllegalArgumentException(
                    "caseWhen.branches must be a non-empty array");
        }
        Object defaultNode = map.containsKey("default")
                ? map.get("default") : map.get("otherwise");
        if (defaultNode == null) {
            throw new IllegalArgumentException(
                    "caseWhen requires default/otherwise");
        }
        NumExpr result = num(defaultNode, columns);
        for (Object node : branches) {
            if (!(node instanceof Map<?, ?> branch)) {
                throw new IllegalArgumentException(
                        "caseWhen branch must be an object");
            }
            result = F.when(bool(branch.get("condition"), columns),
                    num(branch.get("value"), columns), result);
        }
        return result;
    }

    private static NumExpr[] terms(Object node,
                                   Map<String, ColumnStream<?>> columns) {
        if (!(node instanceof List<?> list) || list.isEmpty()) {
            throw new IllegalArgumentException("sum.terms must be a non-empty array");
        }
        NumExpr[] terms = new NumExpr[list.size()];
        for (int i = 0; i < list.size(); i++) {
            terms[i] = num(list.get(i), columns);
        }
        return terms;
    }

    private static BoolExpr[] conditions(Object node,
                                         Map<String, ColumnStream<?>> columns) {
        if (!(node instanceof List<?> list) || list.isEmpty()) {
            throw new IllegalArgumentException(
                    "and/or.conditions must be a non-empty array");
        }
        BoolExpr[] conditions = new BoolExpr[list.size()];
        for (int i = 0; i < list.size(); i++) {
            conditions[i] = bool(list.get(i), columns);
        }
        return conditions;
    }

    private static NumExpr num(Object node,
                               Map<String, ColumnStream<?>> columns) {
        Expr<?> expression = expression(node, columns);
        if (expression instanceof NumExpr number) {
            return number;
        }
        throw new IllegalArgumentException("expected numeric formula node: " + node);
    }

    private static BoolExpr bool(Object node,
                                 Map<String, ColumnStream<?>> columns) {
        Expr<?> expression = expression(node, columns);
        if (expression instanceof BoolExpr condition) {
            return condition;
        }
        throw new IllegalArgumentException("expected boolean formula node: " + node);
    }

    @SuppressWarnings("unchecked")
    private static NumExpr column(String name,
                                  Map<String, ColumnStream<?>> columns) {
        ColumnStream<?> stream = columns.get(name);
        if (stream == null) {
            throw new IllegalArgumentException(
                    "formula references unknown column '" + name
                            + "'; available=" + columns.keySet());
        }
        if (stream.definition().dtype() == DType.FLOAT64) {
            return F.col((ColumnStream<Double>) stream);
        }
        if (stream.definition().dtype() == DType.INT64) {
            return F.colLong((ColumnStream<Long>) stream);
        }
        throw new IllegalArgumentException(
                "formula column '" + name + "' must be float64 or int64");
    }

    private static String string(Object value) {
        if (!(value instanceof String text) || text.isBlank()) {
            throw new IllegalArgumentException("expected non-blank string: " + value);
        }
        return text;
    }

    private static double number(Object value) {
        if (!(value instanceof Number number)) {
            throw new IllegalArgumentException("expected number: " + value);
        }
        return number.doubleValue();
    }
}
