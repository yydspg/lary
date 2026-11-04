// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.rank;

import java.util.Arrays;

/**
 * Stable LSD radix sort for double scores.
 *
 * <p>Scores are mapped to unsigned sortable keys, so the sort is O(8 * n) with
 * 256 buckets and does not allocate comparison objects. Equal scores keep the
 * input order, which makes the original candidate order the deterministic
 * tie-breaker.
 */
public final class RadixSorter {

    private static final int BITS = 8;
    private static final int BUCKETS = 1 << BITS;
    private static final int MASK = BUCKETS - 1;

    private RadixSorter() {
    }

    /** Order {@code 0..count-1} by descending score; stable on ties. */
    public static int[] orderDescending(double[] scores, int count) {
        if (count < 0 || count > scores.length) {
            throw new RankingException(
                    "radix sort count out of range: " + count);
        }
        int[] order = new int[count];
        for (int i = 0; i < count; i++) {
            order[i] = i;
        }
        sortDescending(scores, order, count);
        return order;
    }

    /**
     * Sort an existing index permutation.
     *
     * @param order initial permutation, normally {@code 0..count-1}
     */
    public static void sortDescending(double[] scores, int[] order, int count) {
        if (count < 0 || count > scores.length || count > order.length) {
            throw new RankingException(
                    "radix sort count out of range: " + count);
        }
        if (count < 2) {
            return;
        }

        long[] keys = new long[count];
        for (int i = 0; i < count; i++) {
            double score = scores[i];
            if (!Double.isFinite(score)) {
                throw new RankingException(
                        "radix sort requires finite scores, got " + score);
            }
            // Normalize -0.0 so it ties with +0.0.
            if (score == 0.0) {
                score = 0.0;
            }
            long bits = Double.doubleToLongBits(score);
            long ascending = bits ^ ((bits >> 63) | Long.MIN_VALUE);
            keys[i] = ~ascending;
        }

        int[] source = order;
        int[] target = new int[count];
        int[] bucket = new int[BUCKETS];
        for (int shift = 0; shift < Long.SIZE; shift += BITS) {
            Arrays.fill(bucket, 0);
            for (int i = 0; i < count; i++) {
                bucket[(int) ((keys[source[i]] >>> shift) & MASK)]++;
            }
            int sum = 0;
            for (int i = 0; i < BUCKETS; i++) {
                int current = bucket[i];
                bucket[i] = sum;
                sum += current;
            }
            for (int i = 0; i < count; i++) {
                int index = source[i];
                int position = (int) ((keys[index] >>> shift) & MASK);
                target[bucket[position]++] = index;
            }
            int[] swap = source;
            source = target;
            target = swap;
        }
        if (source != order) {
            System.arraycopy(source, 0, order, 0, count);
        }
    }
}
