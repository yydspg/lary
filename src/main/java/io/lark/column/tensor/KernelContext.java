// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.tensor;


import java.util.Objects;
import java.util.function.Consumer;

/**
 * Execution view handed to a {@link TensorKernel}.
 *
 * <p>Inputs are already resolved by the execution node; the kernel emits its
 * result through {@link #output(Tensor)} and never sees op metadata such as
 * input/output names/dependency edges.
 */
public final class KernelContext {

    private final TensorStore store;
    private final Tensor[] inputs;
    private final Consumer<Tensor> output;

    public KernelContext(TensorStore store, Tensor[] inputs,
                         Consumer<Tensor> output) {
        this.store = Objects.requireNonNull(store, "store");
        this.inputs = inputs == null ? new Tensor[0] : inputs;
        this.output = output;
    }

    public Tensor input(int index) {
        if (index < 0 || index >= inputs.length) {
            throw new IndexOutOfBoundsException(
                    "Kernel input " + index + " out of range [0, "
                            + inputs.length + ")");
        }
        return inputs[index];
    }

    public int inputCount() {
        return inputs.length;
    }

    public void output(Tensor tensor) {
        if (output == null) {
            throw new IllegalStateException(
                    "This kernel context does not accept a tensor output");
        }
        output.accept(Objects.requireNonNull(tensor, "tensor"));
    }

    /** Store escape hatch for business-node adapters and fallback handlers. */
    public TensorStore store() {
        return store;
    }
}
