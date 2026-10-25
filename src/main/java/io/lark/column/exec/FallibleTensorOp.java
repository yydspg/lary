// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.exec;

/**
 * Optional extension for a {@link TensorOp} that can degrade gracefully when
 * its execution fails (business-node fallback, timeout fallback, ...).
 *
 * <p>Kept in the execution layer so business adapters can provide fallback
 * without exposing {@link OpTensor} itself.
 */
public interface FallibleTensorOp extends TensorOp {

    /**
     * Produce a degraded result for the failed execution.
     *
     * @return {@code true} when a usable result was written into the context
     */
    boolean fallback(OpContext ctx, Throwable error);
}
