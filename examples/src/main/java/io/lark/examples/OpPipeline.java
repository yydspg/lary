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

    public static final class FetchUserOp extends Op {
        @Override
        public String name() {
            return "fetch_user";
        }

        @Override
        public void execute(IContext data) {
            int id = data.get("user_id", Integer.class);
            data.set("user", "user_" + id);
        }
    }

    public static final class ScoreUserOp extends Op {
        @Override
        public String name() {
            return "score_user";
        }

        @Override
        public void execute(IContext data) {
            String user = data.get("user", String.class);
            data.set("score", user.length() * 10);
        }
    }

    /** Skips expensive ops when {@code cheap_mode} is set. */
    public static final class CheapModeAspect implements OpAspect {
        @Override
        public boolean shouldSkip(Op op, IContext data) {
            return data.has("cheap_mode", Boolean.class)
                    && data.get("cheap_mode", Boolean.class)
                    && op.name().equals("score_user");
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
                .addOp(new FetchUserOp())
                .addOp(new ScoreUserOp())
                .dependsOn("fetch_user", "score_user")
                .addAspect(new CheapModeAspect());

        DefaultContext data = new DefaultContext();
        data.set("user_id", 7);
        data.set("cheap_mode", true);

        try (OpExecutor executor = new OpExecutor()) {
            executor.execute(graph, data);
            System.out.println("user = " + data.get("user", String.class));
            System.out.println("score present = " + data.has("score", Integer.class));
            System.out.println("all succeeded = " + executor.allSucceeded());
        }
    }
}
