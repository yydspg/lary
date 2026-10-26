// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.data;

/**
 * ggml-style Q8_0 block: 32 int8 values plus one float scale.
 *
 * <p>Layout mirrors the C++ {@code BlockQ8_0} (36 bytes: float + 32 × int8).
 */
public final class BlockQ8_0 {

    public static final int BLOCK_SIZE = 32;
    public static final int BLOCK_BYTES = 36;

    private final float scale;
    private final byte[] qs;

    public BlockQ8_0(float scale) {
        this(scale, new byte[BLOCK_SIZE]);
    }

    public BlockQ8_0(float scale, byte[] qs) {
        if (qs.length != BLOCK_SIZE) {
            throw new IllegalArgumentException("Q8_0 block needs " + BLOCK_SIZE + " values");
        }
        this.scale = scale;
        this.qs = qs;
    }

    public float scale() {
        return scale;
    }

    /** Internal kernel view; do not retain. */
    public byte[] qs() {
        return qs;
    }

    public byte q(int index) {
        return qs[index];
    }

    public void setQ(int index, byte value) {
        qs[index] = value;
    }
}
