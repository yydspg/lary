// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.tensor;

import io.lark.column.context.ColumnRunContext;
import io.lark.column.context.ExperimentContext;

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
    private final String[] inputNames;
    private Tensor[] resolvedInputs;
    private final Consumer<Tensor> output;
    private final ExperimentContext experiment;
    private final ColumnRunContext runContext;

    public KernelContext(TensorStore store, Tensor[] inputs,
                         Consumer<Tensor> output) {
        this(store, inputs, output, ExperimentContext.empty());
    }

    public KernelContext(TensorStore store, Tensor[] inputs,
                         Consumer<Tensor> output,
                         ExperimentContext experiment) {
        this(store, inputs, output, experiment, ColumnRunContext.empty());
    }

    public KernelContext(TensorStore store, Tensor[] inputs,
                         Consumer<Tensor> output,
                         ExperimentContext experiment,
                         ColumnRunContext runContext) {
        this.store = Objects.requireNonNull(store, "store");
        this.inputs = inputs == null ? new Tensor[0] : inputs;
        this.inputNames = null;
        this.output = output;
        this.experiment = Objects.requireNonNull(experiment, "experiment");
        this.runContext = Objects.requireNonNull(runContext, "runContext");
    }

    /** Lazy-input form: names are resolved from the store on demand. */
    public KernelContext(TensorStore store, String[] inputNames,
                         Consumer<Tensor> output,
                         ExperimentContext experiment,
                         ColumnRunContext runContext) {
        this.store = Objects.requireNonNull(store, "store");
        this.inputs = null;
        this.inputNames = Objects.requireNonNull(inputNames, "inputNames").clone();
        this.output = output;
        this.experiment = Objects.requireNonNull(experiment, "experiment");
        this.runContext = Objects.requireNonNull(runContext, "runContext");
    }

    public Tensor input(int index) {
        int count = inputCount();
        if (index < 0 || index >= count) {
            throw new IndexOutOfBoundsException(
                    "Kernel input " + index + " out of range [0, "
                            + count + ")");
        }
        if (inputs != null) {
            return inputs[index];
        }
        if (resolvedInputs != null) {
            return resolvedInputs[index];
        }
        return store.get(inputNames[index]);
    }

    public int inputCount() {
        return inputs != null ? inputs.length : inputNames.length;
    }

    /** Materialize all inputs once without copying for array-backed contexts. */
    public Tensor[] inputArray() {
        if (inputs != null) {
            return inputs;
        }
        if (resolvedInputs == null) {
            Tensor[] values = new Tensor[inputNames.length];
            for (int i = 0; i < values.length; i++) {
                values[i] = store.get(inputNames[i]);
            }
            resolvedInputs = values;
        }
        return resolvedInputs;
    }

    public void output(Tensor tensor) {
        if (output == null) {
            throw new IllegalStateException(
                    "This kernel context does not accept a tensor output");
        }
        output.accept(Objects.requireNonNull(tensor, "tensor"));
    }

    /** Experiment assignments/parameters for this run. */
    public ExperimentContext experiment() {
        return experiment;
    }

    /** Typed business context for this run. */
    public ColumnRunContext runContext() {
        return runContext;
    }

    /** Store escape hatch for business-node adapters and fallback handlers. */
    public TensorStore store() {
        return store;
    }
}
