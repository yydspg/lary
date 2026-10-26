// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.op;

import io.lark.column.DType;
import io.lark.column.Tensor;
import io.lark.column.TensorShape;
import io.lark.column.TensorStore;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OpTensorTest {

    private static OpContext context(Tensor a, Tensor b) {
        TensorStore store = new TensorStore();
        store.set("a", a);
        store.set("b", b);
        return new OpContext(store);
    }

    private static Tensor result(OpContext ctx, String name) {
        return ctx.store().get(name);
    }

    @Test
    void floatAddAndMultiTensorAreTypedOperations() {
        OpContext sumCtx = context(
                Tensor.fromFloats(1.0f, 2.0f, 3.0f),
                Tensor.fromFloats(10.0f, 20.0f, 30.0f));
        OpTensor add = new FloatAddTensor(List.of("a", "b"), List.of("sum"));
        assertEquals("float_add", add.opType());
        add.compute(sumCtx);

        Tensor sum = result(sumCtx, "sum");
        assertEquals(DType.FLOAT32, sum.dtype());
        assertEquals(11.0f, sum.getFloat(0), 1e-6f);
        assertEquals(33.0f, sum.getFloat(2), 1e-6f);

        OpContext mulCtx = context(
                Tensor.fromFloats(2.0f, 3.0f),
                Tensor.fromFloats(4.0f, 5.0f));
        OpTensor multi = new FloatMultiTensor(List.of("a", "b"), List.of("product"));
        assertEquals("float_multi", multi.opType());
        multi.compute(mulCtx);

        Tensor product = result(mulCtx, "product");
        assertEquals(DType.FLOAT32, product.dtype());
        assertEquals(8.0f, product.getFloat(0), 1e-6f);
        assertEquals(15.0f, product.getFloat(1), 1e-6f);
    }

    @Test
    void typedOpsCoverIntLongAndDouble() {
        OpContext intCtx = context(
                Tensor.fromInts(1, 2, 3),
                Tensor.fromInts(10, 20, 30));
        new IntAddTensor(List.of("a", "b"), List.of("r")).compute(intCtx);
        assertEquals(DType.INT32, result(intCtx, "r").dtype());
        assertEquals(22, result(intCtx, "r").getInt(1));

        OpContext longCtx = context(
                Tensor.fromLongs(2, 3),
                Tensor.fromLongs(4, 5));
        new LongMultiTensor(List.of("a", "b"), List.of("r")).compute(longCtx);
        assertEquals(DType.INT64, result(longCtx, "r").dtype());
        assertEquals(15, result(longCtx, "r").getLong(1));

        OpContext doubleCtx = context(
                Tensor.fromDoubles(1.5, 2.5),
                Tensor.fromDoubles(2.0, 4.0));
        new DoubleSubTensor(List.of("a", "b"), List.of("r")).compute(doubleCtx);
        assertEquals(DType.FLOAT64, result(doubleCtx, "r").dtype());
        assertEquals(-0.5, result(doubleCtx, "r").getDouble(0), 1e-9);
    }

    @Test
    void dispatcherSelectsTypedOpFromRuntimeDtype() {
        TensorStore store = new TensorStore();
        store.set("a", Tensor.fromLongs(1, 2, 3));
        store.set("b", Tensor.fromDoubles(0.5, 1.5, 2.5));
        OpContext ctx = new OpContext(store);

        OpTensor add = TensorOps.create(
                new OpSpec("add", List.of("a", "b"), List.of("r")));
        assertInstanceOf(AddTensor.class, add);
        add.compute(ctx);

        Tensor result = store.get("r");
        assertEquals(DType.FLOAT64, result.dtype());
        assertEquals(1.5, result.getDouble(0), 1e-9);
        assertEquals(5.5, result.getDouble(2), 1e-9);
    }

    @Test
    void divisionAlwaysProducesFloat64() {
        OpContext ctx = context(
                Tensor.fromLongs(2, 4),
                Tensor.fromLongs(2, 8));
        new DoubleDivTensor(List.of("a", "b"), List.of("r")).compute(ctx);
        assertEquals(DType.FLOAT64, ctx.store().get("r").dtype());
        assertEquals(1.0, ctx.store().get("r").getDouble(0), 1e-9);
        assertEquals(0.5, ctx.store().get("r").getDouble(1), 1e-9);
    }

    @Test
    void zerosTensorCreatesARequestedShape() {
        TensorStore store = new TensorStore();
        OpTensor zeros = TensorOps.create(new OpSpec("zeros", List.of(),
                List.of("z"), Map.of("dtype", "float64", "shape", "2,3")));
        zeros.compute(new OpContext(store));

        Tensor value = store.get("z");
        assertEquals(TensorShape.of(2, 3), value.shape());
        assertEquals(6, value.size());
        assertEquals(0.0, value.getDouble(5), 1e-9);
    }

    @Test
    void constSupportsShapeAndLegacyCommaLists() {
        TensorStore store = new TensorStore();
        TensorOps.create(new OpSpec("const", List.of(), List.of("legacy"),
                Map.of("value", "1,2,3"))).compute(new OpContext(store));
        assertEquals(3, store.get("legacy").size());
        assertEquals(3.0, store.get("legacy").getDouble(2), 1e-9);

        TensorOps.create(new OpSpec("const", List.of(), List.of("shaped"),
                Map.of("value", "1,2,3,4", "shape", "2x2")))
                .compute(new OpContext(store));
        assertEquals(TensorShape.of(2, 2), store.get("shaped").shape());
        assertEquals(4.0, store.get("shaped").getDouble(3), 1e-9);
    }

    @Test
    void shapeMismatchFailsFast() {
        OpContext ctx = context(
                Tensor.fromFloats(1.0f, 2.0f, 3.0f),
                Tensor.fromFloats(1.0f, 2.0f));
        assertTrue(throwsIllegalArgument(
                () -> new FloatAddTensor(List.of("a", "b"), List.of("r")).compute(ctx)));
    }

    private static boolean throwsIllegalArgument(Runnable action) {
        try {
            action.run();
            return false;
        } catch (IllegalArgumentException expected) {
            return true;
        }
    }
}
