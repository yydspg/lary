// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column;

import java.util.Locale;

/** Numeric element type, including ggml-style Q8_0 block quantization. */
public enum DType {
    INT32(4),
    INT64(8),
    FLOAT32(4),
    FLOAT64(8),
    Q8_0(0);

    private final int scalarBytes;

    DType(int scalarBytes) {
        this.scalarBytes = scalarBytes;
    }

    public boolean isFloat() {
        return this == FLOAT32 || this == FLOAT64;
    }

    public boolean isInt() {
        return this == INT32 || this == INT64;
    }

    public boolean isScalar() {
        return isFloat() || isInt();
    }

    public boolean isQuantized() {
        return this == Q8_0;
    }

    public int scalarBytes() {
        return scalarBytes;
    }

    public String displayName() {
        return name().toLowerCase(Locale.ROOT);
    }

    public static DType parse(String value) {
        return switch (value) {
            case "int32" -> INT32;
            case "int64" -> INT64;
            case "float32", "float" -> FLOAT32;
            case "float64", "double" -> FLOAT64;
            case "q8_0", "q8" -> Q8_0;
            default -> throw new IllegalArgumentException("unknown dtype: " + value);
        };
    }

    /** Bytes needed to store {@code count} logical elements (Q8 rounds up to blocks). */
    public static int bytesFor(int count, DType dtype) {
        if (dtype == Q8_0) {
            return ((count + BlockQ8_0.BLOCK_SIZE - 1) / BlockQ8_0.BLOCK_SIZE)
                    * BlockQ8_0.BLOCK_BYTES;
        }
        return count * dtype.scalarBytes;
    }

    @Override
    public String toString() {
        return displayName();
    }
}
