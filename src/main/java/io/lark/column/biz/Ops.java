// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import io.lark.column.DType;
import io.lark.column.op.OpSpec;

import java.util.List;
import java.util.Map;

/** Convenience builders for pure-code module orchestration. */
public final class Ops {

    private Ops() {
    }

    private static OpSpec binary(String type, String out, String a, String b) {
        return new OpSpec(type, List.of(a, b), List.of(out));
    }

    private static OpSpec scalar(String type, String out, String a, double value) {
        return new OpSpec(type, List.of(a), List.of(out),
                Map.of("scalar", Double.toString(value)));
    }

    private static OpSpec unary(String type, String out, String a) {
        return new OpSpec(type, List.of(a), List.of(out));
    }

    public static OpSpec add(String out, String a, String b) {
        return binary("add", out, a, b);
    }

    public static OpSpec sub(String out, String a, String b) {
        return binary("sub", out, a, b);
    }

    public static OpSpec mul(String out, String a, String b) {
        return binary("mul", out, a, b);
    }

    public static OpSpec div(String out, String a, String b) {
        return binary("div", out, a, b);
    }

    public static OpSpec addScalar(String out, String a, double value) {
        return scalar("add_scalar", out, a, value);
    }

    public static OpSpec subScalar(String out, String a, double value) {
        return scalar("sub_scalar", out, a, value);
    }

    public static OpSpec mulScalar(String out, String a, double value) {
        return scalar("mul_scalar", out, a, value);
    }

    public static OpSpec divScalar(String out, String a, double value) {
        return scalar("div_scalar", out, a, value);
    }

    public static OpSpec neg(String out, String a) {
        return unary("neg", out, a);
    }

    public static OpSpec abs(String out, String a) {
        return unary("abs", out, a);
    }

    public static OpSpec square(String out, String a) {
        return unary("square", out, a);
    }

    public static OpSpec sqrt(String out, String a) {
        return unary("sqrt", out, a);
    }

    public static OpSpec exp(String out, String a) {
        return unary("exp", out, a);
    }

    public static OpSpec log(String out, String a) {
        return unary("log", out, a);
    }

    public static OpSpec sum(String out, String a) {
        return unary("sum", out, a);
    }

    public static OpSpec mean(String out, String a) {
        return unary("mean", out, a);
    }

    public static OpSpec count(String out, String a) {
        return unary("count", out, a);
    }

    public static OpSpec max(String out, String a) {
        return unary("max", out, a);
    }

    public static OpSpec min(String out, String a) {
        return unary("min", out, a);
    }

    public static OpSpec stddev(String out, String a) {
        return unary("stddev", out, a);
    }

    public static OpSpec gt(String out, String a, String b) {
        return binary("gt", out, a, b);
    }

    public static OpSpec ge(String out, String a, String b) {
        return binary("ge", out, a, b);
    }

    public static OpSpec lt(String out, String a, String b) {
        return binary("lt", out, a, b);
    }

    public static OpSpec le(String out, String a, String b) {
        return binary("le", out, a, b);
    }

    public static OpSpec eq(String out, String a, String b) {
        return binary("eq", out, a, b);
    }

    public static OpSpec neq(String out, String a, String b) {
        return binary("neq", out, a, b);
    }

    public static OpSpec gtScalar(String out, String a, double value) {
        return scalar("gt_scalar", out, a, value);
    }

    public static OpSpec geScalar(String out, String a, double value) {
        return scalar("ge_scalar", out, a, value);
    }

    public static OpSpec ltScalar(String out, String a, double value) {
        return scalar("lt_scalar", out, a, value);
    }

    public static OpSpec leScalar(String out, String a, double value) {
        return scalar("le_scalar", out, a, value);
    }

    public static OpSpec eqScalar(String out, String a, double value) {
        return scalar("eq_scalar", out, a, value);
    }

    public static OpSpec neqScalar(String out, String a, double value) {
        return scalar("neq_scalar", out, a, value);
    }

    public static OpSpec select(String out, String mask, String a, String b) {
        return new OpSpec("select", List.of(mask, a, b), List.of(out));
    }

    public static OpSpec filter(String out, String data, String mask) {
        return new OpSpec("filter", List.of(data, mask), List.of(out));
    }

    public static OpSpec cast(String out, String a, String dtype) {
        return new OpSpec("cast", List.of(a), List.of(out),
                Map.of("dtype", dtype));
    }

    public static OpSpec quantize(String out, String a) {
        return unary("quantize", out, a);
    }

    public static OpSpec dequantize(String out, String a) {
        return unary("dequantize", out, a);
    }

    public static OpSpec dot(String out, String a, String b) {
        return new OpSpec("dot", List.of(a, b), List.of(out));
    }

    /** Length-1 floating-point constant. */
    public static OpSpec constant(String out, double value) {
        return constant(out, Double.toString(value), DType.FLOAT64, 1);
    }

    /** Typed constant, scalar or comma-separated list. */
    public static OpSpec constant(String out, String value, DType dtype, int size) {
        return new OpSpec("const", List.of(), List.of(out),
                Map.of("value", value,
                        "dtype", dtype.displayName(),
                        "size", Integer.toString(size)));
    }

    public static OpSpec identity(String out, String in) {
        return new OpSpec("identity", List.of(in), List.of(out));
    }
}
