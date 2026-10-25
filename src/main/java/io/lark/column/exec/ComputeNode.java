// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.exec;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Execution-layer node. Binds exactly one {@link TensorOp}, plus framework
 * wiring (id, module, dependency edges) and per-run state.
 */
public final class ComputeNode {

    public enum Status {
        PENDING, SUCCESS, FAILED
    }

    private final String id;
    private final String module;
    private final TensorOp op;
    private final List<ComputeNode> dependencies = new ArrayList<>();
    private final AtomicReference<Status> status = new AtomicReference<>(Status.PENDING);
    private volatile CompletableFuture<Void> doneEvent = new CompletableFuture<>();
    private volatile long elapsedNanos;
    private volatile Throwable error;

    public ComputeNode(String id, String module, TensorOp op) {
        this.id = Objects.requireNonNull(id, "id");
        this.module = Objects.requireNonNull(module, "module");
        this.op = Objects.requireNonNull(op, "op");
    }

    public String id() {
        return id;
    }

    public String module() {
        return module;
    }

    public TensorOp op() {
        return op;
    }

    public void addDependency(ComputeNode dependency) {
        dependencies.add(Objects.requireNonNull(dependency, "dependency"));
    }

    public List<ComputeNode> dependencies() {
        return List.copyOf(dependencies);
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

    CompletableFuture<Void> doneEvent() {
        return doneEvent;
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
        doneEvent = new CompletableFuture<>();
    }
}
