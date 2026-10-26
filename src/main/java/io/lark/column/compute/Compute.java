// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.compute;

import io.lark.column.DType;
import io.lark.column.Tensor;
import io.lark.column.TensorShape;

/**
 * Public compute-layer facade.
 *
 * <p>Element-wise arithmetic, scalar broadcasting, comparisons and reductions
 * are implemented with the JDK incubator Vector API; special ops
 * (select/filter/quantization) use direct buffer loops.
 */
public final class Compute {

    private Compute() {
    }

    // ---- dtype promotion ------------------------------------------------

    public static DType promote(DType a, DType b) {
        if (a == DType.Q8_0) a = DType.FLOAT64;
        if (b == DType.Q8_0) b = DType.FLOAT64;
        if (a == b) return a;
        boolean af = a.isFloat();
        boolean bf = b.isFloat();
        if (af && bf) return DType.FLOAT64;
        if (af || bf) {
            DType f = af ? a : b;
            DType i = af ? b : a;
            return i == DType.INT64 ? DType.FLOAT64 : f;
        }
        return DType.INT64;
    }

    public static DType promoteScalar(DType a) {
        return switch (a) {
            case INT64 -> DType.INT64;
            case INT32 -> DType.INT32;
            case Q8_0 -> DType.FLOAT64;
            default -> a;
        };
    }

    public static Tensor cast(Tensor a, DType to) {
        return a.castTo(to);
    }

    // ---- arithmetic -----------------------------------------------------

    public static Tensor add(Tensor a, Tensor b) {
        return VectorOps.binary(a, b, promote(a.dtype(), b.dtype()), VectorOps.Binary.ADD);
    }

    public static Tensor sub(Tensor a, Tensor b) {
        return VectorOps.binary(a, b, promote(a.dtype(), b.dtype()), VectorOps.Binary.SUB);
    }

    public static Tensor mul(Tensor a, Tensor b) {
        return VectorOps.binary(a, b, promote(a.dtype(), b.dtype()), VectorOps.Binary.MUL);
    }

    /** Division always produces float64, matching both compute and exec layers. */
    public static Tensor div(Tensor a, Tensor b) {
        return VectorOps.binary(a, b, DType.FLOAT64, VectorOps.Binary.DIV);
    }

    public static Tensor addScalar(Tensor a, double value) {
        return VectorOps.scalar(a, value, promoteScalar(a.dtype()), VectorOps.Binary.ADD);
    }

    public static Tensor subScalar(Tensor a, double value) {
        return VectorOps.scalar(a, value, promoteScalar(a.dtype()), VectorOps.Binary.SUB);
    }

    public static Tensor mulScalar(Tensor a, double value) {
        return VectorOps.scalar(a, value, promoteScalar(a.dtype()), VectorOps.Binary.MUL);
    }

    public static Tensor divScalar(Tensor a, double value) {
        return VectorOps.scalar(a, value, DType.FLOAT64, VectorOps.Binary.DIV);
    }

    // ---- comparisons ----------------------------------------------------

    public static Tensor gt(Tensor a, Tensor b) {
        return VectorOps.compare(a, b, VectorOps.Cmp.GT);
    }

    public static Tensor ge(Tensor a, Tensor b) {
        return VectorOps.compare(a, b, VectorOps.Cmp.GE);
    }

    public static Tensor lt(Tensor a, Tensor b) {
        return VectorOps.compare(a, b, VectorOps.Cmp.LT);
    }

    public static Tensor le(Tensor a, Tensor b) {
        return VectorOps.compare(a, b, VectorOps.Cmp.LE);
    }

    public static Tensor eq(Tensor a, Tensor b) {
        return VectorOps.compare(a, b, VectorOps.Cmp.EQ);
    }

    public static Tensor neq(Tensor a, Tensor b) {
        return VectorOps.compare(a, b, VectorOps.Cmp.NEQ);
    }

    public static Tensor gtScalar(Tensor a, double value) {
        return VectorOps.compareScalar(a, value, VectorOps.Cmp.GT);
    }

    public static Tensor geScalar(Tensor a, double value) {
        return VectorOps.compareScalar(a, value, VectorOps.Cmp.GE);
    }

    public static Tensor ltScalar(Tensor a, double value) {
        return VectorOps.compareScalar(a, value, VectorOps.Cmp.LT);
    }

    public static Tensor leScalar(Tensor a, double value) {
        return VectorOps.compareScalar(a, value, VectorOps.Cmp.LE);
    }

    public static Tensor eqScalar(Tensor a, double value) {
        return VectorOps.compareScalar(a, value, VectorOps.Cmp.EQ);
    }

    public static Tensor neqScalar(Tensor a, double value) {
        return VectorOps.compareScalar(a, value, VectorOps.Cmp.NEQ);
    }

    // ---- unary ----------------------------------------------------------

    public static Tensor neg(Tensor a) {
        return VectorOps.unary(a, VectorOps.Unary.NEG);
    }

    public static Tensor abs(Tensor a) {
        return VectorOps.unary(a, VectorOps.Unary.ABS);
    }

    public static Tensor square(Tensor a) {
        return VectorOps.unary(a, VectorOps.Unary.SQUARE);
    }

    public static Tensor sqrt(Tensor a) {
        return VectorOps.sqrt(a);
    }

    public static Tensor exp(Tensor a) {
        return VectorOps.transcendental(a, true);
    }

    public static Tensor log(Tensor a) {
        return VectorOps.transcendental(a, false);
    }

    // ---- reductions -----------------------------------------------------

    public static double sum(Tensor a) {
        return VectorOps.sum(a);
    }

    public static double mean(Tensor a) {
        return a.size() == 0 ? 0.0 : sum(a) / a.size();
    }

    public static long count(Tensor a) {
        return a.size();
    }

    public static double max(Tensor a) {
        return scalarValue(VectorOps.minMax(a, true));
    }

    public static double min(Tensor a) {
        return scalarValue(VectorOps.minMax(a, false));
    }

    public static double stddev(Tensor a) {
        return VectorOps.stddev(a);
    }

    public static Tensor sumTensor(Tensor a) {
        return Tensor.fromDoubles(sum(a));
    }

    public static Tensor meanTensor(Tensor a) {
        return Tensor.fromDoubles(mean(a));
    }

    public static Tensor countTensor(Tensor a) {
        return Tensor.fromLongs(count(a));
    }

    public static Tensor maxTensor(Tensor a) {
        return VectorOps.minMax(a, true);
    }

    public static Tensor minTensor(Tensor a) {
        return VectorOps.minMax(a, false);
    }

    public static Tensor stddevTensor(Tensor a) {
        return Tensor.fromDoubles(stddev(a));
    }

    // ---- special --------------------------------------------------------

    public static Tensor select(Tensor mask, Tensor a, Tensor b) {
        if (mask.dtype() != DType.INT64) {
            throw new IllegalArgumentException("select: mask must be int64");
        }
        if (!mask.shape().equals(a.shape())
                || !b.shape().equals(a.shape())) {
            throw new IllegalArgumentException("select: shape mismatch");
        }
        DType out = promote(a.dtype(), b.dtype());
        Tensor left = a.dtype() == out ? a : a.castTo(out);
        Tensor right = b.dtype() == out ? b : b.castTo(out);
        Tensor result = Tensor.zeros(a.shape(), out);
        long[] m = mask.longData();

        switch (out) {
            case INT32 -> {
                int[] pa = left.intData();
                int[] pb = right.intData();
                int[] pr = result.intData();
                for (int i = 0; i < m.length; i++) pr[i] = m[i] != 0 ? pa[i] : pb[i];
            }
            case INT64 -> {
                long[] pa = left.longData();
                long[] pb = right.longData();
                long[] pr = result.longData();
                for (int i = 0; i < m.length; i++) pr[i] = m[i] != 0 ? pa[i] : pb[i];
            }
            case FLOAT32 -> {
                float[] pa = left.floatData();
                float[] pb = right.floatData();
                float[] pr = result.floatData();
                for (int i = 0; i < m.length; i++) pr[i] = m[i] != 0 ? pa[i] : pb[i];
            }
            case FLOAT64 -> {
                double[] pa = left.doubleData();
                double[] pb = right.doubleData();
                double[] pr = result.doubleData();
                for (int i = 0; i < m.length; i++) pr[i] = m[i] != 0 ? pa[i] : pb[i];
            }
            case Q8_0 -> throw new IllegalArgumentException("select does not support q8_0");
        }
        return result;
    }

    public static Tensor filter(Tensor data, Tensor mask) {
        if (mask.dtype() != DType.INT64) {
            throw new IllegalArgumentException("filter: mask must be int64");
        }
        if (!mask.shape().equals(data.shape())) {
            throw new IllegalArgumentException("filter: mask shape mismatch");
        }
        if (data.dtype() == DType.Q8_0) {
            throw new IllegalArgumentException("filter: cannot filter a quantized tensor");
        }
        long[] m = mask.longData();
        int keep = 0;
        for (long value : m) {
            if (value != 0) keep++;
        }

        Tensor result = Tensor.zeros(keep, data.dtype());
        int j = 0;
        switch (data.dtype()) {
            case INT32 -> {
                int[] src = data.intData();
                int[] dst = result.intData();
                for (int i = 0; i < m.length; i++) if (m[i] != 0) dst[j++] = src[i];
            }
            case INT64 -> {
                long[] src = data.longData();
                long[] dst = result.longData();
                for (int i = 0; i < m.length; i++) if (m[i] != 0) dst[j++] = src[i];
            }
            case FLOAT32 -> {
                float[] src = data.floatData();
                float[] dst = result.floatData();
                for (int i = 0; i < m.length; i++) if (m[i] != 0) dst[j++] = src[i];
            }
            case FLOAT64 -> {
                double[] src = data.doubleData();
                double[] dst = result.doubleData();
                for (int i = 0; i < m.length; i++) if (m[i] != 0) dst[j++] = src[i];
            }
            case Q8_0 -> throw new IllegalArgumentException("unreachable");
        }
        return result;
    }

    public static Tensor quantize(Tensor a) {
        return a.quantizeQ8_0();
    }

    public static Tensor dequantize(Tensor a) {
        return a.dequantize();
    }

    public static double dot(Tensor a, Tensor b) {
        return a.dotQ8_0(b);
    }

    public static Tensor identity(Tensor a) {
        return a.clone();
    }

    public static Tensor constant(String value, DType dtype, int size) {
        if (value.indexOf(',') < 0) {
            return Tensor.full(size, Double.parseDouble(value.trim()), dtype);
        }
        // Historical behavior: a comma-separated constant determines its own
        // length; the size hint only applies to a single scalar value.
        return constant(value, dtype,
                TensorShape.vector(value.split(",").length));
    }

    public static Tensor constant(String value, DType dtype, TensorShape shape) {
        if (value.indexOf(',') < 0) {
            return Tensor.full(shape, Double.parseDouble(value.trim()), dtype);
        }
        String[] parts = value.split(",");
        Tensor result = Tensor.zeros(shape, dtype);
        if (parts.length != shape.size()) {
            throw new IllegalArgumentException(
                    "const: expected " + shape.size() + " values for shape "
                            + shape + ", got " + parts.length);
        }
        switch (dtype) {
            case INT32 -> {
                int[] data = result.intData();
                for (int i = 0; i < parts.length; i++) {
                    data[i] = (int) Double.parseDouble(parts[i].trim());
                }
            }
            case INT64 -> {
                long[] data = result.longData();
                for (int i = 0; i < parts.length; i++) {
                    data[i] = (long) Double.parseDouble(parts[i].trim());
                }
            }
            case FLOAT32 -> {
                float[] data = result.floatData();
                for (int i = 0; i < parts.length; i++) {
                    data[i] = (float) Double.parseDouble(parts[i].trim());
                }
            }
            case FLOAT64 -> {
                double[] data = result.doubleData();
                for (int i = 0; i < parts.length; i++) {
                    data[i] = Double.parseDouble(parts[i].trim());
                }
            }
            case Q8_0 -> throw new IllegalArgumentException("const does not support q8_0");
        }
        return result;
    }

    private static double scalarValue(Tensor tensor) {
        return switch (tensor.dtype()) {
            case INT32 -> tensor.getInt(0);
            case INT64 -> tensor.getLong(0);
            case FLOAT32 -> tensor.getFloat(0);
            case FLOAT64 -> tensor.getDouble(0);
            case Q8_0 -> tensor.dequantize().getDouble(0);
        };
    }
}
