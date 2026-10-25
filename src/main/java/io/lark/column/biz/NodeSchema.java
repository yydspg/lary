// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import java.util.Objects;

/**
 * Output schema of a single-output node.
 *
 * <p>Nodes expose one {@code SCHEMA} constant and pass it to the typed base
 * constructor instead of repeating the raw output column:
 *
 * <pre>{@code
 * final class CouponRoiNode extends BinaryNode<...> {
 *     public static final NodeSchema<Double> SCHEMA =
 *             NodeSchema.of(Metrics.COUPON_ROI);
 * }
 *
 * // module
 * private final CouponRoiNode couponRoi =
 *         node(new CouponRoiNode(), gmv, cost);
 * }</pre>
 *
 * <p>The schema is a property of the node type. The typed base constructor
 * looks up the public static {@code SCHEMA} field once per class, so modules and
 * tooling can read the output definition without constructing the node.
 */
public record NodeSchema<O>(Column<O> output) {

    public NodeSchema {
        Objects.requireNonNull(output, "output");
    }

    public static <O> NodeSchema<O> of(Column<O> output) {
        return new NodeSchema<>(output);
    }
}
