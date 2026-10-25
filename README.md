# lary — Java 25 DAG + column engine

A standalone Java 25 implementation of the DAG framework and column engine
originally developed in the C++ **lark** repository. The library and the demo
applications are separate Gradle modules.

- **DAG**: virtual threads (Project Loom) for coroutine-style blocking business
  code, Node / Op layers, aspects, registries, KV + arrow DSL, statistics, and
  optional timeout / cancellation with business fallback and repeated-timeout
  quarantine.
- **Column engine**: feed → compute → fetch, class-based typed columns
  (`LongColumn`, `DoubleColumn`, …), `ColumnNode` / `ColumnModule` /
  `ColumnStream` business API, typed tensor operations (`OpTensor`,
  `FloatAddTensor`, `FloatMultiTensor`, …), hidden `ComputeNode` execution nodes,
  multi-dtype tensors, Q8_0 quantization and monitoring.
- **SIMD**: column arithmetic, scalar broadcasting, comparisons, reductions and
  unary kernels use **`jdk.incubator.vector`**.

Package names remain `io.lark.*`; the project/repository is `lary`.

## Layout

```text
lary/
├─ build.gradle.kts              # library
├─ settings.gradle.kts           # includes :examples
├─ src/main/java/io/lark/
│  ├─ metric/      Event / Monitor abstraction
│  ├─ dag/         DAG framework (Node + Op layers)
│  └─ column/      column engine (data, compute, op, exec, biz, backend, monitor)
├─ src/test/java/io/lark/
│  ├─ metric/
│  ├─ dag/
│  └─ column/
└─ examples/                     # separate Gradle application module
   ├─ build.gradle.kts
   └─ src/main/java/io/lark/examples/
      ├─ LaryDemo.java           # compact end-to-end tour
      ├─ SimplePipeline.java
      ├─ OpPipeline.java
      ├─ BusinessNodeExample.java
      └─ risk/                   # realistic multi-class demo project
         ├─ RiskEngineDemo.java
         ├─ RiskResultPrinter.java
         ├─ columns/             # typed ColumnStream subclasses
         ├─ node/                # one file per business node
         ├─ module/              # one file per module
         └─ data/                # transaction repository
```

The root project is the reusable library; `:examples` is an independent
application module that depends on it.

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
./gradlew clean test :examples:build
./gradlew build :examples:build

./gradlew :examples:runDemo      # end-to-end layered demo (recommended)
./gradlew :examples:runSimple    # DAG Node layer
./gradlew :examples:runOp        # DAG Op + Aspect layer
./gradlew :examples:runNodes     # typed column-node quick start
./gradlew :examples:runRisk      # realistic payment risk-engine project
```

The test suite mirrors the C++ suites: `test_dag.cpp`, `test_op.cpp`,
`test_op_dsl.cpp`, `test_column.cpp`, `test_engine.cpp`, plus Java-specific
`TimeoutTest`, `ModulePipelineTest` and `OpTensorTest`. `DagTest` also covers
wide/deep ready-queue scheduling and array/varargs node definitions. The
`:examples` module has `RiskEngineTest` for the realistic risk graph.

## End-to-end demo

```sh
./gradlew :examples:runDemo
```

`LaryDemo` demonstrates the complete layering:

1. **Business layer** — typed `ColumnNode` / `ColumnModule` / `ColumnStream`
   graph with constructor-injected streams, `FeedStream`/`FetchStream`, weight
   and unit-conversion nodes, `@ColumnComponent` module scanning, a deliberate
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

=== 1. Business module / typed stream pipeline ===
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
./gradlew :examples:runRisk
```

The risk demo lives in the independent `:examples` module and is split like a
normal Java service, not packed into one demo class:

```text
io.lark.examples.risk
├─ RiskEngineDemo.java        # main / composition
├─ RiskResultPrinter.java     # console presentation
├─ columns/                   # one class per typed column
├─ node/                      # one class per business node
├─ module/                    # one class per module
└─ data/                      # transaction repository
```

The graph has three modules:

1. **TransactionFeatureModule** — raw transactions → `amount_yuan`,
   `txn_velocity`, `chargeback_ratio`, `weighted_amount` and `merchant_tier`.
   Merchant enrichment simulates an external service; if it fails, a
   conservative default tier is published through `ColumnNode.fallback`.
2. **RiskScoreModule** — `dependsOn(TransactionFeatureModule.class)`; computes
   `risk_score`, `risk_level` (LOW / MEDIUM / HIGH), `high_mask` and `approved`.
3. **RiskReportModule** — `dependsOn(RiskScoreModule.class)`; aggregates total,
   mean, max and high-risk count.

The modules are composed with constructor data wiring, typed columns and
class-reference dependencies:

```java
TransactionFeatureModule features = new TransactionFeatureModule();
RiskScoreModule risk = new RiskScoreModule(features);
RiskReportModule report = new RiskReportModule(risk);

try (ColumnPipeline pipeline = new ColumnPipeline(4)) {
    pipeline.add(features).add(risk).add(report);
    pipeline.compile();
    pipeline.feed(FeedStream.of(new ColumnStream<?>[]{
            features.userId(), features.amountFen(), features.txnCount(),
            features.chargebackCount(), features.merchantId()
    }, TransactionRepository.transactions()));
    pipeline.compute();

    List<Row> scored = pipeline.fetch(FetchStream.of(
            features.userId(), features.amount().out(), risk.score().out(),
            risk.band().level(), risk.approval().out(),
            features.merchantTier().out()));
    System.out.println(pipeline.fetchScalar(report.summary().total()));
}
```

A node is a small class with typed inputs and outputs; for example:

```java
public final class AmountToYuanNode
        extends UnaryNode<AmountFen, AmountYuan> {

    public AmountToYuanNode(AmountFen input) {
        super(input, AmountYuan.class);
    }

    @Override
    protected Tensor compute(ColumnContext ctx, Tensor amountFen) {
        return Compute.divScalar(amountFen, 100.0);
    }
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
        new NodeDef("rank", "fetch_user"));

DefaultContext ctx = new DefaultContext();
ctx.set("user_id", 7);

try (Executor executor = new Executor(2, 4)) {
    executor.execute(graph, ctx);
}
```

`NodeDef` accepts dependency arrays / varargs
(`new NodeDef("rank", "fetch_user")`); use `NodeDef.of(type, id, deps...)` when
a definition needs an explicit id. `Node.compute` / `Node.run` are blocking.

The executor uses a **dependency-counter ready queue**: nodes are started only
after their dependencies complete, and only running node bodies get virtual
threads. This avoids one parked dependency-waiting thread per node while keeping
independent nodes concurrent. `Schedule.run(ctx, PoolKind.COMPUTE, work)` is
the pool-hop equivalent of the C++ `co_await Schedule(...)`.

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

## Column engine — typed modules, nodes and streams

Business code does not use string outputs or positional feed indexes. Columns
are classes, nodes have typed input/output fields, and modules expose typed
fetch streams.

### Define columns as classes

```java
public final class AmountFen extends LongColumn {
}

public final class AmountYuan extends DoubleColumn {
}

// Physical / external names can be explicit; otherwise the class name is
// converted to snake_case (AmountYuan → amount_yuan).
public final class TxnCount24h extends LongColumn {
    public TxnCount24h() {
        super("txn_count_24h");
    }
}
```

`LongColumn`, `DoubleColumn`, `FloatColumn`, `IntColumn` and `BoolColumn` carry
the tensor dtype. `ColumnContext` validates the dtype on every read/write, and
typed feed columns are coerced to their declared dtype.

Classes are preferred over a global column enum: a node constructor can require
`AmountFen` specifically, so passing the wrong column is a compile error. An
enum can still be used as a schema factory that returns these classes if a
project prefers that style.

### Nodes define the business logic

```java
public final class AmountToYuanNode
        extends UnaryNode<AmountFen, AmountYuan> {

    public AmountToYuanNode(AmountFen input) {
        super(input, AmountYuan.class);
    }

    @Override
    protected Tensor compute(ColumnContext ctx, Tensor amountFen) {
        return Compute.divScalar(amountFen, 100.0);
    }
}
```

Use `UnaryNode<I, O>` and `BinaryNode<A, B, O>` for the common shapes, or
`SourceNode<O>` for a source. Custom nodes extend `ColumnNode` and keep typed
fields; outputs are declared with `fetch(SomeColumn.class)`:

```java
public final class RiskBandNode extends ColumnNode {
    private final RiskScore score;
    private final RiskLevel level = fetch(RiskLevel.class);
    private final HighMask highMask = fetch(HighMask.class);

    public RiskBandNode(RiskScore score) {
        super(score);
        this.score = score;
    }

    public void compute(ColumnContext ctx) {
        Tensor scores = ctx.read(score); // named, typed — no feeds()[0]
        // ...
    }
}
```

### Modules compose typed columns

```java
public final class TransactionFeatureModule extends ColumnModule {
    private final AmountFen amountFen = input(AmountFen.class);
    private final AmountToYuanNode amount = add(new AmountToYuanNode(amountFen));
    private final ScaleNode<WeightedAmount> weighted =
            add(new ScaleNode<>(amount.out(), WeightedAmount.class, 0.4));

    public TransactionFeatureModule() {
        output(amount.out(), weighted.out());
    }

    public AmountToYuanNode amount() { return amount; }
}
```

- Node ids are generated by the framework.
- Dependencies are **class/stream references** (`new AmountToYuanNode(amountFen)`),
  never node names.
- Modules depend on modules with `dependsOn(OtherModule.class)`; cycles are
  rejected.
- `ColumnNode.feeds()` / `fetches()` and module `nodes()`, `inputs()`,
  `outputs()`, `dependencies()` are arrays because their arity is fixed.

### Compile, feed, compute, fetch

```java
TransactionFeatureModule features = new TransactionFeatureModule();
try (ColumnPipeline pipeline = new ColumnPipeline()) {
    pipeline.add(features);             // or pipeline.scan("com.example")
    pipeline.compile();

    pipeline.feed(FeedStream.of(features.amountFen(), rows));
    pipeline.compute();

    List<Row> out = pipeline.fetch(FetchStream.of(features.amount().out()));
    double first = pipeline.fetchScalar(features.amount().out());
}
```

`FeedStream` / `FetchStream` carry `ColumnStream<?>[]`, so no names are needed
at the API boundary. Missing required feed columns are rejected before
execution.

### Built-in business transforms

```java
UnitConvertNode<AmountLi> li =
        new UnitConvertNode<>(amount.out(), AmountLi.class, 10.0); // fen → li
ScaleNode<WeightedAmount> weighted =
        new ScaleNode<>(li.out(), WeightedAmount.class, 0.3);      // weight
```

### Spring-style modules

`@ColumnComponent` marks a `ColumnModule`; the scanner supports class
directories and jars, and needs no Spring dependency:

```java
@ColumnComponent
public final class RiskModule extends ColumnModule { ... }

pipeline.scan("com.example.features");
RiskModule risk = pipeline.module(RiskModule.class);
```

### Graph pruning and cycle detection

```java
// Keep only nodes needed for a typed fetch stream.
pipeline.compile(FetchStream.of(features.amount().out()));

// Type-safe forms; names are an internal detail.
pipeline.run(FeedStream.of(features.amountFen(), rows),
             FetchStream.of(features.amount().out()));
```

- `compile(FetchStream)` keeps the transitive producers of requested module
  outputs and drops unreachable branches.
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
| `io.lark.column.biz` | typed business API: `ColumnStream<T>`, `LongColumn` / `DoubleColumn` / `FloatColumn` / `IntColumn` / `BoolColumn`, `ColumnNode`, `UnaryNode` / `BinaryNode` / `SourceNode`, `ColumnContext`, `ColumnModule`, `ColumnPipeline`, `FeedStream`, `FetchStream`, `ScaleNode`, `UnitConvertNode`, `ColumnComponent`, `ColumnComponentScanner` |
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
