// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.tensor;

import io.lark.column.compute.Compute;
import io.lark.column.op.OpSpec;

import java.util.List;
import java.util.Map;

/**
 * Factory for pure tensor kernels.
 *
 * <p>The returned kernel is execution-only. It receives resolved inputs and an
 * output sink through {@link KernelContext}; it does not know graph node ids or
 * dependency edges. Graph construction lives in
 * {@link io.lark.column.op.OpSpec}.
 */
public final class TensorKernels {

    private TensorKernels() {
    }

    public static TensorKernel create(OpSpec spec) {
        return switch (spec.type()) {
            case "add" -> binary(spec, "add");
            case "sub" -> binary(spec, "sub");
            case "mul" -> binary(spec, "mul");
            case "div" -> binary(spec, "div");
            case "mul_add", "fma" -> mulAdd(spec);
            case "add_scalar" -> scalar(spec, "add");
            case "sub_scalar" -> scalar(spec, "sub");
            case "mul_scalar" -> scalar(spec, "mul");
            case "div_scalar" -> scalar(spec, "div");
            case "neg", "abs", "square", "sqrt", "exp", "log" ->
                    unary(spec);
            case "sum", "mean", "count", "max", "min", "stddev" ->
                    reduce(spec);
            case "gt", "ge", "lt", "le", "eq", "neq" -> compare(spec);
            case "gt_scalar", "ge_scalar", "lt_scalar", "le_scalar",
                    "eq_scalar", "neq_scalar" -> compareScalar(spec);
            case "select" -> select(spec);
            case "filter" -> filter(spec);
            case "cast" -> cast(spec);
            case "quantize" -> quantize(spec);
            case "dequantize" -> dequantize(spec);
            case "dot" -> dot(spec);
            case "const" -> constant(spec);
            case "zeros" -> zeros(spec);
            case "identity" -> identity(spec);
            case "placeholder" -> placeholder(spec);
            default -> throw new IllegalArgumentException(
                    "create_kernel: unknown op type '" + spec.type() + "'");
        };
    }

    public static OpSpec spec(io.lark.column.op.OpTensor op) {
        return new OpSpec(op.opType(), List.of(op.inputs()),
                List.of(op.outputs()), Map.of());
    }

    public static TensorKernel legacy(io.lark.column.op.OpTensor op) {
        return new LegacyTensorKernel(op);
    }

    private static TensorKernel binary(OpSpec spec, String type) {
        requireInputs(spec, 2);
        requireOutputs(spec, 1);
        return ctx -> {
            Tensor a = ctx.input(0);
            Tensor b = ctx.input(1);
            Tensor result = switch (type) {
                case "add" -> Compute.add(a, b);
                case "sub" -> Compute.sub(a, b);
                case "mul" -> Compute.mul(a, b);
                case "div" -> Compute.div(a, b);
                default -> throw new IllegalStateException("unreachable");
            };
            ctx.output(result);
        };
    }

    private static TensorKernel mulAdd(OpSpec spec) {
        requireInputs(spec, 3);
        requireOutputs(spec, 1);
        return ctx -> ctx.output(Compute.mulAdd(
                ctx.input(0), ctx.input(1), ctx.input(2)));
    }

    private static TensorKernel scalar(OpSpec spec, String type) {
        requireInputs(spec, 1);
        requireOutputs(spec, 1);
        double value = Double.parseDouble(requireAttr(spec, "scalar"));
        return ctx -> {
            Tensor input = ctx.input(0);
            Tensor result = switch (type) {
                case "add" -> Compute.addScalar(input, value);
                case "sub" -> Compute.subScalar(input, value);
                case "mul" -> Compute.mulScalar(input, value);
                case "div" -> Compute.divScalar(input, value);
                default -> throw new IllegalStateException("unreachable");
            };
            ctx.output(result);
        };
    }

    private static TensorKernel unary(OpSpec spec) {
        requireInputs(spec, 1);
        requireOutputs(spec, 1);
        String type = spec.type();
        return ctx -> {
            Tensor input = ctx.input(0);
            Tensor result = switch (type) {
                case "neg" -> Compute.neg(input);
                case "abs" -> Compute.abs(input);
                case "square" -> Compute.square(input);
                case "sqrt" -> Compute.sqrt(input);
                case "exp" -> Compute.exp(input);
                case "log" -> Compute.log(input);
                default -> throw new IllegalStateException("unreachable");
            };
            ctx.output(result);
        };
    }

    private static TensorKernel reduce(OpSpec spec) {
        requireInputs(spec, 1);
        requireOutputs(spec, 1);
        String type = spec.type();
        return ctx -> {
            Tensor input = ctx.input(0);
            Tensor result = switch (type) {
                case "sum" -> Compute.sumTensor(input);
                case "mean" -> Compute.meanTensor(input);
                case "count" -> Compute.countTensor(input);
                case "max" -> Compute.maxTensor(input);
                case "min" -> Compute.minTensor(input);
                case "stddev" -> Compute.stddevTensor(input);
                default -> throw new IllegalStateException("unreachable");
            };
            ctx.output(result);
        };
    }

    private static TensorKernel compare(OpSpec spec) {
        requireInputs(spec, 2);
        requireOutputs(spec, 1);
        String type = spec.type();
        return ctx -> {
            Tensor a = ctx.input(0);
            Tensor b = ctx.input(1);
            Tensor result = switch (type) {
                case "gt" -> Compute.gt(a, b);
                case "ge" -> Compute.ge(a, b);
                case "lt" -> Compute.lt(a, b);
                case "le" -> Compute.le(a, b);
                case "eq" -> Compute.eq(a, b);
                case "neq" -> Compute.neq(a, b);
                default -> throw new IllegalStateException("unreachable");
            };
            ctx.output(result);
        };
    }

    private static TensorKernel compareScalar(OpSpec spec) {
        requireInputs(spec, 1);
        requireOutputs(spec, 1);
        double value = Double.parseDouble(requireAttr(spec, "scalar"));
        String type = spec.type();
        return ctx -> {
            Tensor input = ctx.input(0);
            Tensor result = switch (type) {
                case "gt_scalar" -> Compute.gtScalar(input, value);
                case "ge_scalar" -> Compute.geScalar(input, value);
                case "lt_scalar" -> Compute.ltScalar(input, value);
                case "le_scalar" -> Compute.leScalar(input, value);
                case "eq_scalar" -> Compute.eqScalar(input, value);
                case "neq_scalar" -> Compute.neqScalar(input, value);
                default -> throw new IllegalStateException("unreachable");
            };
            ctx.output(result);
        };
    }

    private static TensorKernel select(OpSpec spec) {
        requireInputs(spec, 3);
        requireOutputs(spec, 1);
        return ctx -> ctx.output(Compute.select(
                ctx.input(0), ctx.input(1), ctx.input(2)));
    }

    private static TensorKernel filter(OpSpec spec) {
        requireInputs(spec, 2);
        requireOutputs(spec, 1);
        return ctx -> ctx.output(Compute.filter(ctx.input(0), ctx.input(1)));
    }

    private static TensorKernel cast(OpSpec spec) {
        requireInputs(spec, 1);
        requireOutputs(spec, 1);
        DType dtype = DType.parse(requireAttr(spec, "dtype"));
        return ctx -> ctx.output(Compute.cast(ctx.input(0), dtype));
    }

    private static TensorKernel quantize(OpSpec spec) {
        requireInputs(spec, 1);
        requireOutputs(spec, 1);
        return ctx -> ctx.output(Compute.quantize(ctx.input(0)));
    }

    private static TensorKernel dequantize(OpSpec spec) {
        requireInputs(spec, 1);
        requireOutputs(spec, 1);
        return ctx -> ctx.output(Compute.dequantize(ctx.input(0)));
    }

    private static TensorKernel dot(OpSpec spec) {
        requireInputs(spec, 2);
        requireOutputs(spec, 1);
        return ctx -> ctx.output(Tensor.fromDoubles(
                Compute.dot(ctx.input(0), ctx.input(1))));
    }

    private static TensorKernel constant(OpSpec spec) {
        requireOutputs(spec, 1);
        String value = requireAttr(spec, "value");
        DType dtype = DType.parse(spec.attr("dtype", "float64"));
        TensorShape shape = spec.hasAttr("shape")
                ? TensorShape.parse(spec.attr("shape"))
                : value.indexOf(',') < 0
                        ? TensorShape.vector(Integer.parseInt(spec.attr("size", "1")))
                        : TensorShape.vector(value.split(",").length);
        return ctx -> ctx.output(Compute.constant(value, dtype, shape));
    }

    private static TensorKernel zeros(OpSpec spec) {
        requireOutputs(spec, 1);
        DType dtype = DType.parse(spec.attr("dtype", "float64"));
        TensorShape shape = spec.hasAttr("shape")
                ? TensorShape.parse(spec.attr("shape"))
                : TensorShape.vector(Integer.parseInt(spec.attr("size", "1")));
        return ctx -> ctx.output(Tensor.zeros(shape, dtype));
    }

    private static TensorKernel identity(OpSpec spec) {
        requireInputs(spec, 1);
        requireOutputs(spec, 1);
        return ctx -> ctx.output(Compute.identity(ctx.input(0)));
    }

    private static TensorKernel placeholder(OpSpec spec) {
        return ctx -> {
            throw new IllegalStateException(
                    "placeholder executed: unresolved forward reference");
        };
    }

    private static String requireAttr(OpSpec spec, String name) {
        if (!spec.hasAttr(name)) {
            throw new IllegalArgumentException(
                    "op '" + spec.type() + "' requires attr '" + name + "'");
        }
        return spec.attr(name);
    }

    private static void requireInputs(OpSpec spec, int count) {
        if (spec.inputs().size() != count) {
            throw new IllegalArgumentException(
                    "op '" + spec.type() + "' requires " + count
                            + " input(s), got " + spec.inputs().size());
        }
    }

    private static void requireOutputs(OpSpec spec, int count) {
        if (spec.outputs().size() != count) {
            throw new IllegalArgumentException(
                    "op '" + spec.type() + "' requires " + count
                            + " output(s), got " + spec.outputs().size());
        }
    }
}
