// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.recsys.columns;

import io.lark.column.biz.DoubleColumn;

/** Coupon cost per repeat order. */
public final class BuyAgainCost extends DoubleColumn {

    public BuyAgainCost() {
        super("buyagain_cost");
    }
}
