// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import io.lark.column.tensor.DType;

/** Numeric formula expression (FLOAT64 column). */
public interface NumExpr extends Expr<Double> {

    @Override
    default DType dtype() {
        return DType.FLOAT64;
    }

    default NumExpr add(NumExpr other) {
        return F.add(this, other);
    }

    default NumExpr sub(NumExpr other) {
        return F.sub(this, other);
    }

    default NumExpr mul(NumExpr other) {
        return F.mul(this, other);
    }

    default NumExpr div(NumExpr other) {
        return F.div(this, other);
    }

    default BoolExpr gt(NumExpr other) {
        return F.gt(this, other);
    }

    default BoolExpr ge(NumExpr other) {
        return F.ge(this, other);
    }

    default BoolExpr lt(NumExpr other) {
        return F.lt(this, other);
    }

    default BoolExpr le(NumExpr other) {
        return F.le(this, other);
    }

    default BoolExpr eq(NumExpr other) {
        return F.eq(this, other);
    }

    default BoolExpr neq(NumExpr other) {
        return F.neq(this, other);
    }
}
