// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.op;

import io.lark.column.DType;
import io.lark.column.Tensor;
import io.lark.column.compute.Compute;

import java.util.List;

/** Built-in tensor-operation factory (CPU backend). */
public final class TensorOps {

    private TensorOps() {
    }

    /**
     * Create an {@link OpTensor} for a spec.
     *
     * @throws IllegalArgumentException on an unknown type or missing attr/input
     */
    public static OpTensor create(OpSpec spec) {
        return switch (spec.type()) {
            case "add" -> new AddTensor(spec.inputs(), spec.outputs());
            case "sub" -> new SubTensor(spec.inputs(), spec.outputs());
            case "mul" -> new MultiTensor(spec.inputs(), spec.outputs());
            case "div" -> new DivTensor(spec.inputs(), spec.outputs());
            case "add_scalar", "sub_scalar", "mul_scalar", "div_scalar" ->
                    new ScalarArithOp(spec);
            case "neg", "abs", "square", "sqrt", "exp", "log" -> new UnaryOp(spec);
            case "sum", "mean", "count", "max", "min", "stddev" -> new ReduceOp(spec);
            case "gt", "ge", "lt", "le", "eq", "neq" -> new CompareOp(spec);
            case "gt_scalar", "ge_scalar", "lt_scalar", "le_scalar",
                    "eq_scalar", "neq_scalar" -> new CompareScalarOp(spec);
            case "select" -> new SelectOp(spec);
            case "filter" -> new FilterOp(spec);
            case "cast" -> new CastOp(spec);
            case "quantize" -> new QuantizeOp(spec);
            case "dequantize" -> new DequantizeOp(spec);
            case "dot" -> new DotQ8Op(spec);
            case "const" -> new ConstOp(spec);
            case "identity" -> new IdentityOp(spec);
            case "placeholder" -> new PlaceholderOp(spec);
            default -> throw new IllegalArgumentException(
                    "create_op: unknown op type '" + spec.type() + "'");
        };
    }

    public static OpTensor placeholder(String output) {
        return new PlaceholderOp(new OpSpec("placeholder", List.of(), List.of(output)));
    }

    private abstract static class BaseOp extends OpTensor {
        protected final String type;
        protected final List<String> inputs;
        protected final List<String> outputs;

        private BaseOp(OpSpec spec) {
            super(spec.type(), spec.inputs(), spec.outputs());
            this.type = spec.type();
            this.inputs = spec.inputs();
            this.outputs = spec.outputs();
        }
    }

    private static final class ScalarArithOp extends BaseOp {
        private final double scalar;

        private ScalarArithOp(OpSpec spec) {
            super(spec);
            requireInputs(inputs, 1, type);
            if (!spec.hasAttr("scalar")) {
                throw new IllegalArgumentException(
                        "scalar op '" + type + "' requires attr 'scalar'");
            }
            this.scalar = Double.parseDouble(spec.attr("scalar"));
        }

        @Override
        public void compute(OpContext ctx) {
            Tensor a = ctx.input(inputs.get(0));
            Tensor result = switch (type) {
                case "add_scalar" -> Compute.addScalar(a, scalar);
                case "sub_scalar" -> Compute.subScalar(a, scalar);
                case "mul_scalar" -> Compute.mulScalar(a, scalar);
                case "div_scalar" -> Compute.divScalar(a, scalar);
                default -> throw new IllegalStateException("unreachable");
            };
            ctx.set(outputs.get(0), result);
        }
    }

    private static final class UnaryOp extends BaseOp {
        private UnaryOp(OpSpec spec) {
            super(spec);
            requireInputs(inputs, 1, type);
        }

        @Override
        public void compute(OpContext ctx) {
            Tensor a = ctx.input(inputs.get(0));
            Tensor result = switch (type) {
                case "neg" -> Compute.neg(a);
                case "abs" -> Compute.abs(a);
                case "square" -> Compute.square(a);
                case "sqrt" -> Compute.sqrt(a);
                case "exp" -> Compute.exp(a);
                case "log" -> Compute.log(a);
                default -> throw new IllegalStateException("unreachable");
            };
            ctx.set(outputs.get(0), result);
        }
    }

    private static final class ReduceOp extends BaseOp {
        private ReduceOp(OpSpec spec) {
            super(spec);
            requireInputs(inputs, 1, type);
        }

        @Override
        public void compute(OpContext ctx) {
            Tensor a = ctx.input(inputs.get(0));
            Tensor result = switch (type) {
                case "sum" -> Compute.sumTensor(a);
                case "mean" -> Compute.meanTensor(a);
                case "count" -> Compute.countTensor(a);
                case "max" -> Compute.maxTensor(a);
                case "min" -> Compute.minTensor(a);
                case "stddev" -> Compute.stddevTensor(a);
                default -> throw new IllegalStateException("unreachable");
            };
            ctx.set(outputs.get(0), result);
        }
    }

    private static final class CompareOp extends BaseOp {
        private CompareOp(OpSpec spec) {
            super(spec);
            requireInputs(inputs, 2, type);
        }

        @Override
        public void compute(OpContext ctx) {
            Tensor a = ctx.input(inputs.get(0));
            Tensor b = ctx.input(inputs.get(1));
            Tensor result = switch (type) {
                case "gt" -> Compute.gt(a, b);
                case "ge" -> Compute.ge(a, b);
                case "lt" -> Compute.lt(a, b);
                case "le" -> Compute.le(a, b);
                case "eq" -> Compute.eq(a, b);
                case "neq" -> Compute.neq(a, b);
                default -> throw new IllegalStateException("unreachable");
            };
            ctx.set(outputs.get(0), result);
        }
    }

    private static final class CompareScalarOp extends BaseOp {
        private final double scalar;

        private CompareScalarOp(OpSpec spec) {
            super(spec);
            requireInputs(inputs, 1, type);
            if (!spec.hasAttr("scalar")) {
                throw new IllegalArgumentException(
                        "scalar compare '" + type + "' requires attr 'scalar'");
            }
            this.scalar = Double.parseDouble(spec.attr("scalar"));
        }

        @Override
        public void compute(OpContext ctx) {
            Tensor a = ctx.input(inputs.get(0));
            Tensor result = switch (type) {
                case "gt_scalar" -> Compute.gtScalar(a, scalar);
                case "ge_scalar" -> Compute.geScalar(a, scalar);
                case "lt_scalar" -> Compute.ltScalar(a, scalar);
                case "le_scalar" -> Compute.leScalar(a, scalar);
                case "eq_scalar" -> Compute.eqScalar(a, scalar);
                case "neq_scalar" -> Compute.neqScalar(a, scalar);
                default -> throw new IllegalStateException("unreachable");
            };
            ctx.set(outputs.get(0), result);
        }
    }

    private static final class SelectOp extends BaseOp {
        private SelectOp(OpSpec spec) {
            super(spec);
            requireInputs(inputs, 3, "select (mask,a,b)");
        }

        @Override
        public void compute(OpContext ctx) {
            ctx.set(outputs.get(0), Compute.select(
                    ctx.input(inputs.get(0)),
                    ctx.input(inputs.get(1)),
                    ctx.input(inputs.get(2))));
        }
    }

    private static final class FilterOp extends BaseOp {
        private FilterOp(OpSpec spec) {
            super(spec);
            requireInputs(inputs, 2, "filter (data,mask)");
        }

        @Override
        public void compute(OpContext ctx) {
            ctx.set(outputs.get(0), Compute.filter(
                    ctx.input(inputs.get(0)),
                    ctx.input(inputs.get(1))));
        }
    }

    private static final class CastOp extends BaseOp {
        private final DType dtype;

        private CastOp(OpSpec spec) {
            super(spec);
            requireInputs(inputs, 1, "cast");
            if (!spec.hasAttr("dtype")) {
                throw new IllegalArgumentException("cast requires attr 'dtype'");
            }
            this.dtype = DType.parse(spec.attr("dtype"));
        }

        @Override
        public void compute(OpContext ctx) {
            ctx.set(outputs.get(0), Compute.cast(ctx.input(inputs.get(0)), dtype));
        }
    }

    private static final class QuantizeOp extends BaseOp {
        private QuantizeOp(OpSpec spec) {
            super(spec);
            requireInputs(inputs, 1, "quantize");
        }

        @Override
        public void compute(OpContext ctx) {
            ctx.set(outputs.get(0), Compute.quantize(ctx.input(inputs.get(0))));
        }
    }

    private static final class DequantizeOp extends BaseOp {
        private DequantizeOp(OpSpec spec) {
            super(spec);
            requireInputs(inputs, 1, "dequantize");
        }

        @Override
        public void compute(OpContext ctx) {
            ctx.set(outputs.get(0), Compute.dequantize(ctx.input(inputs.get(0))));
        }
    }

    private static final class DotQ8Op extends BaseOp {
        private DotQ8Op(OpSpec spec) {
            super(spec);
            requireInputs(inputs, 2, "dot");
        }

        @Override
        public void compute(OpContext ctx) {
            double value = Compute.dot(
                    ctx.input(inputs.get(0)),
                    ctx.input(inputs.get(1)));
            ctx.set(outputs.get(0), Tensor.fromDoubles(value));
        }
    }

    private static final class ConstOp extends BaseOp {
        private final String value;
        private final DType dtype;
        private final int size;

        private ConstOp(OpSpec spec) {
            super(spec);
            if (!spec.hasAttr("value")) {
                throw new IllegalArgumentException("const requires attr 'value'");
            }
            this.value = spec.attr("value");
            this.dtype = DType.parse(spec.attr("dtype", "float64"));
            this.size = Integer.parseInt(spec.attr("size", "1"));
        }

        @Override
        public void compute(OpContext ctx) {
            ctx.set(outputs.get(0), Compute.constant(value, dtype, size));
        }
    }

    private static final class IdentityOp extends BaseOp {
        private IdentityOp(OpSpec spec) {
            super(spec);
            requireInputs(inputs, 1, "identity");
        }

        @Override
        public void compute(OpContext ctx) {
            String input = inputs.get(0);
            String output = outputs.get(0);
            if (input.equals(output)) {
                return; // pure ordering anchor
            }
            ctx.set(output, Compute.identity(ctx.input(input)));
        }
    }

    private static final class PlaceholderOp extends BaseOp {
        private PlaceholderOp(OpSpec spec) {
            super(spec);
        }

        @Override
        public void compute(OpContext ctx) {
            throw new IllegalStateException(
                    "placeholder executed: unresolved forward reference");
        }
    }

    private static void requireInputs(List<String> inputs, int count, String op) {
        if (inputs.size() != count) {
            throw new IllegalArgumentException(
                    op + " requires " + count + " input(s), got " + inputs.size());
        }
    }
}
