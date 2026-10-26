// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.data;

import java.util.Objects;

/**
 * Row-oriented business cell — the Java analogue of the C++
 * {@code std::variant<std::monostate, int64_t, double>}.
 */
public final class Cell {

    private static final Cell NIL = new Cell(true, false, 0L, 0.0);

    private final boolean nil;
    private final boolean doubleValue;
    private final long longValue;
    private final double doubleValueValue;

    private Cell(boolean nil, boolean doubleValue, long longValue, double doubleValueValue) {
        this.nil = nil;
        this.doubleValue = doubleValue;
        this.longValue = longValue;
        this.doubleValueValue = doubleValueValue;
    }

    public static Cell of(long value) {
        return new Cell(false, false, value, 0.0);
    }

    public static Cell of(int value) {
        return of((long) value);
    }

    public static Cell of(double value) {
        return new Cell(false, true, 0L, value);
    }

    public static Cell of(float value) {
        return of((double) value);
    }

    public static Cell nil() {
        return NIL;
    }

    public boolean isNil() {
        return nil;
    }

    public boolean isDouble() {
        return doubleValue;
    }

    public long asLong() {
        if (nil) return 0L;
        return doubleValue ? (long) doubleValueValue : longValue;
    }

    public double asDouble() {
        if (nil) return 0.0;
        return doubleValue ? doubleValueValue : (double) longValue;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Cell cell)) return false;
        if (nil || cell.nil) return nil == cell.nil;
        if (doubleValue || cell.doubleValue) {
            return Double.compare(asDouble(), cell.asDouble()) == 0;
        }
        return longValue == cell.longValue;
    }

    @Override
    public int hashCode() {
        return Objects.hash(nil, doubleValue, longValue, doubleValueValue);
    }

    @Override
    public String toString() {
        return nil ? "nil" : doubleValue ? Double.toString(doubleValueValue)
                : Long.toString(longValue);
    }
}
