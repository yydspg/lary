# lary — Java 25 DAG + column engine

A standalone, single-project Java 25 implementation of the DAG framework and
column engine originally developed in the C++ **lark** repository.

- **DAG**: virtual threads (Project Loom) for coroutine-style blocking business
  code, Node / Op layers, aspects, registries, KV + arrow DSL, statistics, and
  optional timeout / cancellation with business fallback and repeated-timeout
  quarantine.
- **Column engine**: feed → compute → fetch, a business `ColumnNode` /
  `ColumnModule` / `ColumnStream` API, typed tensor operations (`OpTensor`,
  `FloatAddTensor`, `FloatMultiTensor`, …), hidden `ComputeNode` execution nodes,
  multi-dtype tensors, Q8_0 quantization and monitoring.
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
./gradlew runNodes     # column module / stream business API
./gradlew runRisk      # realistic payment risk-engine example
```

The test suite mirrors the C++ suites: `test_dag.cpp`, `test_op.cpp`,
`test_op_dsl.cpp`, `test_column.cpp`, `test_engine.cpp`, plus Java-specific
`TimeoutTest`, `ModulePipelineTest` and `OpTensorTest`.

## End-to-end demo

```sh
./gradlew runDemo
```

`LaryDemo` demonstrates the complete layering:

1. **Business layer** — `ColumnNode` / `ColumnModule` / `ColumnStream` graph
   with constructor-injected streams, `FeedStream`/`FetchStream`, weight and
   unit-conversion nodes, `@ColumnComponent` module scanning, a deliberate
   failure + fallback, aggregate outputs and run statistics.
2. **Graph pruning / cycle detection** — compile only the branch needed for
   `score`, then detect a cyclic module graph.
3. **Tensor-op layer** — direct `FloatAddTensor` / `FloatMultiTensor` execution
   and runtime dtype dispatch through `TensorOps.create`.
4. **DAG timeout** — a slow node is interrupted, `Node.fallback` produces a
   degraded result, and the run returns quickly.

Sample output:

```text
SIMD: JDK Vector API (jdk.incubator.vector) {double=2, float=4, int=4, long=2}

=== 1. Business module / stream pipeline ===
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
registered nodes = 3, compiled nodes = 2
pruned score = 340.0
module cycle detected: ColumnPipeline: module dependency cycle detected

=== 3. Typed OpTensor layer ===
FloatAddTensor   sum     = [11.0, 22.0, 33.0]
FloatMultiTensor product = [10.0, 40.0, 90.0]
dispatched add: dtype=float64, values=[1.5, 3.5, 5.5]

=== 4. DAG timeout + business fallback ===
status    = fallback
fallback  = true
elapsed   = 87ms
```

## Realistic example — payment risk engine

```sh
./gradlew runRisk
```

`RiskEngineExample` models a production-shaped pipeline with three modules:

1. **TransactionFeatureModule** — raw transactions → `amount_yuan`,
   `txn_velocity`, `chargeback_ratio`, `weighted_amount` and `merchant_tier`.
   Merchant enrichment simulates an external service; if it fails, a
   conservative default tier is published through `ColumnNode.fallback`.
2. **RiskScoreModule** — `dependsOn(TransactionFeatureModule.class)`; computes
   `risk_score`, `risk_level` (LOW / MEDIUM / HIGH), `high_mask` and `approved`.
3. **RiskReportModule** — `dependsOn(RiskScoreModule.class)`; aggregates total,
   mean, max and high-risk count.

The modules are composed with constructor data wiring and class-reference
dependencies:

```java
TransactionFeatureModule features = new TransactionFeatureModule();
RiskScoreModule risk = new RiskScoreModule(features);
RiskReportModule report = new RiskReportModule(risk);

try (ColumnPipeline pipeline = new ColumnPipeline(4)) {
    pipeline.add(features).add(risk).add(report);
    pipeline.compile();
    pipeline.feed(FeedStream.of(new ColumnStream[]{
            features.userId, features.amountFen, features.txnCount,
            features.chargebackCount, features.merchantId
    }, rows));
    pipeline.compute();

    List<Row> scored = pipeline.fetch(FetchStream.of(
            features.userId, features.amount.out, risk.score.out,
            risk.band.level, risk.approval.out, features.tier.out));
    System.out.println(pipeline.fetchScalar("total_risk_score"));
}
```

Sample output:

```text
=== payment risk engine ===
user      amount_yuan   risk_score    level   approved   tier
1001          1200.00       486.67        1          1      1
1002          8500.00      3418.33        2          0      1
1003            50.00        25.42        0          1      1
1004         25000.00     10024.17        2          0      1
1005           450.00       186.25        0          1      1
1006         15000.00      6028.33        2          0      1
total_risk_score = 20169.166666666664
mean_risk_score  = 3361.5277777777774
max_risk_score   = 10024.166666666666
high_risk_count  = 3.0
fallback events  = 1
```

The run also emits per-module timing and CPU-pressure statistics through the
same monitoring layer used by `LaryDemo`.

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

## Column engine — modules, nodes and streams

Business nodes have **no `name()` / `inputs()` / `outputs()` overrides**:
the framework generates a unique node id, inputs are `ColumnStream` references
passed to the constructor, and outputs are `ColumnStream` fields.

```java
final class ScoreNode extends ColumnNode {
    final ColumnStream out = output("score");

    ScoreNode(ColumnStream user) {
        super(user);
    }

    public void compute(ColumnContext ctx) {
        ctx.write(out, Compute.mulScalar(ctx.read(feeds()[0]), 2.0));
    }
}
```

A `ColumnModule` groups nodes and exposes its fetch outputs:

```java
@ColumnComponent
final class FeatureModule extends ColumnModule {
    final ColumnStream userId = input("user_id");
    final FetchUserNode fetch = add(new FetchUserNode(userId));
    final ScoreNode score = add(new ScoreNode(fetch.out));

    {
        output(fetch.out, score.out);
    }
}
```

Wire and run the module:

```java
FeatureModule feature = new FeatureModule();
try (ColumnPipeline pipeline = new ColumnPipeline()) {
    pipeline.add(feature);                 // or pipeline.scan("com.example")
    pipeline.compile();

    pipeline.feed(FeedStream.of(feature.userId, rows));
    pipeline.compute();

    List<Row> out = pipeline.fetch(FetchStream.of(feature.score.out));
}
```

- Dependencies are **class/stream references** (`new ScoreNode(fetch.out)`),
  never node names.
- `ColumnNode.feeds()` / `fetches()` return arrays. `ColumnModule.nodes()`,
  `inputs()`, `outputs()` and `dependencies()` also return arrays; arity is
  fixed after construction.
- A module depends on another module with
  `dependsOn(OtherModule.class)`; module cycles are rejected.
- `ColumnPipeline` builds the hidden `ComputeNode` graph internally; the
  business API never imports execution or tensor-op internals.

### Feed / fetch streams

`FeedStream` is the row-data input boundary; `FetchStream` is the output
boundary. Both carry `ColumnStream[]`:

```java
pipeline.feed(FeedStream.of(feature.userId, rows));
List<Row> out = pipeline.fetch(FetchStream.of(feature.score.out));
pipeline.run(FeedStream.of(feature.userId, rows),
             FetchStream.of(feature.score.out));
```

Missing required feed columns are rejected before execution.

### Built-in business transforms

```java
UnitConvertNode li = new UnitConvertNode(fetch.out, "amount_li", 10.0); // fen → li
ScaleNode weighted = new ScaleNode(li.out, "weighted", 0.3);           // weight
```

### Spring-style modules

`@ColumnComponent` marks a `ColumnModule`; the scanner supports class
directories and jars, and needs no Spring dependency:

```java
@ColumnComponent
final class RiskModule extends ColumnModule { ... }

pipeline.scan("com.example.features");
RiskModule risk = pipeline.module(RiskModule.class);
```

### Graph pruning and cycle detection

```java
// Keep only nodes needed for the "score" module output.
pipeline.compile(Set.of("score"));

// Or prune as part of run.
pipeline.run(FeedStream.of(feature.userId, rows),
             FetchStream.of(feature.score.out));
```

- `compile(requiredOutputs)` keeps the transitive producers of requested
  module outputs and drops unreachable branches.
- Module dependency cycles and node graph cycles are both detected.
- The full graph is validated before pruning, so a cyclic unused branch still
  fails compilation.
- `nodeCount()` is total registered nodes; `compiledNodeCount()` is the pruned
  execution graph size.

### Typed tensor operations

The operation layer is separate from execution nodes:

```java
TensorStore store = new TensorStore();
store.set("a", Tensor.fromFloats(1, 2, 3));
store.set("b", Tensor.fromFloats(10, 20, 30));

OpTensor add = new FloatAddTensor(List.of("a", "b"), List.of("sum"));
add.compute(new OpContext(store));       // dtype stays FLOAT32

OpTensor mul = new FloatMultiTensor(List.of("a", "b"), List.of("product"));
mul.compute(new OpContext(store));
```

`OpTensor` stores inputs/outputs as arrays. `TensorOps.create(spec)` uses
runtime dtype promotion to choose the typed implementation (`AddTensor` →
`IntAddTensor` / `LongAddTensor` / `FloatAddTensor` / `DoubleAddTensor`).

`Simd.summary()` reports the active Vector API species. SIMD kernels live in
`io.lark.column.compute.VectorOps`.

## Column architecture

| Package | Responsibility |
|---|---|
| `io.lark.column` | `DType`, `Tensor`, `TensorTable`, `TensorStore`, `Cell`, `Row`, `FeedFetch`, `Simd` |
| `io.lark.column.compute` | `Compute`, `VectorOps` |
| `io.lark.column.op` | `OpTensor`, `OpContext`, `OpSpec`, `TensorOps`; typed ops: `FloatAddTensor`, `FloatMultiTensor`, `DoubleAddTensor`, `IntAddTensor`, `LongMultiTensor`, … |
| `io.lark.column.exec` | `ComputeNode`, `ComputeGraph`, `ExecutionContext` — hidden execution layer |
| `io.lark.column.biz` | `ColumnNode`, `ColumnContext`, `ColumnStream`, `ColumnModule`, `ColumnPipeline`, `FeedStream`, `FetchStream`, `ScaleNode`, `UnitConvertNode`, `ColumnComponent`, `ColumnComponentScanner` — business API |
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
