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
 *
 * <p>{@link IndexSorter} is the allocation-free form used by the ranking
 * engine: it sorts an {@code int[]} of candidate indexes in place and never
 * moves the candidate objects themselves.
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
     * Sort an existing index permutation by scores aligned with the permutation.
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
            keys[i] = descendingKey(scores[i]);
        }
        int[] target = new int[count];
        int[] bucket = new int[BUCKETS];
        sort(order, target, bucket, keys, count);
    }

    private static void sort(int[] source, int[] target, int[] bucket,
                             long[] keys, int count) {
        int[] current = source;
        int[] next = target;
        for (int shift = 0; shift < Long.SIZE; shift += BITS) {
            Arrays.fill(bucket, 0);
            for (int i = 0; i < count; i++) {
                bucket[(int) ((keys[current[i]] >>> shift) & MASK)]++;
            }
            int sum = 0;
            for (int i = 0; i < BUCKETS; i++) {
                int value = bucket[i];
                bucket[i] = sum;
                sum += value;
            }
            for (int i = 0; i < count; i++) {
                int index = current[i];
                int position = (int) ((keys[index] >>> shift) & MASK);
                next[bucket[position]++] = index;
            }
            int[] swap = current;
            current = next;
            next = swap;
        }
        if (current != source) {
            System.arraycopy(current, 0, source, 0, count);
        }
    }

    private static long descendingKey(double score) {
        if (!Double.isFinite(score)) {
            throw new RankingException(
                    "radix sort requires finite scores, got " + score);
        }
        double normalized = score == 0.0 ? 0.0 : score;
        long bits = Double.doubleToLongBits(normalized);
        long ascending = bits ^ ((bits >> 63) | Long.MIN_VALUE);
        return ~ascending;
    }

    /**
     * Reusable radix-sort session: sorts candidate indexes by a score array
     * indexed by candidate index, with no object movement and no per-slot
     * allocation.
     */
    public static final class IndexSorter {

        private final long[] keys;
        private final int[] temp;
        private final int[] bucket = new int[BUCKETS];

        public IndexSorter(int capacity) {
            if (capacity < 0) {
                throw new IllegalArgumentException(
                        "capacity must be non-negative");
            }
            this.keys = new long[capacity];
            this.temp = new int[capacity];
        }

        /**
         * Sort {@code indices[0..count)} by {@code scoresByIndex[indices[i]]}
         * descending, stable on ties.
         */
        public void sortDescending(double[] scoresByIndex, int[] indices,
                                   int count) {
            if (count < 0 || count > indices.length || count > temp.length) {
                throw new RankingException(
                        "radix sort count out of range: " + count);
            }
            if (count < 2) {
                return;
            }
            for (int i = 0; i < count; i++) {
                int index = indices[i];
                if (index < 0 || index >= keys.length
                        || index >= scoresByIndex.length) {
                    throw new RankingException(
                            "radix sort candidate index out of range: "
                                    + index);
                }
                keys[index] = descendingKey(scoresByIndex[index]);
            }
            sort(indices, temp, bucket, keys, count);
        }
    }
}
