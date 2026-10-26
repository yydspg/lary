// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.exec;

import io.lark.column.tensor.Tensor;
import io.lark.column.tensor.TensorStore;
import io.lark.column.tensor.FallibleTensorKernel;
import io.lark.column.tensor.KernelContext;
import io.lark.column.tensor.TensorKernel;
import io.lark.column.tensor.TensorKernels;
import io.lark.column.op.OpSpec;
import io.lark.column.op.OpTensor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * Execution-layer node. Binds a graph-level {@link OpSpec} to an execution
 * {@link TensorKernel}, plus graph wiring (id, module, dependency edges) and
 * per-run state.
 *
 * <p>The node owns orchestration only: it resolves named inputs from the store,
 * calls the kernel, and writes the first declared output. It does not contain
 * business formula code or tensor arithmetic.
 */
public final class ComputeNode {

    public enum Status {
        PENDING, SUCCESS, FAILED, FALLBACK
    }

    private final String id;
    private final String module;
    private final OpSpec op;
    private final TensorKernel kernel;
    private final List<ComputeNode> dependencies = new ArrayList<>();
    private final AtomicReference<Status> status = new AtomicReference<>(Status.PENDING);
    private volatile long elapsedNanos;
    private volatile Throwable error;

    public ComputeNode(String id, String module, OpSpec op, TensorKernel kernel) {
        this.id = Objects.requireNonNull(id, "id");
        this.module = Objects.requireNonNull(module, "module");
        this.op = Objects.requireNonNull(op, "op");
        this.kernel = Objects.requireNonNull(kernel, "kernel");
    }

    /**
     * Compatibility constructor for the pre-kernel API. New code should pass an
     * {@link OpSpec} and a {@link TensorKernel}.
     */
    @Deprecated
    public ComputeNode(String id, String module, OpTensor op) {
        this(id, module, TensorKernels.spec(op), TensorKernels.legacy(op));
    }

    public String id() {
        return id;
    }

    public String module() {
        return module;
    }

    /** Graph-level operation descriptor. */
    public OpSpec opSpec() {
        return op;
    }

    /** Actual execution operator. */
    public TensorKernel kernel() {
        return kernel;
    }

    public String opType() {
        return op.type();
    }

    public void addDependency(ComputeNode dependency) {
        dependencies.add(Objects.requireNonNull(dependency, "dependency"));
    }

    public List<ComputeNode> dependencies() {
        return List.copyOf(dependencies);
    }

    /**
     * Scheduler view of the dependency list. The returned list is owned by the
     * node and must not be modified; use {@link #dependencies()} at API
     * boundaries.
     */
    List<ComputeNode> dependencyView() {
        return dependencies;
    }

    /**
     * Rewire every edge pointing at {@code from} to {@code to} (used when a
     * placeholder is replaced by the real producer); de-duplicates.
     */
    public void replaceDependency(ComputeNode from, ComputeNode to) {
        for (int i = 0; i < dependencies.size(); i++) {
            if (dependencies.get(i) == from) {
                dependencies.set(i, to);
            }
        }
        List<ComputeNode> unique = new ArrayList<>();
        for (ComputeNode dependency : dependencies) {
            if (!unique.contains(dependency)) {
                unique.add(dependency);
            }
        }
        dependencies.clear();
        dependencies.addAll(unique);
    }

    public Status status() {
        return status.get();
    }

    public long elapsedNanos() {
        return elapsedNanos;
    }

    public Throwable error() {
        return error;
    }

    void execute(TensorStore store) {
        KernelContext ctx = context(store);
        kernel.compute(ctx);
    }

    boolean fallback(TensorStore store, Throwable failure) {
        if (kernel instanceof FallibleTensorKernel fallible) {
            return fallible.fallback(
                    new KernelContext(store, new Tensor[0], null), failure);
        }
        return false;
    }

    private KernelContext context(TensorStore store) {
        Tensor[] inputs = new Tensor[op.inputs().size()];
        for (int i = 0; i < inputs.length; i++) {
            inputs[i] = store.get(op.inputs().get(i));
        }
        Consumer<Tensor> sink = op.outputs().isEmpty()
                ? null : tensor -> store.set(op.outputs().get(0), tensor);
        return new KernelContext(store, inputs, sink);
    }

    void setStatus(Status newStatus) {
        status.set(Objects.requireNonNull(newStatus, "status"));
    }

    void setElapsedNanos(long elapsedNanos) {
        this.elapsedNanos = elapsedNanos;
    }

    void setError(Throwable error) {
        this.error = error;
    }

    void resetRunState() {
        status.set(Status.PENDING);
        elapsedNanos = 0;
        error = null;
    }
}
