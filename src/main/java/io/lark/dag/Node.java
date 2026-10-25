// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.dag;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Abstract base for all business nodes.
 *
 * <p>Business code subclasses {@code Node} and implements {@link #compute}
 * for synchronous work, or overrides {@link #run} for custom orchestration.
 * Each node body runs on its own Java 21 virtual thread — the idiomatic Java
 * equivalent of the C++ coroutine {@code Task<void>}: blocking code parks the
 * virtual thread cheaply instead of occupying a platform worker. The
 * framework owns the node, drives it once its dependencies complete, times
 * it, catches failures and invokes {@link #fallback} for graceful
 * degradation.
 *
 * <p>Framework state (id, dependencies, completion event, status) is set by
 * the graph / executor; business code only reads it.
 */
public class Node {

    private String id = "";
    private String type = "";
    private final List<Node> dependencies = new ArrayList<>();
    private final AtomicReference<NodeStatus> status = new AtomicReference<>(NodeStatus.PENDING);
    private volatile CompletableFuture<Void> doneEvent = new CompletableFuture<>();
    private volatile long elapsedNanos;
    private volatile long startedAtNanos;
    private volatile Throwable error;

    // ---- business extension points ---------------------------------------

    /**
     * Synchronous business logic. Default no-op; override for the common
     * case. Runs on the executor's IO pool unless {@link #run} is overridden.
     */
    public void compute(IContext ctx) {
        // Default no-op.
    }

    /**
     * Node entry point, invoked on a dedicated virtual thread after all
     * dependencies completed. The default simply runs {@link #compute};
     * override for custom orchestration. Blocking calls are fine — they park
     * the virtual thread rather than a pool worker.
     *
     * <p>If this method throws, the executor invokes {@link #fallback}.
     */
    public void run(IContext ctx) {
        compute(ctx);
    }

    /**
     * Per-node deadline. Zero (the default) means "use the executor's
     * {@link TimeoutPolicy}"; a positive value overrides it. When the deadline
     * expires the executor interrupts the body and invokes
     * {@link #fallback}.
     */
    public Duration timeout() {
        return Duration.ZERO;
    }

    /**
     * Fallback / degradation hook, invoked when {@link #run} fails or times
     * out.
     *
     * @return {@code true} if a usable degraded result was produced (status
     *     becomes {@link NodeStatus#FALLBACK}); {@code false} to propagate the
     *     failure (status becomes {@link NodeStatus#FAILED} for errors or
     *     {@link NodeStatus#TIMED_OUT} for timeouts)
     */
    public boolean fallback(IContext ctx, Throwable error) {
        return false;
    }

    // ---- framework-managed accessors -------------------------------------

    public final String id() {
        return id;
    }

    public final String type() {
        return type;
    }

    public final List<Node> dependencies() {
        return Collections.unmodifiableList(dependencies);
    }

    public final NodeStatus status() {
        return status.get();
    }

    public final long elapsedNanos() {
        return elapsedNanos;
    }

    /**
     * Time offset in nanoseconds from the start of the run to when this node
     * began executing. Zero for skipped nodes.
     */
    public final long startedAtNanos() {
        return startedAtNanos;
    }

    public final Throwable error() {
        return error;
    }

    // ---- framework internals (called by Graph / Executor) ----------------

    final void setIdentity(String id, String type) {
        this.id = Objects.requireNonNull(id, "id");
        this.type = Objects.requireNonNull(type, "type");
    }

    final void addDependency(Node dependency) {
        dependencies.add(Objects.requireNonNull(dependency, "dependency"));
    }

    final CompletableFuture<Void> doneEvent() {
        return doneEvent;
    }

    final void setStatus(NodeStatus newStatus) {
        status.set(Objects.requireNonNull(newStatus, "status"));
    }

    final void setElapsedNanos(long elapsedNanos) {
        this.elapsedNanos = elapsedNanos;
    }

    final void setStartedAtNanos(long startedAtNanos) {
        this.startedAtNanos = startedAtNanos;
    }

    final void setError(Throwable error) {
        this.error = error;
    }

    /** Clear per-run state so the node can be executed again. */
    final void resetRunState() {
        status.set(NodeStatus.PENDING);
        elapsedNanos = 0;
        startedAtNanos = 0;
        error = null;
        doneEvent = new CompletableFuture<>();
    }
}
