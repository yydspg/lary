# lary — Java 25 DAG + column engine

A standalone, single-project Java 25 implementation of the DAG framework and
column engine originally developed in the C++ **lark** repository.

- **DAG**: virtual threads (Project Loom) for coroutine-style blocking business
  code, Node / Op layers, aspects, registries, KV + arrow DSL, statistics.
- **Column engine**: feed → compute → fetch, multi-dtype tensors, Q8_0
  quantization, global graph compilation, DSL modules, monitoring.
- **SIMD**: column arithmetic, scalar broadcasting, comparisons, reductions and
  unary kernels use **`jdk.incubator.vector`**.

Package names remain `io.lark.*`; the project/repository is `lary`.

## Layout

```text
lary/
├─ build.gradle.kts
├─ settings.gradle.kts
├─ src/main/java/io/lark/
│  ├─ metric/      Event / Monitor abstraction
│  ├─ dag/         DAG framework (Node + Op layers)
│  ├─ column/      column engine (data, compute, exec, biz, backend, monitor)
│  └─ examples/    runnable pipelines
└─ src/test/java/io/lark/
   ├─ metric/
   ├─ dag/
   └─ column/
```

Everything is one Gradle project — no subprojects.

## Requirements

- JDK 25 (Temurin 25 verified)
- Gradle wrapper is checked in (Gradle 9.8.2)
- Column kernels require the incubator Vector API. The build adds
  `--add-modules jdk.incubator.vector` for compilation, tests and JavaExec runs.
  The JVM prints `WARNING: Using incubator modules: jdk.incubator.vector` once
  at startup and offers no flag to disable it. Gradle `JavaExec` output is
  filtered for this line; tests hide raw test-process streams (results remain in
  the reports). Direct `java` launches will still print it.

If JDK 25 is not your default:

```sh
export JAVA_HOME=/path/to/jdk-25
./gradlew test
```

Or configure installed JDKs:

```properties
# ~/.gradle/gradle.properties
org.gradle.java.installations.paths=/path/to/jdk-25
```

## Build / test / run

```sh
./gradlew clean test
./gradlew build

./gradlew runSimple     # DAG Node layer
./gradlew runOp         # DAG Op + Aspect layer
./gradlew runColumn     # column feed/compute/fetch + Vector API
```

The test suite mirrors the C++ suites: `test_dag.cpp`, `test_op.cpp`,
`test_op_dsl.cpp`, `test_column.cpp`, `test_engine.cpp`.

## DAG quick start

```java
NodeRegistry registry = new NodeRegistry();
registry.register("fetch_user", FetchUserNode::new);
registry.register("rank", RankNode::new);

Graph graph = new GraphBuilder(registry).build(
        new NodeDef("fetch_user"),
        new NodeDef("rank", List.of("fetch_user")));

DefaultContext ctx = new DefaultContext();
ctx.set("user_id", 7);

try (Executor executor = new Executor(2, 4)) {
    executor.execute(graph, ctx);
}
```

`Node.compute` / `Node.run` are blocking; each node gets a virtual thread.
`Schedule.run(ctx, PoolKind.COMPUTE, work)` is the pool-hop equivalent of the
C++ `co_await Schedule(...)`.

## Op-layer quick start

```java
OpGraph graph = new OpGraph()
        .addOp(new FetchUserOp())
        .addOp(new ScoreUserOp())
        .dependsOn("fetch_user", "score_user")
        .addAspect(new CheapModeAspect());

try (OpExecutor executor = new OpExecutor()) {
    executor.execute(graph, data);
}
```

## Column engine quick start

```java
Module feature = new Module("feature");
feature.input("x").input("y")
        .output("score").output("total")
        .fromDsl(
                "score = x * 2 + y\n"
                        + "total = sum(score)\n");

try (Pipeline pipeline = new Pipeline()) {
    pipeline.addModule(feature);
    pipeline.compile();

    pipeline.feed(List.of("x", "y"), List.of(
            Row.of(Cell.of(1), Cell.of(10)),
            Row.of(Cell.of(5), Cell.of(50))));
    pipeline.compute();

    double total = pipeline.fetchScalar("total");
}
```

`Simd.summary()` reports the active Vector API species. The SIMD-backed kernels
live in `io.lark.column.compute.VectorOps`.

## Column architecture

| Package | Responsibility |
|---|---|
| `io.lark.column` | `DType`, `Tensor`, `TensorTable`, `TensorStore`, `Cell`, `Row`, `FeedFetch`, `Simd` |
| `io.lark.column.compute` | `Compute`, `VectorOps` |
| `io.lark.column.exec` | `OpSpec`, `TensorOp`, `TensorOps`, `ComputeNode`, `ComputeGraph`, `ExecutionContext` |
| `io.lark.column.biz` | `Module`, `SubGraph`, `ColumnDsl`, `Ops`, `Pipeline`, `PipelineCompiler` |
| `io.lark.column.backend` | `Backend`, `CpuBackend`, `BackendFactory` |
| `io.lark.column.monitor` | `RunStats`, `ColumnStatsCollector` |

## Licensing

MIT, matching the upstream lark project. See `LICENSE`.
