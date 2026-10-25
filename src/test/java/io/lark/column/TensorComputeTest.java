// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column;

import io.lark.column.compute.Compute;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TensorComputeTest {

    @Test
    void multiDtypeTensors() {
        Tensor i32 = Tensor.fromInts(1, 2, 3, 4);
        assertEquals(DType.INT32, i32.dtype());
        assertEquals(3, i32.getInt(2));

        Tensor f32 = Tensor.fromFloats(1.5f, 2.5f, 3.5f);
        assertEquals(DType.FLOAT32, f32.dtype());
        assertEquals(2.5f, f32.getFloat(1), 1e-6f);

        assertEquals(7, Tensor.full(4, 7.0, DType.INT32).getInt(3));
        assertEquals(2.5f, Tensor.full(4, 2.5, DType.FLOAT32).getFloat(0), 1e-6f);

        assertTrue(DType.INT32.isInt());
        assertTrue(DType.FLOAT32.isFloat());
        assertEquals("q8_0", DType.Q8_0.displayName());
        assertEquals(4, DType.INT32.scalarBytes());
        assertEquals(4, DType.FLOAT32.scalarBytes());
        assertEquals(2 * BlockQ8_0.BLOCK_BYTES, DType.bytesFor(33, DType.Q8_0));
    }

    @Test
    void q8_0QuantizationRoundTrip() {
        double[] values = new double[100];
        for (int i = 0; i < values.length; i++) values[i] = i % 50;
        Tensor a = Tensor.fromDoubles(values);
        Tensor q = a.quantizeQ8_0();

        assertEquals(DType.Q8_0, q.dtype());
        assertEquals(a.size(), q.size());
        assertEquals(4, q.numBlocks());

        Tensor d = q.dequantize();
        assertEquals(DType.FLOAT64, d.dtype());
        double maxError = 0.0;
        for (int i = 0; i < a.size(); i++) {
            maxError = Math.max(maxError,
                    Math.abs(a.getDouble(i) - d.getDouble(i)));
        }
        assertTrue(maxError < 0.5, "max error " + maxError);

        double[] yValues = new double[100];
        for (int i = 0; i < yValues.length; i++) yValues[i] = (i * 3) % 50;
        Tensor b = Tensor.fromDoubles(yValues);
        Tensor qb = b.quantizeQ8_0();

        double exact = 0.0;
        for (int i = 0; i < values.length; i++) exact += values[i] * yValues[i];
        double approx = q.dotQ8_0(qb);
        assertTrue(Math.abs(approx - exact) / exact < 0.1);

        assertThrows(IllegalStateException.class,
                () -> Tensor.ones(3, DType.FLOAT64).dequantize());
    }

    @Test
    void tensorStoreSupportsDifferentLengths() {
        TensorStore store = new TensorStore();
        store.set("x", Tensor.fromLongs(1, 2, 3));
        store.set("y", Tensor.fromDoubles(1.5, 2.5));
        assertEquals(2, store.size());
        assertTrue(store.has("x"));
        assertFalse(store.has("z"));
        assertEquals("x", store.names().get(0));
        assertEquals(3, store.get("x").size());
        assertEquals(2, store.get("y").size());

        store.set("x", Tensor.fromLongs(9));
        assertEquals(9, store.get("x").getLong(0));
        assertEquals(2, store.names().size());

        store.erase("y");
        assertFalse(store.has("y"));

        TensorTable table = new TensorTable();
        table.add("a", Tensor.fromLongs(1, 2));
        table.add("b", Tensor.fromDoubles(0.5, 1.5));
        TensorStore s2 = new TensorStore();
        s2.addTable(table);
        TensorTable out = s2.toTable(List.of("a", "b"));
        assertEquals(2, out.numRows());
    }

    @Test
    void feedAndFetchRoundTrip() {
        List<Row> rows = List.of(
                Row.of(Cell.of(1), Cell.of(10.5)),
                Row.of(Cell.of(2), Cell.of(20.0)),
                Row.of(Cell.nil(), Cell.of(30.0)));

        TensorTable table = FeedFetch.feedTable(List.of("x", "y"), rows);
        assertEquals(DType.INT64, table.get("x").dtype());
        assertEquals(DType.FLOAT64, table.get("y").dtype());
        assertEquals(3, table.get("x").size());
        assertEquals(0L, table.get("x").getLong(2));

        List<Row> out = FeedFetch.fetchRows(table, List.of("x", "y"));
        assertEquals(3, out.size());
        assertEquals(1L, out.get(0).get(0).asLong());
        assertEquals(10.5, out.get(0).get(1).asDouble(), 1e-9);
        assertEquals(20.0, out.get(1).get(1).asDouble(), 1e-9);
        assertEquals(30.0, out.get(2).get(1).asDouble(), 1e-9);
    }

    @Test
    void arithmeticAndScalarOps() {
        Tensor a = Tensor.fromDoubles(1, 2, 3, 4);
        Tensor b = Tensor.fromDoubles(10, 20, 30, 40);

        assertEquals(11.0, Compute.add(a, b).getDouble(0), 1e-9);
        assertEquals(44.0, Compute.add(a, b).getDouble(3), 1e-9);
        assertEquals(9.0, Compute.sub(b, a).getDouble(0), 1e-9);
        assertEquals(90.0, Compute.mul(a, b).getDouble(2), 1e-9);
        assertEquals(10.0, Compute.div(b, a).getDouble(1), 1e-9);

        Tensor x = Tensor.fromLongs(1, 2, 3);
        Tensor y = Tensor.fromLongs(10, 20, 30);
        Tensor intSum = Compute.add(x, y);
        assertEquals(DType.INT64, intSum.dtype());
        assertEquals(22, intSum.getLong(1));

        Tensor mixed = Compute.mul(x, Tensor.fromDoubles(1, 2, 3));
        assertEquals(DType.FLOAT64, mixed.dtype());
        assertEquals(4.0, mixed.getDouble(1), 1e-9);

        assertEquals(2.0, Compute.addScalar(a, 1.0).getDouble(0), 1e-9);
        assertEquals(6.0, Compute.mulScalar(a, 2.0).getDouble(2), 1e-9);
        assertEquals(6, Compute.mulScalar(x, 2.0).getLong(2));
        assertEquals(DType.FLOAT64, Compute.divScalar(x, 2.0).dtype());
        assertEquals(0.5, Compute.divScalar(x, 2.0).getDouble(0), 1e-9);
        assertEquals(-4.0, Compute.neg(a).getDouble(3), 1e-9);
    }

    @Test
    void unaryOperations() {
        Tensor a = Tensor.fromDoubles(1, 4, 9, 16);
        assertEquals(1.0, Compute.square(a).getDouble(0), 1e-9);
        assertEquals(16.0, Compute.square(a).getDouble(1), 1e-9);
        assertEquals(2.0, Compute.sqrt(a).getDouble(1), 1e-9);
        assertEquals(4.0, Compute.sqrt(a).getDouble(3), 1e-9);

        Tensor negative = Tensor.fromDoubles(-1, -2, 3);
        assertEquals(1.0, Compute.abs(negative).getDouble(0), 1e-9);
        assertEquals(2.0, Compute.abs(negative).getDouble(1), 1e-9);
        assertEquals(1.0, Compute.exp(Tensor.fromDoubles(0.0)).getDouble(0), 1e-9);
        assertEquals(0.0, Compute.log(Tensor.fromDoubles(1.0)).getDouble(0), 1e-9);
    }

    @Test
    void reductions() {
        Tensor a = Tensor.fromDoubles(1, 2, 3, 4, 5);
        assertEquals(15.0, Compute.sum(a), 1e-9);
        assertEquals(3.0, Compute.mean(a), 1e-9);
        assertEquals(5, Compute.count(a));
        assertEquals(5, Compute.max(a), 1e-9);
        assertEquals(1, Compute.min(a), 1e-9);
        assertEquals(Math.sqrt(2.0), Compute.stddev(a), 1e-9);

        Tensor b = Tensor.fromLongs(10, 20, 30);
        assertEquals(60.0, Compute.sum(b), 1e-9);
        assertEquals(20.0, Compute.mean(b), 1e-9);

        Tensor empty = Tensor.fromLongs();
        assertEquals(0.0, Compute.max(empty), 1e-9);
        assertEquals(0.0, Compute.min(empty), 1e-9);
        assertEquals(0.0, Compute.sum(empty), 1e-9);
    }

    @Test
    void comparisons() {
        Tensor a = Tensor.fromDoubles(1, 5, 10, 15);
        Tensor mask = Compute.gtScalar(a, 5.0);
        assertEquals(DType.INT64, mask.dtype());
        assertEquals(0, mask.getLong(0));
        assertEquals(0, mask.getLong(1));
        assertEquals(1, mask.getLong(2));
        assertEquals(1, mask.getLong(3));

        Tensor le = Compute.leScalar(a, 5.0);
        assertEquals(1, le.getLong(0));
        assertEquals(1, le.getLong(1));
        assertEquals(0, le.getLong(2));

        Tensor b = Tensor.fromDoubles(2, 5, 8, 20);
        Tensor gt = Compute.gt(a, b);
        assertEquals(0, gt.getLong(0));
        assertEquals(0, gt.getLong(1));
        assertEquals(1, gt.getLong(2));
        assertEquals(0, gt.getLong(3));
    }

    @Test
    void specialOperations() {
        Tensor a = Tensor.fromLongs(1, 2, 3, 4);
        Tensor b = Tensor.fromLongs(10, 20, 30, 40);
        Tensor mask = Compute.gtScalar(a, 2.0);

        Tensor selected = Compute.select(mask, a, b);
        assertEquals(10, selected.getLong(0));
        assertEquals(20, selected.getLong(1));
        assertEquals(3, selected.getLong(2));
        assertEquals(4, selected.getLong(3));

        Tensor filtered = Compute.filter(a, mask);
        assertEquals(2, filtered.size());
        assertEquals(3, filtered.getLong(0));
        assertEquals(4, filtered.getLong(1));

        Tensor casted = Compute.cast(a, DType.FLOAT32);
        assertEquals(DType.FLOAT32, casted.dtype());
        assertEquals(1.0f, casted.getFloat(0), 1e-6f);

        Tensor q = Compute.quantize(Tensor.fromDoubles(1.5, 2.5, 3.5, 4.5));
        assertEquals(DType.Q8_0, q.dtype());
        Tensor dq = Compute.dequantize(q);
        assertEquals(DType.FLOAT64, dq.dtype());
        double dot = Compute.dot(q, q);
        double expected = 1.5 * 1.5 + 2.5 * 2.5 + 3.5 * 3.5 + 4.5 * 4.5;
        assertTrue(Math.abs(dot - expected) / expected < 0.1);

        Tensor constant = Compute.constant("1,2,3", DType.INT64, 1);
        assertEquals(3, constant.size());
        assertEquals(2, constant.getLong(1));
    }

    @Test
    void simdLargeTensor() {
        int n = 10_000;
        Tensor a = Tensor.full(n, 3.0, DType.FLOAT64);
        Tensor b = Tensor.full(n, 7.0, DType.FLOAT64);

        Tensor sum = Compute.add(a, b);
        Tensor product = Compute.mul(a, b);
        Tensor mixed = Compute.add(Compute.mul(a, b), Compute.sub(a, b));

        assertEquals(10.0, sum.getDouble(0), 1e-9);
        assertEquals(21.0, product.getDouble(0), 1e-9);
        assertEquals(17.0, mixed.getDouble(0), 1e-9);
        assertEquals(30_000.0, Compute.sum(a), 1e-6);
        assertEquals(3.0, Compute.mean(a), 1e-9);
        assertTrue(Simd.doubleLanes() > 0);
        assertTrue(Simd.floatLanes() > 0);
        assertTrue(Simd.intLanes() > 0);
        assertTrue(Simd.longLanes() > 0);
    }
}
