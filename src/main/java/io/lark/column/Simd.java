// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column;

import jdk.incubator.vector.ByteVector;
import jdk.incubator.vector.DoubleVector;
import jdk.incubator.vector.FloatVector;
import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.LongVector;
import jdk.incubator.vector.ShortVector;

/** Runtime information about the JDK incubator Vector API backend. */
public final class Simd {

    private Simd() {
    }

    public static String backendName() {
        return "JDK Vector API (jdk.incubator.vector)";
    }

    public static int doubleLanes() {
        return DoubleVector.SPECIES_PREFERRED.length();
    }

    public static int floatLanes() {
        return FloatVector.SPECIES_PREFERRED.length();
    }

    public static int intLanes() {
        return IntVector.SPECIES_PREFERRED.length();
    }

    public static int longLanes() {
        return LongVector.SPECIES_PREFERRED.length();
    }

    public static int byteLanes() {
        return ByteVector.SPECIES_PREFERRED.length();
    }

    public static int shortLanes() {
        return ShortVector.SPECIES_PREFERRED.length();
    }

    public static String summary() {
        return backendName()
                + " {double=" + doubleLanes()
                + ", float=" + floatLanes()
                + ", int=" + intLanes()
                + ", long=" + longLanes() + '}';
    }
}
