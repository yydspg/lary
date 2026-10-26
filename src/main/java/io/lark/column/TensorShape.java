// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column;

import java.util.Arrays;
import java.util.Objects;

/**
 * Row-major shape of a {@link Tensor}, up to three dimensions.
 *
 * <p>The storage is still one flat primitive array; the shape describes how
 * those elements are interpreted, e.g. {@code [batch, candidate, feature]}.
 * Element-wise arithmetic requires equal shapes, while scalar operations keep
 * the input shape.
 */
public final class TensorShape {

    public static final int MAX_RANK = 3;

    private final int[] dimensions;
    private final int size;

    private TensorShape(int[] dimensions) {
        Objects.requireNonNull(dimensions, "dimensions");
        if (dimensions.length == 0 || dimensions.length > MAX_RANK) {
            throw new IllegalArgumentException(
                    "Tensor shape rank must be 1.." + MAX_RANK
                            + ", got " + dimensions.length);
        }
        long product = 1;
        for (int dimension : dimensions) {
            if (dimension < 0) {
                throw new IllegalArgumentException(
                        "Tensor shape dimensions must be non-negative: "
                                + Arrays.toString(dimensions));
            }
            product *= dimension;
            if (product > Integer.MAX_VALUE) {
                throw new IllegalArgumentException(
                        "Tensor shape is too large: " + Arrays.toString(dimensions));
            }
        }
        this.dimensions = dimensions.clone();
        this.size = (int) product;
    }

    public static TensorShape of(int... dimensions) {
        return new TensorShape(dimensions);
    }

    public static TensorShape vector(int size) {
        return of(size);
    }

    /**
     * Parse {@code "2,3,4"}, {@code "2x3x4"} or whitespace-separated
     * dimensions. Used by the declarative {@code zeros}/{@code const} ops.
     */
    public static TensorShape parse(String value) {
        Objects.requireNonNull(value, "value");
        String text = value.trim();
        if (text.isEmpty()) {
            throw new IllegalArgumentException("Tensor shape must not be empty");
        }
        String[] parts = text.split("[xX,\\s]+");
        int[] dimensions = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            try {
                dimensions[i] = Integer.parseInt(parts[i]);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(
                        "Invalid tensor shape: " + value, e);
            }
        }
        return of(dimensions);
    }

    public int rank() {
        return dimensions.length;
    }

    public int dim(int axis) {
        if (axis < 0 || axis >= dimensions.length) {
            throw new IndexOutOfBoundsException(
                    "Shape axis " + axis + " out of range [0, "
                            + dimensions.length + ")");
        }
        return dimensions[axis];
    }

    /** Defensive copy of the dimensions. */
    public int[] dimensions() {
        return dimensions.clone();
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean isVector() {
        return dimensions.length == 1;
    }

    /** Row-major stride of one axis. */
    public int stride(int axis) {
        if (axis < 0 || axis >= dimensions.length) {
            throw new IndexOutOfBoundsException(
                    "Shape axis " + axis + " out of range [0, "
                            + dimensions.length + ")");
        }
        int stride = 1;
        for (int i = axis + 1; i < dimensions.length; i++) {
            stride *= dimensions[i];
        }
        return stride;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof TensorShape shape
                && Arrays.equals(dimensions, shape.dimensions);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(dimensions);
    }

    @Override
    public String toString() {
        if (dimensions.length == 1) {
            return Integer.toString(dimensions[0]);
        }
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < dimensions.length; i++) {
            if (i > 0) {
                text.append('x');
            }
            text.append(dimensions[i]);
        }
        return text.toString();
    }
}
