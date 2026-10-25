// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.compute;

import io.lark.column.DType;
import io.lark.column.Tensor;

import jdk.incubator.vector.DoubleVector;
import jdk.incubator.vector.FloatVector;
import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.LongVector;
import jdk.incubator.vector.VectorMask;
import jdk.incubator.vector.VectorOperators;

/**
 * SIMD kernels backed by the JDK incubator Vector API.
 *
 * <p>Element-wise arithmetic, scalar broadcasting, comparisons and reductions
 * operate on whole vectors ({@code *Vector.SPECIES_PREFERRED}) with a scalar
 * tail. Mixed dtypes are cast to the promoted output type before the kernel
 * runs.
 */
final class VectorOps {

    enum Binary {
        ADD, SUB, MUL, DIV
    }

    enum Cmp {
        GT, GE, LT, LE, EQ, NEQ
    }

    enum Unary {
        NEG, ABS, SQUARE
    }

    private VectorOps() {
    }

    // ---- binary ---------------------------------------------------------

    static Tensor binary(Tensor a, Tensor b, DType outDtype, Binary op) {
        if (a.size() != b.size()) {
            throw new IllegalArgumentException(
                    "binary op: length mismatch (" + a.size() + " vs " + b.size() + ")");
        }
        Tensor left = a.dtype() == outDtype ? a : a.castTo(outDtype);
        Tensor right = b.dtype() == outDtype ? b : b.castTo(outDtype);
        Tensor result = Tensor.zeros(a.size(), outDtype);
        switch (outDtype) {
            case INT32 -> binaryInt(left.intData(), right.intData(),
                    result.intData(), op);
            case INT64 -> binaryLong(left.longData(), right.longData(),
                    result.longData(), op);
            case FLOAT32 -> binaryFloat(left.floatData(), right.floatData(),
                    result.floatData(), op);
            case FLOAT64 -> binaryDouble(left.doubleData(), right.doubleData(),
                    result.doubleData(), op);
            case Q8_0 -> throw new IllegalArgumentException(
                    "elementwise ops do not support q8_0 directly");
        }
        return result;
    }

    static Tensor scalar(Tensor a, double value, DType outDtype, Binary op) {
        Tensor input = a.dtype() == outDtype ? a : a.castTo(outDtype);
        Tensor result = Tensor.zeros(a.size(), outDtype);
        switch (outDtype) {
            case INT32 -> scalarInt(input.intData(), (int) value, result.intData(), op);
            case INT64 -> scalarLong(input.longData(), (long) value, result.longData(), op);
            case FLOAT32 -> scalarFloat(input.floatData(), (float) value,
                    result.floatData(), op);
            case FLOAT64 -> scalarDouble(input.doubleData(), value,
                    result.doubleData(), op);
            case Q8_0 -> throw new IllegalArgumentException(
                    "scalar ops do not support q8_0 directly");
        }
        return result;
    }

    // ---- comparisons ----------------------------------------------------

    static Tensor compare(Tensor a, Tensor b, Cmp cmp) {
        if (a.size() != b.size()) {
            throw new IllegalArgumentException(
                    "compare: length mismatch (" + a.size() + " vs " + b.size() + ")");
        }
        DType dtype = Compute.promote(a.dtype(), b.dtype());
        Tensor left = a.dtype() == dtype ? a : a.castTo(dtype);
        Tensor right = b.dtype() == dtype ? b : b.castTo(dtype);
        Tensor result = Tensor.zeros(a.size(), DType.INT64);
        long[] out = result.longData();

        switch (dtype) {
            case INT32 -> compareInt(left.intData(), right.intData(), out, cmp);
            case INT64 -> compareLong(left.longData(), right.longData(), out, cmp);
            case FLOAT32 -> compareFloat(left.floatData(), right.floatData(), out, cmp);
            case FLOAT64 -> compareDouble(left.doubleData(), right.doubleData(), out, cmp);
            case Q8_0 -> throw new IllegalArgumentException(
                    "comparisons do not support q8_0 directly");
        }
        return result;
    }

    static Tensor compareScalar(Tensor a, double value, Cmp cmp) {
        Tensor result = Tensor.zeros(a.size(), DType.INT64);
        long[] out = result.longData();
        switch (a.dtype()) {
            case INT32 -> compareIntScalar(a.intData(), (int) value, out, cmp);
            case INT64 -> compareLongScalar(a.longData(), (long) value, out, cmp);
            case FLOAT32 -> compareFloatScalar(a.floatData(), (float) value, out, cmp);
            case FLOAT64 -> compareDoubleScalar(a.doubleData(), value, out, cmp);
            case Q8_0 -> throw new IllegalArgumentException(
                    "comparisons do not support q8_0 directly");
        }
        return result;
    }

    // ---- unary ----------------------------------------------------------

    static Tensor unary(Tensor a, Unary op) {
        DType dtype = a.dtype();
        Tensor result = Tensor.zeros(a.size(), dtype);
        switch (dtype) {
            case INT32 -> unaryInt(a.intData(), result.intData(), op);
            case INT64 -> unaryLong(a.longData(), result.longData(), op);
            case FLOAT32 -> unaryFloat(a.floatData(), result.floatData(), op);
            case FLOAT64 -> unaryDouble(a.doubleData(), result.doubleData(), op);
            case Q8_0 -> throw new IllegalArgumentException(
                    "unary ops do not support q8_0 directly");
        }
        return result;
    }

    /** sqrt always returns float64, like the C++ compute layer. */
    static Tensor sqrt(Tensor a) {
        Tensor input = a.dtype() == DType.FLOAT64 ? a : a.castTo(DType.FLOAT64);
        Tensor result = Tensor.zeros(a.size(), DType.FLOAT64);
        double[] src = input.doubleData();
        double[] out = result.doubleData();
        var species = DoubleVector.SPECIES_PREFERRED;
        int lanes = species.length();
        int i = 0;
        for (; i + lanes <= src.length; i += lanes) {
            DoubleVector.fromArray(species, src, i).sqrt().intoArray(out, i);
        }
        for (; i < src.length; i++) {
            out[i] = Math.sqrt(src[i]);
        }
        return result;
    }

    /** exp/log keep the C++ execution-layer behavior: always float64. */
    static Tensor transcendental(Tensor a, boolean exp) {
        Tensor input = a.dtype() == DType.FLOAT64 ? a : a.castTo(DType.FLOAT64);
        Tensor result = Tensor.zeros(a.size(), DType.FLOAT64);
        double[] src = input.doubleData();
        double[] out = result.doubleData();
        for (int i = 0; i < src.length; i++) {
            out[i] = exp ? Math.exp(src[i]) : Math.log(src[i]);
        }
        return result;
    }

    // ---- reductions -----------------------------------------------------

    static double sum(Tensor a) {
        return switch (a.dtype()) {
            case INT32 -> sumInt(a.intData());
            case INT64 -> sumLong(a.longData());
            case FLOAT32 -> sumFloat(a.floatData());
            case FLOAT64 -> sumDouble(a.doubleData());
            case Q8_0 -> sumDouble(a.dequantize().doubleData());
        };
    }

    static Tensor minMax(Tensor a, boolean wantMax) {
        if (a.size() == 0) {
            return a.dtype() == DType.Q8_0
                    ? Tensor.fromDoubles(0.0)
                    : Tensor.full(1, 0.0, a.dtype());
        }
        return switch (a.dtype()) {
            case INT32 -> Tensor.fromInts(minMaxInt(a.intData(), wantMax));
            case INT64 -> Tensor.fromLongs(minMaxLong(a.longData(), wantMax));
            case FLOAT32 -> Tensor.fromFloats(minMaxFloat(a.floatData(), wantMax));
            case FLOAT64 -> Tensor.fromDoubles(minMaxDouble(a.doubleData(), wantMax));
            case Q8_0 -> minMax(a.dequantize(), wantMax);
        };
    }

    static double stddev(Tensor a) {
        int n = a.size();
        if (n == 0) {
            return 0.0;
        }
        Tensor input = a.dtype() == DType.FLOAT64 ? a : a.castTo(DType.FLOAT64);
        double[] src = input.doubleData();
        double mean = sumDouble(src) / n;

        // Second pass only: subtracting into a temporary diff[] doubles the
        // memory traffic for no benefit.
        var species = DoubleVector.SPECIES_PREFERRED;
        int lanes = species.length();
        DoubleVector meanVector = DoubleVector.broadcast(species, mean);
        double sq = 0.0;
        int i = 0;
        for (; i + lanes <= n; i += lanes) {
            DoubleVector diff = DoubleVector.fromArray(species, src, i)
                    .sub(meanVector);
            sq += diff.mul(diff).reduceLanes(VectorOperators.ADD);
        }
        for (; i < n; i++) {
            double diff = src[i] - mean;
            sq += diff * diff;
        }
        return Math.sqrt(sq / n);
    }

    // ---- int32 kernels --------------------------------------------------

    private static void binaryInt(int[] a, int[] b, int[] out, Binary op) {
        var species = IntVector.SPECIES_PREFERRED;
        int lanes = species.length();
        int i = 0;
        for (; i + lanes <= a.length; i += lanes) {
            IntVector va = IntVector.fromArray(species, a, i);
            IntVector vb = IntVector.fromArray(species, b, i);
            switch (op) {
                case ADD -> va.add(vb).intoArray(out, i);
                case SUB -> va.sub(vb).intoArray(out, i);
                case MUL -> va.mul(vb).intoArray(out, i);
                case DIV -> throw new IllegalArgumentException(
                        "int division is promoted to float64");
            }
        }
        for (; i < a.length; i++) {
            out[i] = switch (op) {
                case ADD -> a[i] + b[i];
                case SUB -> a[i] - b[i];
                case MUL -> a[i] * b[i];
                case DIV -> throw new IllegalArgumentException(
                        "int division is promoted to float64");
            };
        }
    }

    private static void scalarInt(int[] a, int scalar, int[] out, Binary op) {
        var species = IntVector.SPECIES_PREFERRED;
        int lanes = species.length();
        IntVector vs = IntVector.broadcast(species, scalar);
        int i = 0;
        for (; i + lanes <= a.length; i += lanes) {
            IntVector va = IntVector.fromArray(species, a, i);
            switch (op) {
                case ADD -> va.add(vs).intoArray(out, i);
                case SUB -> va.sub(vs).intoArray(out, i);
                case MUL -> va.mul(vs).intoArray(out, i);
                case DIV -> throw new IllegalArgumentException(
                        "int division is promoted to float64");
            }
        }
        for (; i < a.length; i++) {
            out[i] = switch (op) {
                case ADD -> a[i] + scalar;
                case SUB -> a[i] - scalar;
                case MUL -> a[i] * scalar;
                case DIV -> throw new IllegalArgumentException(
                        "int division is promoted to float64");
            };
        }
    }

    // ---- int64 kernels --------------------------------------------------

    private static void binaryLong(long[] a, long[] b, long[] out, Binary op) {
        var species = LongVector.SPECIES_PREFERRED;
        int lanes = species.length();
        int i = 0;
        for (; i + lanes <= a.length; i += lanes) {
            LongVector va = LongVector.fromArray(species, a, i);
            LongVector vb = LongVector.fromArray(species, b, i);
            switch (op) {
                case ADD -> va.add(vb).intoArray(out, i);
                case SUB -> va.sub(vb).intoArray(out, i);
                case MUL -> va.mul(vb).intoArray(out, i);
                case DIV -> throw new IllegalArgumentException(
                        "integer division is promoted to float64");
            }
        }
        for (; i < a.length; i++) {
            out[i] = switch (op) {
                case ADD -> a[i] + b[i];
                case SUB -> a[i] - b[i];
                case MUL -> a[i] * b[i];
                case DIV -> throw new IllegalArgumentException(
                        "integer division is promoted to float64");
            };
        }
    }

    private static void scalarLong(long[] a, long scalar, long[] out, Binary op) {
        var species = LongVector.SPECIES_PREFERRED;
        int lanes = species.length();
        LongVector vs = LongVector.broadcast(species, scalar);
        int i = 0;
        for (; i + lanes <= a.length; i += lanes) {
            LongVector va = LongVector.fromArray(species, a, i);
            switch (op) {
                case ADD -> va.add(vs).intoArray(out, i);
                case SUB -> va.sub(vs).intoArray(out, i);
                case MUL -> va.mul(vs).intoArray(out, i);
                case DIV -> throw new IllegalArgumentException(
                        "integer division is promoted to float64");
            }
        }
        for (; i < a.length; i++) {
            out[i] = switch (op) {
                case ADD -> a[i] + scalar;
                case SUB -> a[i] - scalar;
                case MUL -> a[i] * scalar;
                case DIV -> throw new IllegalArgumentException(
                        "integer division is promoted to float64");
            };
        }
    }

    // ---- float32 kernels ------------------------------------------------

    private static void binaryFloat(float[] a, float[] b, float[] out, Binary op) {
        var species = FloatVector.SPECIES_PREFERRED;
        int lanes = species.length();
        int i = 0;
        for (; i + lanes <= a.length; i += lanes) {
            FloatVector va = FloatVector.fromArray(species, a, i);
            FloatVector vb = FloatVector.fromArray(species, b, i);
            switch (op) {
                case ADD -> va.add(vb).intoArray(out, i);
                case SUB -> va.sub(vb).intoArray(out, i);
                case MUL -> va.mul(vb).intoArray(out, i);
                case DIV -> va.div(vb).intoArray(out, i);
            }
        }
        for (; i < a.length; i++) {
            out[i] = switch (op) {
                case ADD -> a[i] + b[i];
                case SUB -> a[i] - b[i];
                case MUL -> a[i] * b[i];
                case DIV -> a[i] / b[i];
            };
        }
    }

    private static void scalarFloat(float[] a, float scalar, float[] out, Binary op) {
        var species = FloatVector.SPECIES_PREFERRED;
        int lanes = species.length();
        FloatVector vs = FloatVector.broadcast(species, scalar);
        int i = 0;
        for (; i + lanes <= a.length; i += lanes) {
            FloatVector va = FloatVector.fromArray(species, a, i);
            switch (op) {
                case ADD -> va.add(vs).intoArray(out, i);
                case SUB -> va.sub(vs).intoArray(out, i);
                case MUL -> va.mul(vs).intoArray(out, i);
                case DIV -> va.div(vs).intoArray(out, i);
            }
        }
        for (; i < a.length; i++) {
            out[i] = switch (op) {
                case ADD -> a[i] + scalar;
                case SUB -> a[i] - scalar;
                case MUL -> a[i] * scalar;
                case DIV -> a[i] / scalar;
            };
        }
    }

    // ---- float64 kernels ------------------------------------------------

    private static void binaryDouble(double[] a, double[] b, double[] out, Binary op) {
        var species = DoubleVector.SPECIES_PREFERRED;
        int lanes = species.length();
        int i = 0;
        for (; i + lanes <= a.length; i += lanes) {
            DoubleVector va = DoubleVector.fromArray(species, a, i);
            DoubleVector vb = DoubleVector.fromArray(species, b, i);
            switch (op) {
                case ADD -> va.add(vb).intoArray(out, i);
                case SUB -> va.sub(vb).intoArray(out, i);
                case MUL -> va.mul(vb).intoArray(out, i);
                case DIV -> va.div(vb).intoArray(out, i);
            }
        }
        for (; i < a.length; i++) {
            out[i] = switch (op) {
                case ADD -> a[i] + b[i];
                case SUB -> a[i] - b[i];
                case MUL -> a[i] * b[i];
                case DIV -> a[i] / b[i];
            };
        }
    }

    private static void scalarDouble(double[] a, double scalar, double[] out, Binary op) {
        var species = DoubleVector.SPECIES_PREFERRED;
        int lanes = species.length();
        DoubleVector vs = DoubleVector.broadcast(species, scalar);
        int i = 0;
        for (; i + lanes <= a.length; i += lanes) {
            DoubleVector va = DoubleVector.fromArray(species, a, i);
            switch (op) {
                case ADD -> va.add(vs).intoArray(out, i);
                case SUB -> va.sub(vs).intoArray(out, i);
                case MUL -> va.mul(vs).intoArray(out, i);
                case DIV -> va.div(vs).intoArray(out, i);
            }
        }
        for (; i < a.length; i++) {
            out[i] = switch (op) {
                case ADD -> a[i] + scalar;
                case SUB -> a[i] - scalar;
                case MUL -> a[i] * scalar;
                case DIV -> a[i] / scalar;
            };
        }
    }

    // ---- comparisons ----------------------------------------------------

    private static void compareInt(int[] a, int[] b, long[] out, Cmp cmp) {
        var species = IntVector.SPECIES_PREFERRED;
        int lanes = species.length();
        int i = 0;
        for (; i + lanes <= a.length; i += lanes) {
            IntVector va = IntVector.fromArray(species, a, i);
            IntVector vb = IntVector.fromArray(species, b, i);
            writeMask(va.compare(comparison(cmp), vb), out, i, lanes);
        }
        for (; i < a.length; i++) out[i] = applyInt(a[i], b[i], cmp);
    }

    private static void compareIntScalar(int[] a, int scalar, long[] out, Cmp cmp) {
        var species = IntVector.SPECIES_PREFERRED;
        int lanes = species.length();
        int i = 0;
        for (; i + lanes <= a.length; i += lanes) {
            IntVector va = IntVector.fromArray(species, a, i);
            writeMask(va.compare(comparison(cmp), scalar), out, i, lanes);
        }
        for (; i < a.length; i++) out[i] = applyInt(a[i], scalar, cmp);
    }

    private static void compareLong(long[] a, long[] b, long[] out, Cmp cmp) {
        var species = LongVector.SPECIES_PREFERRED;
        int lanes = species.length();
        int i = 0;
        for (; i + lanes <= a.length; i += lanes) {
            LongVector va = LongVector.fromArray(species, a, i);
            LongVector vb = LongVector.fromArray(species, b, i);
            writeMask(va.compare(comparison(cmp), vb), out, i, lanes);
        }
        for (; i < a.length; i++) out[i] = applyLong(a[i], b[i], cmp);
    }

    private static void compareLongScalar(long[] a, long scalar, long[] out, Cmp cmp) {
        var species = LongVector.SPECIES_PREFERRED;
        int lanes = species.length();
        int i = 0;
        for (; i + lanes <= a.length; i += lanes) {
            LongVector va = LongVector.fromArray(species, a, i);
            writeMask(va.compare(comparison(cmp), scalar), out, i, lanes);
        }
        for (; i < a.length; i++) out[i] = applyLong(a[i], scalar, cmp);
    }

    private static void compareFloat(float[] a, float[] b, long[] out, Cmp cmp) {
        var species = FloatVector.SPECIES_PREFERRED;
        int lanes = species.length();
        int i = 0;
        for (; i + lanes <= a.length; i += lanes) {
            FloatVector va = FloatVector.fromArray(species, a, i);
            FloatVector vb = FloatVector.fromArray(species, b, i);
            writeMask(va.compare(comparison(cmp), vb), out, i, lanes);
        }
        for (; i < a.length; i++) out[i] = applyDouble(a[i], b[i], cmp);
    }

    private static void compareFloatScalar(float[] a, float scalar, long[] out, Cmp cmp) {
        var species = FloatVector.SPECIES_PREFERRED;
        int lanes = species.length();
        int i = 0;
        for (; i + lanes <= a.length; i += lanes) {
            FloatVector va = FloatVector.fromArray(species, a, i);
            writeMask(va.compare(comparison(cmp), scalar), out, i, lanes);
        }
        for (; i < a.length; i++) out[i] = applyDouble(a[i], scalar, cmp);
    }

    private static void compareDouble(double[] a, double[] b, long[] out, Cmp cmp) {
        var species = DoubleVector.SPECIES_PREFERRED;
        int lanes = species.length();
        int i = 0;
        for (; i + lanes <= a.length; i += lanes) {
            DoubleVector va = DoubleVector.fromArray(species, a, i);
            DoubleVector vb = DoubleVector.fromArray(species, b, i);
            writeMask(va.compare(comparison(cmp), vb), out, i, lanes);
        }
        for (; i < a.length; i++) out[i] = applyDouble(a[i], b[i], cmp);
    }

    private static void compareDoubleScalar(double[] a, double scalar, long[] out, Cmp cmp) {
        var species = DoubleVector.SPECIES_PREFERRED;
        int lanes = species.length();
        int i = 0;
        for (; i + lanes <= a.length; i += lanes) {
            DoubleVector va = DoubleVector.fromArray(species, a, i);
            writeMask(va.compare(comparison(cmp), scalar), out, i, lanes);
        }
        for (; i < a.length; i++) out[i] = applyDouble(a[i], scalar, cmp);
    }

    private static VectorOperators.Comparison comparison(Cmp cmp) {
        return switch (cmp) {
            case GT -> VectorOperators.GT;
            case GE -> VectorOperators.GE;
            case LT -> VectorOperators.LT;
            case LE -> VectorOperators.LE;
            case EQ -> VectorOperators.EQ;
            case NEQ -> VectorOperators.NE;
        };
    }

    private static void writeMask(VectorMask<?> mask, long[] out, int offset, int lanes) {
        if (lanes <= Long.SIZE) {
            long bits = mask.toLong();
            for (int i = 0; i < lanes; i++) {
                out[offset + i] = (bits >>> i) & 1L;
            }
            return;
        }
        for (int i = 0; i < lanes; i++) {
            out[offset + i] = mask.laneIsSet(i) ? 1L : 0L;
        }
    }

    private static long applyInt(int a, int b, Cmp cmp) {
        return switch (cmp) {
            case GT -> a > b ? 1 : 0;
            case GE -> a >= b ? 1 : 0;
            case LT -> a < b ? 1 : 0;
            case LE -> a <= b ? 1 : 0;
            case EQ -> a == b ? 1 : 0;
            case NEQ -> a != b ? 1 : 0;
        };
    }

    private static long applyLong(long a, long b, Cmp cmp) {
        return switch (cmp) {
            case GT -> a > b ? 1 : 0;
            case GE -> a >= b ? 1 : 0;
            case LT -> a < b ? 1 : 0;
            case LE -> a <= b ? 1 : 0;
            case EQ -> a == b ? 1 : 0;
            case NEQ -> a != b ? 1 : 0;
        };
    }

    private static long applyDouble(double a, double b, Cmp cmp) {
        return switch (cmp) {
            case GT -> a > b ? 1 : 0;
            case GE -> a >= b ? 1 : 0;
            case LT -> a < b ? 1 : 0;
            case LE -> a <= b ? 1 : 0;
            case EQ -> a == b ? 1 : 0;
            case NEQ -> a != b ? 1 : 0;
        };
    }

    // ---- unary ----------------------------------------------------------

    private static void unaryInt(int[] a, int[] out, Unary op) {
        var species = IntVector.SPECIES_PREFERRED;
        int lanes = species.length();
        int i = 0;
        for (; i + lanes <= a.length; i += lanes) {
            IntVector v = IntVector.fromArray(species, a, i);
            IntVector r = switch (op) {
                case NEG -> v.neg();
                case ABS -> v.abs();
                case SQUARE -> v.mul(v);
            };
            r.intoArray(out, i);
        }
        for (; i < a.length; i++) {
            out[i] = switch (op) {
                case NEG -> -a[i];
                case ABS -> Math.abs(a[i]);
                case SQUARE -> a[i] * a[i];
            };
        }
    }

    private static void unaryLong(long[] a, long[] out, Unary op) {
        var species = LongVector.SPECIES_PREFERRED;
        int lanes = species.length();
        int i = 0;
        for (; i + lanes <= a.length; i += lanes) {
            LongVector v = LongVector.fromArray(species, a, i);
            LongVector r = switch (op) {
                case NEG -> v.neg();
                case ABS -> v.abs();
                case SQUARE -> v.mul(v);
            };
            r.intoArray(out, i);
        }
        for (; i < a.length; i++) {
            out[i] = switch (op) {
                case NEG -> -a[i];
                case ABS -> Math.abs(a[i]);
                case SQUARE -> a[i] * a[i];
            };
        }
    }

    private static void unaryFloat(float[] a, float[] out, Unary op) {
        var species = FloatVector.SPECIES_PREFERRED;
        int lanes = species.length();
        int i = 0;
        for (; i + lanes <= a.length; i += lanes) {
            FloatVector v = FloatVector.fromArray(species, a, i);
            FloatVector r = switch (op) {
                case NEG -> v.neg();
                case ABS -> v.abs();
                case SQUARE -> v.mul(v);
            };
            r.intoArray(out, i);
        }
        for (; i < a.length; i++) {
            out[i] = switch (op) {
                case NEG -> -a[i];
                case ABS -> Math.abs(a[i]);
                case SQUARE -> a[i] * a[i];
            };
        }
    }

    private static void unaryDouble(double[] a, double[] out, Unary op) {
        var species = DoubleVector.SPECIES_PREFERRED;
        int lanes = species.length();
        int i = 0;
        for (; i + lanes <= a.length; i += lanes) {
            DoubleVector v = DoubleVector.fromArray(species, a, i);
            DoubleVector r = switch (op) {
                case NEG -> v.neg();
                case ABS -> v.abs();
                case SQUARE -> v.mul(v);
            };
            r.intoArray(out, i);
        }
        for (; i < a.length; i++) {
            out[i] = switch (op) {
                case NEG -> -a[i];
                case ABS -> Math.abs(a[i]);
                case SQUARE -> a[i] * a[i];
            };
        }
    }

    // ---- reductions -----------------------------------------------------

    private static double sumInt(int[] a) {
        var species = IntVector.SPECIES_PREFERRED;
        int lanes = species.length();
        int i = 0;
        long sum = 0;
        for (; i + lanes <= a.length; i += lanes) {
            sum += IntVector.fromArray(species, a, i)
                    .reduceLanes(VectorOperators.ADD);
        }
        for (; i < a.length; i++) sum += a[i];
        return sum;
    }

    private static double sumLong(long[] a) {
        var species = LongVector.SPECIES_PREFERRED;
        int lanes = species.length();
        int i = 0;
        long sum = 0;
        for (; i + lanes <= a.length; i += lanes) {
            sum += LongVector.fromArray(species, a, i)
                    .reduceLanes(VectorOperators.ADD);
        }
        for (; i < a.length; i++) sum += a[i];
        return sum;
    }

    private static double sumFloat(float[] a) {
        var species = FloatVector.SPECIES_PREFERRED;
        int lanes = species.length();
        int i = 0;
        double sum = 0.0;
        for (; i + lanes <= a.length; i += lanes) {
            sum += FloatVector.fromArray(species, a, i)
                    .reduceLanes(VectorOperators.ADD);
        }
        for (; i < a.length; i++) sum += a[i];
        return sum;
    }

    private static double sumDouble(double[] a) {
        var species = DoubleVector.SPECIES_PREFERRED;
        int lanes = species.length();
        int i = 0;
        double sum = 0.0;
        for (; i + lanes <= a.length; i += lanes) {
            sum += DoubleVector.fromArray(species, a, i)
                    .reduceLanes(VectorOperators.ADD);
        }
        for (; i < a.length; i++) sum += a[i];
        return sum;
    }

    private static int minMaxInt(int[] a, boolean max) {
        var species = IntVector.SPECIES_PREFERRED;
        int lanes = species.length();
        int acc = a[0];
        int i = 1;
        for (; i + lanes <= a.length; i += lanes) {
            int chunk = IntVector.fromArray(species, a, i)
                    .reduceLanes(max ? VectorOperators.MAX : VectorOperators.MIN);
            acc = max ? Math.max(acc, chunk) : Math.min(acc, chunk);
        }
        for (; i < a.length; i++) acc = max ? Math.max(acc, a[i]) : Math.min(acc, a[i]);
        return acc;
    }

    private static long minMaxLong(long[] a, boolean max) {
        var species = LongVector.SPECIES_PREFERRED;
        int lanes = species.length();
        long acc = a[0];
        int i = 1;
        for (; i + lanes <= a.length; i += lanes) {
            long chunk = LongVector.fromArray(species, a, i)
                    .reduceLanes(max ? VectorOperators.MAX : VectorOperators.MIN);
            acc = max ? Math.max(acc, chunk) : Math.min(acc, chunk);
        }
        for (; i < a.length; i++) acc = max ? Math.max(acc, a[i]) : Math.min(acc, a[i]);
        return acc;
    }

    private static float minMaxFloat(float[] a, boolean max) {
        var species = FloatVector.SPECIES_PREFERRED;
        int lanes = species.length();
        float acc = a[0];
        int i = 1;
        for (; i + lanes <= a.length; i += lanes) {
            float chunk = FloatVector.fromArray(species, a, i)
                    .reduceLanes(max ? VectorOperators.MAX : VectorOperators.MIN);
            acc = max ? Math.max(acc, chunk) : Math.min(acc, chunk);
        }
        for (; i < a.length; i++) acc = max ? Math.max(acc, a[i]) : Math.min(acc, a[i]);
        return acc;
    }

    private static double minMaxDouble(double[] a, boolean max) {
        var species = DoubleVector.SPECIES_PREFERRED;
        int lanes = species.length();
        double acc = a[0];
        int i = 1;
        for (; i + lanes <= a.length; i += lanes) {
            double chunk = DoubleVector.fromArray(species, a, i)
                    .reduceLanes(max ? VectorOperators.MAX : VectorOperators.MIN);
            acc = max ? Math.max(acc, chunk) : Math.min(acc, chunk);
        }
        for (; i < a.length; i++) acc = max ? Math.max(acc, a[i]) : Math.min(acc, a[i]);
        return acc;
    }
}
