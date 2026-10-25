// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples;

import io.lark.dag.DefaultContext;
import io.lark.dag.IContext;
import io.lark.dag.Op;
import io.lark.dag.OpAspect;
import io.lark.dag.OpExecutor;
import io.lark.dag.OpGraph;
import io.lark.dag.OpStatus;

/**
 * Op-layer quick start: business ops + a cross-cutting aspect.
 *
 * <p>Run with {@code ./gradlew :examples:runOp}.
 */
public final class OpPipeline {

    public static final class SourceOp extends Op {
        @Override
        public String name() {
            return "source";
        }

        @Override
        public void execute(IContext data) {
            data.set("value", data.get("seed", Integer.class));
        }
    }

    public static final class ScaleOp extends Op {
        @Override
        public String name() {
            return "scale";
        }

        @Override
        public void execute(IContext data) {
            data.set("scaled", data.get("value", Integer.class) * 10);
        }
    }

    /** Skips expensive ops when {@code cheap_mode} is set. */
    public static final class CheapModeAspect implements OpAspect {
        @Override
        public boolean shouldSkip(Op op, IContext data) {
            return data.has("cheap_mode", Boolean.class)
                    && data.get("cheap_mode", Boolean.class)
                    && op.name().equals("scale");
        }

        @Override
        public void onAfter(Op op, IContext data, OpStatus status) {
            System.out.println("aspect: " + op.name() + " -> " + status);
        }
    }

    private OpPipeline() {
    }

    public static void main(String[] args) {
        OpGraph graph = new OpGraph()
                .addOp(new SourceOp())
                .addOp(new ScaleOp())
                .dependsOn("source", "scale")
                .addAspect(new CheapModeAspect());

        DefaultContext data = new DefaultContext();
        data.set("seed", 7);
        data.set("cheap_mode", true);

        try (OpExecutor executor = new OpExecutor()) {
            executor.execute(graph, data);
            System.out.println("value = " + data.get("value", Integer.class));
            System.out.println("scaled present = "
                    + data.has("scaled", Integer.class));
            System.out.println("all succeeded = " + executor.allSucceeded());
        }
    }
}
