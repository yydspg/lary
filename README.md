# lary — Java 25 DAG + column engine

A standalone, single-project Java 25 implementation of the DAG framework and
column engine originally developed in the C++ **lark** repository.

- **DAG**: virtual threads (Project Loom) for coroutine-style blocking business
  code, Node / Op layers, aspects, registries, KV + arrow DSL, statistics, and
  optional timeout / cancellation with business fallback and repeated-timeout
  quarantine.
- **Column engine**: feed → compute → fetch, a business `ColumnNode` API,
  typed tensor operations (`OpTensor`, `FloatAddTensor`, `FloatMultiTensor`, …),
  hidden `ComputeNode` execution nodes, multi-dtype tensors, Q8_0 quantization,
  declarative DSL modules, monitoring.
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
│  ├─ column/      column engine (data, compute, op, exec, biz, backend, monitor)
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
  at startup and offers no flag to disable it. Gradle `JavaExec` output filters
  this line. The test JVM may still print it once because Gradle forwards
  test-worker startup stderr; test events and reports are unaffected. Direct
  `java` launches will print it.

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

./gradlew runDemo      # end-to-end layered demo (recommended)
./gradlew runSimple    # DAG Node layer
./gradlew runOp        # DAG Op + Aspect layer
./gradlew runNodes     # column business ColumnNode API (hidden ComputeNode)
./gradlew runColumn    # declarative column feed/compute/fetch + Vector API
```

The test suite mirrors the C++ suites: `test_dag.cpp`, `test_op.cpp`,
`test_op_dsl.cpp`, `test_column.cpp`, `test_engine.cpp`, plus Java-specific
`TimeoutTest`, `BusinessNodeTest`, `OpTensorTest` and `GraphPruningTest`.

## End-to-end demo

```sh
./gradlew runDemo
```

`LaryDemo` demonstrates the complete layering:

1. **Business layer** — `ColumnNode` graph with automatic column dependency
   wiring, `FeedStream`/`FetchStream`, weight and unit-conversion nodes,
   `@ColumnComponent` scanning, a deliberate failure + fallback, aggregate
   outputs and run statistics.
2. **Graph pruning / cycle detection** — compile only the branch needed for
   `score`, then detect a cyclic business graph.
3. **Tensor-op layer** — direct `FloatAddTensor` / `FloatMultiTensor` execution
   and runtime dtype dispatch through `TensorOps.create`.
4. **DAG timeout** — a slow node is interrupted, `Node.fallback` produces a
   degraded result, and the run returns quickly.

Sample output:

```text
SIMD: JDK Vector API (jdk.incubator.vector) {double=2, float=4, int=4, long=2}

=== 1. Business ColumnNode pipeline ===
user     = [110, 120, 130, 140, 150, 160]
score    = [220, 240, 260, 280, 300, 320]
mask     = [0, 0, 1, 1, 1, 1]
user_li  = [1100, 1200, 1300, 1400, 1500, 1600]
weighted = [2200, 2400, 2600, 2800, 3000, 3200]
bonus    = [2201, 2401, 2601, 2801, 3001, 3201]
high     = [260, 280, 300, 320]
total    = 1620.0
mean     = 270.0
max      = 320.0
fallback output = 8.0
fallback events = 1

=== 2. Graph pruning + cycle detection ===
registered nodes = 4, compiled nodes = 2
pruned score = 340.0
cycle detected: ColumnGraph: dependency cycle detected

=== 3. Typed OpTensor layer ===
FloatAddTensor   sum     = [11.0, 22.0, 33.0]
FloatMultiTensor product = [10.0, 40.0, 90.0]
dispatched add: dtype=float64, values=[1.5, 3.5, 5.5]

=== 4. DAG timeout + business fallback ===
status    = fallback
fallback  = true
elapsed   = 87ms
```

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

### Timeouts, fallback and quarantine

Timeouts are opt-in:

```java
try (Executor executor = new Executor(2, 4)) {
    executor.setTimeoutPolicy(TimeoutPolicy.builder()
            .defaultTimeout(Duration.ofMillis(500))
            .maxConsecutiveTimeouts(3)
            .quarantineDuration(Duration.ofMinutes(1))
            .repeatedAction(TimeoutAction.QUARANTINE) // or ABORT_GRAPH
            .build());
    executor.execute(graph, ctx);
}
```

- On timeout the worker is interrupted (configurable) and `Node.fallback(ctx,
  error)` runs with a `DagTimeoutException`. A successful fallback sets
  `NodeStatus.FALLBACK`; otherwise the node becomes `TIMED_OUT`.
- A node can override `timeout()` per instance.
- `TimeoutTracker` counts consecutive timeouts by node type. When the threshold
  is reached the key is quarantined, and later runs skip it quickly (still
  invoking fallback) until the quarantine expires or `timeoutTracker().reset`.
- `TimeoutAction.ABORT_GRAPH` cancels running bodies and drains the current run;
  inspect `executor.lastRunAborted()` / `lastAbortReason()`.
- `graphTimeout(...)` sets a whole-run deadline.
- `Op` has the same `timeout()` / `fallback()` hooks, handled by `OpExecutor`.

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

## Column engine — business Node API (recommended)

Business code subclasses `ColumnNode` and implements only `name()`,
`inputs()`, `outputs()` and `execute(ColumnContext)`. The framework derives
dependency edges from column names and compiles every business node into a
hidden `ComputeNode` execution node — business code never constructs or sees
one.

```java
final class ScoreNode extends ColumnNode {
    public String name() { return "score"; }
    public List<String> inputs() { return List.of("user"); }
    public List<String> outputs() { return List.of("score"); }

    public void execute(ColumnContext ctx) {
        ctx.output("score", Compute.mulScalar(ctx.input("user"), 2.0));
    }
}

try (NodePipeline pipeline = new NodePipeline()) {
    pipeline.addNode(new FetchUserNode())
            .addNode(new ScoreNode());
    pipeline.compile();

    pipeline.feed(List.of("user_id"), rows);
    pipeline.compute();
    List<Row> scores = pipeline.fetch(List.of("score"));
}
```

- Dependencies are implicit: if B reads a column A writes, A runs first.
- `pipeline.dependsOn("b", "a")` adds ordering without data flow.
- `ColumnGraph.compile()` rejects duplicate producers, unknown dependencies,
  self-dependencies and cycles.
- `ColumnNode.fallback(ColumnContext, Throwable)` produces a degraded result on
  failure; the framework sets the hidden ComputeNode status to `FALLBACK`.
- `ComputeNode` is the execution-layer node that actually runs the business
  adapter. `OpTensor` is the typed tensor-operation layer it wraps — e.g.
  `FloatAddTensor`, `FloatMultiTensor`, `DoubleAddTensor`, `IntAddTensor`.
  Neither is part of the public business API.

### Feed / fetch streams and input sources

```java
pipeline.feed(FeedStream.of(List.of("user_id"), rows));
List<Row> out = pipeline.fetch(FetchStream.of("score"));
pipeline.run(FeedStream.of(List.of("user_id"), rows),
             FetchStream.of("score"));
```

Each input can declare its provenance:

```java
@Override
public ColumnSource inputSource(String column) {
    return ColumnSource.FEED;   // or NODE / AUTO (default)
}
```

- `FEED` must come from row data; `NODE` must be produced by another node;
  `AUTO` uses a producer when present, otherwise feed.
- Compilation rejects `FEED` columns that a node produces, and `NODE` columns
  with no producer.
- `NodePipeline.compute()` rejects missing required feed columns before running.
- `FeedStream` and `FetchStream` make the row-in / row-out boundaries explicit.

Built-in business transformations:

```java
pipeline.addNodes(
    new ScaleNode("weighted", "score", "weighted_score", 0.3),
    new UnitConvertNode("fen_to_li", "amount_fen", "amount_li", 10.0));
```

Spring-style components (no Spring dependency):

```java
@ColumnComponent
final class WeightedScoreNode extends ColumnNode { ... }

pipeline.scan("com.example.features");
```

`ColumnComponentScanner` scans class directories and jars and instantiates
annotated nodes with a no-argument constructor.

### Graph pruning and cycle detection

```java
// Only keep nodes needed to produce "score"; unused branches are pruned.
pipeline.compile(Set.of("score"));

// Or prune and run in one call.
pipeline.run(FeedStream.of(names, rows), FetchStream.of("score"));
```

- `ColumnGraph.compile(requiredOutputs)` keeps the transitive producers of the
  requested columns and drops unreachable nodes.
- `FeedStream`/`FetchStream` `run(...)` prunes before execution.
- Cycles are always detected on the **full** registered graph, even if the
  cyclic branch would be pruned. Duplicate producers, self-dependencies and
  unknown explicit `dependsOn` targets are also rejected.
- `NodePipeline.compiledNodeCount()` reports the pruned node count;
  `nodeCount()` reports all registered nodes.

### Typed tensor operations

The operation layer is deliberately separate from execution nodes:

```java
TensorStore store = new TensorStore();
store.set("a", Tensor.fromFloats(1, 2, 3));
store.set("b", Tensor.fromFloats(10, 20, 30));

OpTensor add = new FloatAddTensor(List.of("a", "b"), List.of("sum"));
add.compute(new OpContext(store));       // dtype stays FLOAT32

OpTensor mul = new FloatMultiTensor(List.of("a", "b"), List.of("product"));
mul.compute(new OpContext(store));
```

`TensorOps.create(spec)` uses runtime dtype promotion to choose the typed
implementation (`AddTensor` → `IntAddTensor` / `LongAddTensor` /
`FloatAddTensor` / `DoubleAddTensor`); `MultiTensor`, `SubTensor` and
`DivTensor` follow the same pattern.

`Simd.summary()` reports the active Vector API species. The SIMD-backed kernels
live in `io.lark.column.compute.VectorOps`.

## Column engine — declarative Module / DSL API (advanced)

The declarative API remains available for prebuilt kernel graphs:

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

## Column architecture

| Package | Responsibility |
|---|---|
| `io.lark.column` | `DType`, `Tensor`, `TensorTable`, `TensorStore`, `Cell`, `Row`, `FeedFetch`, `Simd` |
| `io.lark.column.compute` | `Compute`, `VectorOps` |
| `io.lark.column.op` | `OpTensor`, `OpContext`, `OpSpec`, `TensorOps`; typed ops: `FloatAddTensor`, `FloatMultiTensor`, `DoubleAddTensor`, `IntAddTensor`, `LongMultiTensor`, … |
| `io.lark.column.exec` | `ComputeNode`, `ComputeGraph`, `ExecutionContext` — hidden execution layer |
| `io.lark.column.biz` | `ColumnNode`, `ColumnContext`, `ColumnGraph`, `NodePipeline`, `ColumnSource`, `FeedStream`, `FetchStream`, `ScaleNode`, `UnitConvertNode`, `ColumnComponent`, `ColumnComponentScanner` (business API); `Module`, `SubGraph`, `ColumnDsl`, `Ops`, `Pipeline`, `PipelineCompiler` (declarative API) |
| `io.lark.column.backend` | `Backend`, `CpuBackend`, `BackendFactory` |
| `io.lark.column.monitor` | `RunStats`, `ColumnStatsCollector` |

## Maintenance rule

Every behavior or API change must be committed together with:

1. an updated `README.md` (feature, usage, limitations);
2. an updated `.opencode/skills/java-port-sync/SKILL.md` (mapping, invariants,
   verification);
3. tests and, when user-facing, an example.

The sync skill is the operational checklist for mirroring changes from the
upstream C++ `lark` modules.

## Licensing

MIT, matching the upstream lark project. See `LICENSE`.
