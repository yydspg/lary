// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

/**
 * Typed single-output business node.
 *
 * <p>{@code ModuleNode<O>} makes the output type part of the node type. It is
 * the preferred base for formula and ranking nodes; the older
 * {@link ColumnNode} remains available for multi-output custom nodes.
 */
@SuppressWarnings("this-escape")
public abstract class ModuleNode<O> extends ColumnNode {

    private ColumnStream<O> out;

    /** Wiring form for subclasses that resolve their schema after super(). */
    protected ModuleNode(ColumnStream<?>... feeds) {
        super(feeds);
    }

    protected ModuleNode(Column<O> output, ColumnStream<?>... feeds) {
        super(feeds);
        bindOutput(output);
    }

    /** Bind the single typed output once. */
    protected final void bindOutput(Column<O> output) {
        if (out != null) {
            throw new IllegalStateException(
                    "ModuleNode output is already bound");
        }
        out = fetch(output);
    }

    /** The single typed output produced by this node. */
    public final ColumnStream<O> out() {
        if (out == null) {
            throw new IllegalStateException("ModuleNode output is not bound");
        }
        return out;
    }
}
