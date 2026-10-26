// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.data;

import java.util.Arrays;
import java.util.Objects;

/**
 * Numeric tensor backed by one flat primitive array.
 *
 * <p>Arithmetic lives in {@code io.lark.column.compute} and in the execution
 * layer. Storage is a primitive array matching {@link #dtype()}, while
 * {@link #shape()} gives the row-major interpretation (up to three
 * dimensions). Quantized tensors store {@link BlockQ8_0} blocks and
 * {@link #size()} reports the logical element count.
 */
public final class Tensor {

    private final DType dtype;
    private final Object data;
    private final TensorShape shape;
    private final int size;

    private Tensor(DType dtype, Object data, int size) {
        this(dtype, data, TensorShape.vector(size));
    }

    private Tensor(DType dtype, Object data, TensorShape shape) {
        this.dtype = Objects.requireNonNull(dtype, "dtype");
        this.data = Objects.requireNonNull(data, "data");
        this.shape = Objects.requireNonNull(shape, "shape");
        this.size = shape.size();
    }

    // ---- factories ------------------------------------------------------

    public static Tensor zeros(int size, DType dtype) {
        return zeros(TensorShape.vector(size), dtype);
    }

    public static Tensor zeros(TensorShape shape, DType dtype) {
        Objects.requireNonNull(shape, "shape");
        int size = shape.size();
        return switch (dtype) {
            case INT32 -> new Tensor(dtype, new int[size], shape);
            case INT64 -> new Tensor(dtype, new long[size], shape);
            case FLOAT32 -> new Tensor(dtype, new float[size], shape);
            case FLOAT64 -> new Tensor(dtype, new double[size], shape);
            case Q8_0 -> {
                int blocks = (size + BlockQ8_0.BLOCK_SIZE - 1) / BlockQ8_0.BLOCK_SIZE;
                BlockQ8_0[] q = new BlockQ8_0[blocks];
                for (int i = 0; i < blocks; i++) {
                    q[i] = new BlockQ8_0(0.0f);
                }
                yield new Tensor(dtype, q, shape);
            }
        };
    }

    public static Tensor ones(int size, DType dtype) {
        return ones(TensorShape.vector(size), dtype);
    }

    public static Tensor ones(TensorShape shape, DType dtype) {
        return full(shape, 1.0, dtype);
    }

    public static Tensor full(int size, double value, DType dtype) {
        return full(TensorShape.vector(size), value, dtype);
    }

    public static Tensor full(TensorShape shape, double value, DType dtype) {
        Tensor t = zeros(shape, dtype);
        switch (dtype) {
            case INT32 -> Arrays.fill(t.intData(), (int) value);
            case INT64 -> Arrays.fill(t.longData(), (long) value);
            case FLOAT32 -> Arrays.fill(t.floatData(), (float) value);
            case FLOAT64 -> Arrays.fill(t.doubleData(), value);
            case Q8_0 -> throw new IllegalArgumentException("Tensor.full: unsupported dtype q8_0");
        }
        return t;
    }

    public static Tensor fromInts(int... values) {
        return new Tensor(DType.INT32, values.clone(), values.length);
    }

    public static Tensor fromInts(TensorShape shape, int... values) {
        requireElementCount(shape, values.length);
        return new Tensor(DType.INT32, values.clone(), shape);
    }

    public static Tensor fromLongs(long... values) {
        return new Tensor(DType.INT64, values.clone(), values.length);
    }

    public static Tensor fromLongs(TensorShape shape, long... values) {
        requireElementCount(shape, values.length);
        return new Tensor(DType.INT64, values.clone(), shape);
    }

    /** Adopt an already-allocated buffer (feed/fetch conversion hot path). */
    static Tensor wrapLongs(long[] values) {
        return new Tensor(DType.INT64, values, values.length);
    }

    /** Adopt an already-allocated buffer (feed/fetch conversion hot path). */
    static Tensor wrapDoubles(double[] values) {
        return new Tensor(DType.FLOAT64, values, values.length);
    }

    public static Tensor fromFloats(float... values) {
        return new Tensor(DType.FLOAT32, values.clone(), values.length);
    }

    public static Tensor fromFloats(TensorShape shape, float... values) {
        requireElementCount(shape, values.length);
        return new Tensor(DType.FLOAT32, values.clone(), shape);
    }

    public static Tensor fromDoubles(double... values) {
        return new Tensor(DType.FLOAT64, values.clone(), values.length);
    }

    public static Tensor fromDoubles(TensorShape shape, double... values) {
        requireElementCount(shape, values.length);
        return new Tensor(DType.FLOAT64, values.clone(), shape);
    }

    private static void requireElementCount(TensorShape shape, int count) {
        Objects.requireNonNull(shape, "shape");
        if (shape.size() != count) {
            throw new IllegalArgumentException(
                    "Tensor shape " + shape + " needs " + shape.size()
                            + " elements, got " + count);
        }
    }

    // ---- metadata -------------------------------------------------------

    public DType dtype() {
        return dtype;
    }

    public int size() {
        return size;
    }

    public TensorShape shape() {
        return shape;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int numBlocks() {
        return dtype == DType.Q8_0
                ? (size + BlockQ8_0.BLOCK_SIZE - 1) / BlockQ8_0.BLOCK_SIZE
                : size;
    }

    // ---- typed access ---------------------------------------------------

    public int getInt(int index) {
        checkIndex(index);
        if (dtype != DType.INT32) {
            throw new IllegalStateException("tensor is " + dtype + ", not int32");
        }
        return intData()[index];
    }

    public long getLong(int index) {
        checkIndex(index);
        if (dtype != DType.INT64) {
            throw new IllegalStateException("tensor is " + dtype + ", not int64");
        }
        return longData()[index];
    }

    public float getFloat(int index) {
        checkIndex(index);
        if (dtype != DType.FLOAT32) {
            throw new IllegalStateException("tensor is " + dtype + ", not float32");
        }
        return floatData()[index];
    }

    public double getDouble(int index) {
        checkIndex(index);
        if (dtype == DType.Q8_0) {
            throw new IllegalStateException("use dequantize() for q8_0 tensors");
        }
        return switch (dtype) {
            case INT32 -> intData()[index];
            case INT64 -> longData()[index];
            case FLOAT32 -> floatData()[index];
            case FLOAT64 -> doubleData()[index];
            case Q8_0 -> throw new IllegalStateException("unreachable");
        };
    }

    public void setInt(int index, int value) {
        checkIndex(index);
        if (dtype != DType.INT32) throw new IllegalStateException("not int32");
        intData()[index] = value;
    }

    public void setLong(int index, long value) {
        checkIndex(index);
        if (dtype != DType.INT64) throw new IllegalStateException("not int64");
        longData()[index] = value;
    }

    public void setFloat(int index, float value) {
        checkIndex(index);
        if (dtype != DType.FLOAT32) throw new IllegalStateException("not float32");
        floatData()[index] = value;
    }

    public void setDouble(int index, double value) {
        checkIndex(index);
        if (dtype != DType.FLOAT64) throw new IllegalStateException("not float64");
        doubleData()[index] = value;
    }

    // ---- raw kernel views (mirror C++ data_as<T>; do not retain) ---------

    public int[] intData() {
        require(DType.INT32);
        return (int[]) data;
    }

    public long[] longData() {
        require(DType.INT64);
        return (long[]) data;
    }

    public float[] floatData() {
        require(DType.FLOAT32);
        return (float[]) data;
    }

    public double[] doubleData() {
        require(DType.FLOAT64);
        return (double[]) data;
    }

    public BlockQ8_0[] q8Blocks() {
        require(DType.Q8_0);
        return (BlockQ8_0[]) data;
    }

    // ---- casts / conversion --------------------------------------------

    public Tensor toDouble() {
        return castTo(DType.FLOAT64);
    }

    public Tensor castTo(DType target) {
        if (dtype == target) {
            return clone();
        }
        if (dtype == DType.Q8_0) {
            return dequantize().castTo(target);
        }
        if (target == DType.Q8_0) {
            return quantizeQ8_0();
        }

        Tensor result = zeros(shape, target);
        switch (dtype) {
            case INT32 -> castFromInts(intData(), result);
            case INT64 -> castFromLongs(longData(), result);
            case FLOAT32 -> castFromFloats(floatData(), result);
            case FLOAT64 -> castFromDoubles(doubleData(), result);
            case Q8_0 -> throw new IllegalStateException("unreachable");
        }
        return result;
    }

    private static void castFromInts(int[] src, Tensor out) {
        switch (out.dtype) {
            case INT32 -> System.arraycopy(src, 0, out.intData(), 0, src.length);
            case INT64 -> {
                long[] dst = out.longData();
                for (int i = 0; i < src.length; i++) dst[i] = src[i];
            }
            case FLOAT32 -> {
                float[] dst = out.floatData();
                for (int i = 0; i < src.length; i++) dst[i] = src[i];
            }
            case FLOAT64 -> {
                double[] dst = out.doubleData();
                for (int i = 0; i < src.length; i++) dst[i] = src[i];
            }
            default -> throw new IllegalArgumentException("bad cast target");
        }
    }

    private static void castFromLongs(long[] src, Tensor out) {
        switch (out.dtype) {
            case INT32 -> {
                int[] dst = out.intData();
                for (int i = 0; i < src.length; i++) dst[i] = (int) src[i];
            }
            case INT64 -> System.arraycopy(src, 0, out.longData(), 0, src.length);
            case FLOAT32 -> {
                float[] dst = out.floatData();
                for (int i = 0; i < src.length; i++) dst[i] = (float) src[i];
            }
            case FLOAT64 -> {
                double[] dst = out.doubleData();
                for (int i = 0; i < src.length; i++) dst[i] = src[i];
            }
            default -> throw new IllegalArgumentException("bad cast target");
        }
    }

    private static void castFromFloats(float[] src, Tensor out) {
        switch (out.dtype) {
            case INT32 -> {
                int[] dst = out.intData();
                for (int i = 0; i < src.length; i++) dst[i] = (int) src[i];
            }
            case INT64 -> {
                long[] dst = out.longData();
                for (int i = 0; i < src.length; i++) dst[i] = (long) src[i];
            }
            case FLOAT32 -> System.arraycopy(src, 0, out.floatData(), 0, src.length);
            case FLOAT64 -> {
                double[] dst = out.doubleData();
                for (int i = 0; i < src.length; i++) dst[i] = src[i];
            }
            default -> throw new IllegalArgumentException("bad cast target");
        }
    }

    private static void castFromDoubles(double[] src, Tensor out) {
        switch (out.dtype) {
            case INT32 -> {
                int[] dst = out.intData();
                for (int i = 0; i < src.length; i++) dst[i] = (int) src[i];
            }
            case INT64 -> {
                long[] dst = out.longData();
                for (int i = 0; i < src.length; i++) dst[i] = (long) src[i];
            }
            case FLOAT32 -> {
                float[] dst = out.floatData();
                for (int i = 0; i < src.length; i++) dst[i] = (float) src[i];
            }
            case FLOAT64 -> System.arraycopy(src, 0, out.doubleData(), 0, src.length);
            default -> throw new IllegalArgumentException("bad cast target");
        }
    }

    // ---- quantization ---------------------------------------------------

    public Tensor quantizeQ8_0() {
        if (!dtype.isFloat()) {
            throw new IllegalStateException("quantize_q8_0: input must be a float tensor");
        }
        int blocks = numBlocks();
        BlockQ8_0[] out = new BlockQ8_0[blocks];

        if (dtype == DType.FLOAT32) {
            float[] src = floatData();
            for (int b = 0; b < blocks; b++) {
                int base = b * BlockQ8_0.BLOCK_SIZE;
                int count = Math.min(BlockQ8_0.BLOCK_SIZE, size - base);
                float amax = 0.0f;
                for (int i = 0; i < count; i++) {
                    amax = Math.max(amax, Math.abs(src[base + i]));
                }
                float d = amax / 127.0f;
                BlockQ8_0 block = new BlockQ8_0(d);
                for (int i = 0; i < count; i++) {
                    float q = d == 0.0f ? 0.0f : src[base + i] / d;
                    block.setQ(i, (byte) Math.max(-127.0f, Math.min(127.0f, q)));
                }
                out[b] = block;
            }
        } else {
            double[] src = doubleData();
            for (int b = 0; b < blocks; b++) {
                int base = b * BlockQ8_0.BLOCK_SIZE;
                int count = Math.min(BlockQ8_0.BLOCK_SIZE, size - base);
                double amax = 0.0;
                for (int i = 0; i < count; i++) {
                    amax = Math.max(amax, Math.abs(src[base + i]));
                }
                float d = (float) (amax / 127.0);
                BlockQ8_0 block = new BlockQ8_0(d);
                for (int i = 0; i < count; i++) {
                    float q = d == 0.0f ? 0.0f : (float) src[base + i] / d;
                    block.setQ(i, (byte) Math.max(-127.0f, Math.min(127.0f, q)));
                }
                out[b] = block;
            }
        }

        return new Tensor(DType.Q8_0, out, shape);
    }

    public Tensor dequantize() {
        if (dtype != DType.Q8_0) {
            throw new IllegalStateException("dequantize: input must be a q8_0 tensor");
        }
        Tensor result = zeros(shape, DType.FLOAT64);
        double[] dst = result.doubleData();
        BlockQ8_0[] blocks = q8Blocks();
        for (int b = 0; b < blocks.length; b++) {
            int base = b * BlockQ8_0.BLOCK_SIZE;
            int count = Math.min(BlockQ8_0.BLOCK_SIZE, size - base);
            float d = blocks[b].scale();
            byte[] qs = blocks[b].qs();
            for (int i = 0; i < count; i++) {
                dst[base + i] = qs[i] * (double) d;
            }
        }
        return result;
    }

    public double dotQ8_0(Tensor other) {
        if (dtype != DType.Q8_0 || other.dtype != DType.Q8_0) {
            throw new IllegalArgumentException("dot_q8_0: both operands must be q8_0");
        }
        if (size != other.size) {
            throw new IllegalArgumentException("dot_q8_0: length mismatch");
        }
        BlockQ8_0[] a = q8Blocks();
        BlockQ8_0[] b = other.q8Blocks();
        double acc = 0.0;
        for (int blk = 0; blk < a.length; blk++) {
            int count = Math.min(BlockQ8_0.BLOCK_SIZE,
                    size - blk * BlockQ8_0.BLOCK_SIZE);
            int sum = 0;
            byte[] qa = a[blk].qs();
            byte[] qb = b[blk].qs();
            for (int i = 0; i < count; i++) {
                sum += qa[i] * qb[i];
            }
            acc += (double) a[blk].scale() * (double) b[blk].scale() * sum;
        }
        return acc;
    }

    // ---- utility --------------------------------------------------------

    @Override
    public Tensor clone() {
        Object copy = switch (dtype) {
            case INT32 -> intData().clone();
            case INT64 -> longData().clone();
            case FLOAT32 -> floatData().clone();
            case FLOAT64 -> doubleData().clone();
            case Q8_0 -> {
                BlockQ8_0[] src = q8Blocks();
                BlockQ8_0[] dst = new BlockQ8_0[src.length];
                for (int i = 0; i < src.length; i++) {
                    dst[i] = new BlockQ8_0(src[i].scale(), src[i].qs().clone());
                }
                yield dst;
            }
        };
        return new Tensor(dtype, copy, shape);
    }

    private void require(DType expected) {
        if (dtype != expected) {
            throw new IllegalStateException(
                    "tensor is " + dtype + ", not " + expected);
        }
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(
                    "Tensor index " + index + " out of range [0, " + size + ")");
        }
    }

    @Override
    public String toString() {
        return "Tensor{" + dtype + ", shape=" + shape
                + ", size=" + size + '}';
    }
}
