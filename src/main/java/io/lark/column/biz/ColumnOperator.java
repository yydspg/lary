// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import io.lark.column.tensor.Tensor;

/**
 * Declarative column operator: the computation a node applies to its inputs.
 *
 * <p>A module selects an operator (normally as a class) and wires typed
 * streams; it never writes a per-node compute body:
 *
 * <pre>{@code
 * ctr = node(SearchMetrics.CTR, RatioOp.class, clicks, impressions);
 * couponCost = node(SearchMetrics.COUPON_COST,
 *         new ScaleOp(100.0), feed(SearchMetrics.COUPON_COST_FEN));
 * }</pre>
 *
 * <p>Built-in operators live in {@code io.lark.column.biz.op}. Domain-specific
 * operators implement this interface once and are then reused by class or
 * instance, exactly like the built-ins. Operators contain the math; modules
 * contain only the graph wiring.
 *
 * <p>The operator result is coerced to the output column's declared dtype.
 */
@FunctionalInterface
public interface ColumnOperator {

    /** Apply the operator to already-resolved input tensors. */
    Tensor apply(Tensor... inputs);

    /**
     * Context-aware form. The default ignores the run context; override it
     * only when the computation needs request-scoped values.
     */
    default Tensor apply(ColumnOperatorContext context, Tensor... inputs) {
        return apply(inputs);
    }

    /**
     * Expected input count, or {@code -1} when the operator accepts any
     * number. Parameterless operators return their fixed arity so modules can
     * reject bad wiring while defining the graph.
     */
    default int arity() {
        return -1;
    }

    /** Human-readable name used in wiring errors. */
    default String operatorName() {
        return getClass().getSimpleName().isEmpty()
                ? getClass().getName() : getClass().getSimpleName();
    }

    /**
     * Stable identity used to share anonymous nodes across modules. Stateless
     * operators can keep the default class name; parameterized operators must
     * include their parameters.
     */
    default String operatorKey() {
        return getClass().getName();
    }
}
