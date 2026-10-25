// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.dag;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Supplier;

/**
 * Orchestration primitives for business code.
 *
 * <p>With Java 21+ virtual threads, blocking is cheap: a node body runs on its
 * own virtual thread, and {@link #run} / {@link #call} move work onto a
 * specific pool exactly like the C++ {@code Schedule} awaiter hops a
 * coroutine. The asynchronous {@link #hop}, {@link #submit} and
 * {@link #supply} variants remain available for future-style composition.
 */
public final class Schedule {

    private Schedule() {
    }

    /**
     * Run {@code work} on the given pool and block the current (virtual)
     * thread until it finishes. Exceptions are rethrown unchanged for
     * unchecked/error types, or wrapped in {@link CompletionException}.
     */
    public static void run(IContext ctx, PoolKind kind, Runnable work) {
        Objects.requireNonNull(work, "work");
        await(ctx.pool(kind).submit(work));
    }

    /** Callable variant of {@link #run}; returns the produced value. */
    public static <T> T call(IContext ctx, PoolKind kind, Callable<? extends T> work) {
        Objects.requireNonNull(work, "work");
        try {
            return ctx.pool(kind).submit(work).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CompletionException(e);
        } catch (ExecutionException e) {
            throw rethrow(e.getCause());
        }
    }

    /** Fire-and-forget a callable on the chosen pool. */
    public static void spawn(IContext ctx, PoolKind kind, Runnable work) {
        Objects.requireNonNull(work, "work");
        ctx.pool(kind).submit(work);
    }

    /** Awaitable zero-work hop onto the chosen pool. */
    public static CompletableFuture<Void> hop(IContext ctx, PoolKind kind) {
        CompletableFuture<Void> ready = new CompletableFuture<>();
        ctx.pool(kind).execute(() -> ready.complete(null));
        return ready;
    }

    /** Submit work asynchronously on the chosen pool. */
    public static CompletableFuture<Void> submit(IContext ctx, PoolKind kind,
                                                 Runnable work) {
        return CompletableFuture.runAsync(Objects.requireNonNull(work, "work"),
                ctx.pool(kind));
    }

    /** Submit a value-producing task asynchronously on the chosen pool. */
    public static <T> CompletableFuture<T> supply(IContext ctx, PoolKind kind,
                                                  Supplier<T> work) {
        return CompletableFuture.supplyAsync(Objects.requireNonNull(work, "work"),
                ctx.pool(kind));
    }

    private static void await(Future<?> future) {
        try {
            future.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CompletionException(e);
        } catch (ExecutionException e) {
            throw rethrow(e.getCause());
        }
    }

    private static RuntimeException rethrow(Throwable error) {
        if (error instanceof RuntimeException runtime) {
            throw runtime;
        }
        if (error instanceof Error fatal) {
            throw fatal;
        }
        throw new CompletionException(error);
    }
}
