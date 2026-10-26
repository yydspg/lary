// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.tensor;

/**
 * Actual tensor execution operator.
 *
 * <p>A kernel contains computation logic only. Graph metadata (op type, input
 * names, output names and attributes) lives in
 * {@link io.lark.column.op.OpSpec}; the execution node resolves inputs and
 * writes outputs through {@link KernelContext}.
 */
@FunctionalInterface
public interface TensorKernel {

    void compute(KernelContext ctx);
}
