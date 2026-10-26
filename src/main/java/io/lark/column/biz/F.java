// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import io.lark.column.compute.Compute;
import io.lark.column.tensor.DType;
import io.lark.column.tensor.Tensor;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Formula DSL for business scoring and eligibility rules.
 *
 * <p>Expressions are typed and composable:
 *
 * <pre>{@code
 * NumExpr score = F.sum(
 *         F.weighted(ctr, "rerank.ctr_w", 100),
 *         F.weighted(cvr, "rerank.cvr_w", 50))
 *     .add(F.when(
 *         F.variant("rerank", "coupon_boost")
 *             .and(couponRoi.gt(F.val(0))),
 *         F.weighted(gmv, "rerank.coupon_gmv_w", 0.02),
 *         F.val(0)));
 * }</pre>
 *
 * <p>All branches are columnar; experiments and conditions do not require
 * rebuilding the graph.
 */
public final class F {

    private F() {
    }

    // ---- columns and constants -----------------------------------------

    public static NumExpr col(ColumnStream<Double> stream) {
        return new ColumnNum(stream);
    }

    public static NumExpr colLong(ColumnStream<Long> stream) {
        return new LongColumnNum(stream);
    }

    public static NumExpr val(double value) {
        return new ConstNum(value);
    }

    /** Experiment parameter evaluated per run, with a default. */
    public static NumExpr param(String key, double fallback) {
        return new ParamNum(key, fallback);
    }

    // ---- arithmetic -----------------------------------------------------

    public static NumExpr add(NumExpr left, NumExpr right) {
        return new BinaryNum(BinaryOp.ADD, left, right);
    }

    public static NumExpr sub(NumExpr left, NumExpr right) {
        return new BinaryNum(BinaryOp.SUB, left, right);
    }

    public static NumExpr mul(NumExpr left, NumExpr right) {
        return new BinaryNum(BinaryOp.MUL, left, right);
    }

    public static NumExpr div(NumExpr left, NumExpr right) {
        return new BinaryNum(BinaryOp.DIV, left, right);
    }

    public static NumExpr neg(NumExpr value) {
        return new UnaryNum(UnaryOp.NEG, value);
    }

    public static NumExpr abs(NumExpr value) {
        return new UnaryNum(UnaryOp.ABS, value);
    }

    public static NumExpr sqrt(NumExpr value) {
        return new UnaryNum(UnaryOp.SQRT, value);
    }

    public static NumExpr exp(NumExpr value) {
        return new UnaryNum(UnaryOp.EXP, value);
    }

    public static NumExpr log(NumExpr value) {
        return new UnaryNum(UnaryOp.LOG, value);
    }

    /** Sum of formula terms; useful for readable weighted re-rank scores. */
    public static NumExpr sum(NumExpr... terms) {
        if (terms == null || terms.length == 0) {
            throw new IllegalArgumentException("sum needs at least one term");
        }
        NumExpr result = terms[0];
        for (int i = 1; i < terms.length; i++) {
            result = add(result, terms[i]);
        }
        return result;
    }

    /** {@code expression * experimentParam(key, fallback)}. */
    public static NumExpr weighted(NumExpr expression, String key,
                                   double fallback) {
        return mul(expression, param(key, fallback));
    }

    public static NumExpr max(NumExpr left, NumExpr right) {
        return when(gt(left, right), left, right);
    }

    public static NumExpr min(NumExpr left, NumExpr right) {
        return when(lt(left, right), left, right);
    }

    /** Clamp to {@code [low, high]}. */
    public static NumExpr clamp(NumExpr value, NumExpr low, NumExpr high) {
        return max(low, min(value, high));
    }

    // ---- conditions -----------------------------------------------------

    public static BoolExpr gt(NumExpr left, NumExpr right) {
        return new CompareBool(CompareOp.GT, left, right);
    }

    public static BoolExpr ge(NumExpr left, NumExpr right) {
        return new CompareBool(CompareOp.GE, left, right);
    }

    public static BoolExpr lt(NumExpr left, NumExpr right) {
        return new CompareBool(CompareOp.LT, left, right);
    }

    public static BoolExpr le(NumExpr left, NumExpr right) {
        return new CompareBool(CompareOp.LE, left, right);
    }

    public static BoolExpr eq(NumExpr left, NumExpr right) {
        return new CompareBool(CompareOp.EQ, left, right);
    }

    public static BoolExpr neq(NumExpr left, NumExpr right) {
        return new CompareBool(CompareOp.NEQ, left, right);
    }

    public static BoolExpr and(BoolExpr left, BoolExpr right) {
        return new BoolComposite(BoolOp.AND, left, right);
    }

    public static BoolExpr and(BoolExpr... conditions) {
        if (conditions == null || conditions.length == 0) {
            throw new IllegalArgumentException("and needs at least one condition");
        }
        BoolExpr result = conditions[0];
        for (int i = 1; i < conditions.length; i++) {
            result = and(result, conditions[i]);
        }
        return result;
    }

    public static BoolExpr or(BoolExpr left, BoolExpr right) {
        return new BoolComposite(BoolOp.OR, left, right);
    }

    public static BoolExpr or(BoolExpr... conditions) {
        if (conditions == null || conditions.length == 0) {
            throw new IllegalArgumentException("or needs at least one condition");
        }
        BoolExpr result = conditions[0];
        for (int i = 1; i < conditions.length; i++) {
            result = or(result, conditions[i]);
        }
        return result;
    }

    public static BoolExpr not(BoolExpr value) {
        return new BoolComposite(BoolOp.NOT, value, null);
    }

    /** True when the request is assigned to an experiment variant. */
    public static BoolExpr variant(String layer, String variant) {
        return new VariantBool(layer, variant);
    }

    /** Conditional score: {@code condition ? whenTrue : whenFalse}. */
    public static NumExpr when(BoolExpr condition, NumExpr whenTrue,
                               NumExpr whenFalse) {
        return new ConditionalNum(condition, whenTrue, whenFalse);
    }

    public static NumExpr ifElse(BoolExpr condition, NumExpr whenTrue,
                                 NumExpr whenFalse) {
        return when(condition, whenTrue, whenFalse);
    }

    /**
     * Select a whole formula branch from an experiment layer.
     *
     * <p>This is useful for genuinely different rerank formulas, not just
     * coefficient changes. Unlisted variants fall back to {@code otherwise}.
     */
    public static NumExpr chooseVariant(String layer,
                                        Map<String, ? extends NumExpr> variants,
                                        NumExpr otherwise) {
        Objects.requireNonNull(layer, "layer");
        Objects.requireNonNull(otherwise, "otherwise");
        Objects.requireNonNull(variants, "variants");
        NumExpr result = otherwise;
        for (Map.Entry<String, ? extends NumExpr> entry : variants.entrySet()) {
            result = when(variant(layer, entry.getKey()), entry.getValue(), result);
        }
        return result;
    }

    /** Multi-branch conditional: later entries win when conditions overlap. */
    public static NumExpr caseWhen(Map<BoolExpr, ? extends NumExpr> branches,
                                   NumExpr otherwise) {
        Objects.requireNonNull(branches, "branches");
        Objects.requireNonNull(otherwise, "otherwise");
        NumExpr result = otherwise;
        for (Map.Entry<BoolExpr, ? extends NumExpr> entry : branches.entrySet()) {
            result = when(entry.getKey(), entry.getValue(), result);
        }
        return result;
    }

    // ---- expression implementations ------------------------------------

    private enum BinaryOp { ADD, SUB, MUL, DIV }

    private enum UnaryOp { NEG, ABS, SQRT, EXP, LOG }

    private enum CompareOp { GT, GE, LT, LE, EQ, NEQ }

    private enum BoolOp { AND, OR, NOT }

    private record ColumnNum(ColumnStream<Double> column) implements NumExpr {
        @Override
        public Tensor eval(FormulaContext ctx) {
            return asDouble(ctx.read(column));
        }

        @Override
        public Set<ColumnStream<?>> inputs() {
            return Set.<ColumnStream<?>>of(column);
        }
    }

    private record LongColumnNum(ColumnStream<Long> column) implements NumExpr {
        @Override
        public Tensor eval(FormulaContext ctx) {
            return asDouble(ctx.read(column));
        }

        @Override
        public Set<ColumnStream<?>> inputs() {
            return Set.<ColumnStream<?>>of(column);
        }
    }

    private record ConstNum(double value) implements NumExpr {
        @Override
        public Tensor eval(FormulaContext ctx) {
            return Tensor.full(ctx.shape(), value, DType.FLOAT64);
        }

        @Override
        public Set<ColumnStream<?>> inputs() {
            return Set.of();
        }
    }

    private record ParamNum(String key, double fallback) implements NumExpr {
        @Override
        public Tensor eval(FormulaContext ctx) {
            return Tensor.full(ctx.shape(),
                    ctx.experiment().param(key, fallback), DType.FLOAT64);
        }

        @Override
        public Set<ColumnStream<?>> inputs() {
            return Set.of();
        }
    }

    private record UnaryNum(UnaryOp op, NumExpr value) implements NumExpr {
        @Override
        public Tensor eval(FormulaContext ctx) {
            Tensor input = value.eval(ctx);
            return switch (op) {
                case NEG -> Compute.neg(input);
                case ABS -> Compute.abs(input);
                case SQRT -> Compute.sqrt(input);
                case EXP -> Compute.exp(input);
                case LOG -> Compute.log(input);
            };
        }

        @Override
        public Set<ColumnStream<?>> inputs() {
            return value.inputs();
        }
    }

    private record BinaryNum(BinaryOp op, NumExpr left, NumExpr right)
            implements NumExpr {
        @Override
        public Tensor eval(FormulaContext ctx) {
            Tensor a = left.eval(ctx);
            Tensor b = right.eval(ctx);
            return switch (op) {
                case ADD -> Compute.add(a, b);
                case SUB -> Compute.sub(a, b);
                case MUL -> Compute.mul(a, b);
                case DIV -> safeDiv(a, b);
            };
        }

        @Override
        public Set<ColumnStream<?>> inputs() {
            return union(left.inputs(), right.inputs());
        }
    }

    private record CompareBool(CompareOp op, NumExpr left, NumExpr right)
            implements BoolExpr {
        @Override
        public Tensor eval(FormulaContext ctx) {
            Tensor a = left.eval(ctx);
            Tensor b = right.eval(ctx);
            return switch (op) {
                case GT -> Compute.gt(a, b);
                case GE -> Compute.ge(a, b);
                case LT -> Compute.lt(a, b);
                case LE -> Compute.le(a, b);
                case EQ -> Compute.eq(a, b);
                case NEQ -> Compute.neq(a, b);
            };
        }

        @Override
        public Set<ColumnStream<?>> inputs() {
            return union(left.inputs(), right.inputs());
        }
    }

    private record BoolComposite(BoolOp op, BoolExpr left, BoolExpr right)
            implements BoolExpr {
        @Override
        public Tensor eval(FormulaContext ctx) {
            Tensor a = left.eval(ctx);
            return switch (op) {
                case AND -> Compute.mul(a, right.eval(ctx));
                case OR -> Compute.gtScalar(
                        Compute.add(a, right.eval(ctx)), 0.0);
                case NOT -> Compute.eqScalar(a, 0.0);
            };
        }

        @Override
        public Set<ColumnStream<?>> inputs() {
            return right == null ? left.inputs()
                    : union(left.inputs(), right.inputs());
        }
    }

    private record VariantBool(String layer, String variant) implements BoolExpr {
        @Override
        public Tensor eval(FormulaContext ctx) {
            long value = ctx.experiment().is(layer, variant) ? 1L : 0L;
            return Tensor.full(ctx.shape(), value, DType.INT64);
        }

        @Override
        public Set<ColumnStream<?>> inputs() {
            return Set.of();
        }
    }

    private record ConditionalNum(BoolExpr condition, NumExpr whenTrue,
                                  NumExpr whenFalse) implements NumExpr {
        @Override
        public Tensor eval(FormulaContext ctx) {
            return Compute.select(condition.eval(ctx),
                    whenTrue.eval(ctx), whenFalse.eval(ctx));
        }

        @Override
        public Set<ColumnStream<?>> inputs() {
            return union(condition.inputs(), whenTrue.inputs(),
                    whenFalse.inputs());
        }
    }

    private static Tensor asDouble(Tensor tensor) {
        return tensor.dtype() == DType.FLOAT64
                ? tensor : tensor.castTo(DType.FLOAT64);
    }

    private static Tensor safeDiv(Tensor numerator, Tensor denominator) {
        Tensor result = Compute.div(numerator, denominator);
        Tensor zero = Tensor.zeros(result.shape(), DType.FLOAT64);
        Tensor mask = Compute.eqScalar(denominator, 0.0);
        if (!mask.shape().equals(result.shape())) {
            mask = broadcastMask(mask, result);
        }
        return Compute.select(mask, zero, result);
    }

    private static Tensor broadcastMask(Tensor mask, Tensor target) {
        if (mask.size() == target.size()) {
            Tensor result = Tensor.zeros(target.shape(), DType.INT64);
            System.arraycopy(mask.longData(), 0, result.longData(), 0,
                    mask.size());
            return result;
        }
        if (mask.size() == 1) {
            return Tensor.full(target.shape(), mask.longData()[0],
                    DType.INT64);
        }
        throw new IllegalArgumentException(
                "formula: cannot broadcast denominator mask " + mask.shape()
                        + " to " + target.shape());
    }

    @SafeVarargs
    private static Set<ColumnStream<?>> union(
            Set<ColumnStream<?>>... inputs) {
        LinkedHashSet<ColumnStream<?>> result = new LinkedHashSet<>();
        for (Set<ColumnStream<?>> input : inputs) {
            result.addAll(Objects.requireNonNull(input, "inputs"));
        }
        return Set.copyOf(result);
    }
}
