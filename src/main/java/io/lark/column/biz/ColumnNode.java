// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Business computation node.
 *
 * <p>No {@code name()} / {@code inputs()} / {@code outputs()} overrides: the
 * framework generates a unique node id, inputs are {@link ColumnStream}s passed
 * to the constructor, and outputs are {@link ColumnStream}s created with
 * {@link #output(String)}. Dependencies are class/stream references, not node
 * names.
 *
 * <p>Feeds and fetches are exposed as arrays because their arity is fixed once
 * the node is constructed.
 *
 * <pre>{@code
 * final class ScoreNode extends ColumnNode {
 *     final ColumnStream out = output("score");
 *
 *     ScoreNode(ColumnStream feed) {
 *         super(feed);
 *     }
 *
 *     public void compute(ColumnContext ctx) {
 *         ctx.write(out, Compute.mulScalar(ctx.read(feeds()[0]), 2.0));
 *     }
 * }
 * }</pre>
 */
public abstract class ColumnNode {

    private final String id = NodeIds.next(getClass());
    private final ColumnStream[] feeds;
    private final List<ColumnStream> fetchBuilder = new ArrayList<>();
    private ColumnStream[] fetches;

    protected ColumnNode(ColumnStream... feeds) {
        this.feeds = feeds == null ? new ColumnStream[0] : feeds;
        for (ColumnStream feed : this.feeds) {
            Objects.requireNonNull(feed, "feed stream");
        }
    }

    public final String id() {
        return id;
    }

    /** Fixed input streams, in constructor order. */
    public final ColumnStream[] feeds() {
        return feeds;
    }

    /** Declared output streams, in declaration order. */
    public final ColumnStream[] fetches() {
        if (fetches == null) {
            fetches = fetchBuilder.toArray(ColumnStream[]::new);
        }
        return fetches;
    }

    /** Declare an output stream; call from a field initializer or constructor. */
    protected final ColumnStream output(String name) {
        ColumnStream stream = new ColumnStream(name, this);
        fetchBuilder.add(stream);
        fetches = null;
        return stream;
    }

    /** Business computation; read/write streams via {@link ColumnContext}. */
    public abstract void compute(ColumnContext ctx);

    /**
     * Optional degradation hook invoked when {@link #compute} fails.
     *
     * @return {@code true} if a usable degraded result was written
     */
    public boolean fallback(ColumnContext ctx, Throwable error) {
        return false;
    }

    @Override
    public String toString() {
        return id + Arrays.toString(fetches());
    }
}
