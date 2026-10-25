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
      ├─ SimplePipeline.java     # DAG Node tour
      ├─ OpPipeline.java         # Op / Aspect tour
      └─ recsys/                 # search / recommendation metrics project
         ├─ RecSysDemo.java
         ├─ MetricResultPrinter.java
         ├─ columns/             # typed ColumnStream subclasses
         ├─ node/                # one file per business node
         ├─ module/              # one file per module
         └─ data/                # item-level metric repository
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

./gradlew :examples:runRecsys    # search/recommendation metric project
./gradlew :examples:runSimple    # DAG Node layer
./gradlew :examples:runOp        # DAG Op + Aspect layer
```

The test suite mirrors the C++ suites: `test_dag.cpp`, `test_op.cpp`,
`test_op_dsl.cpp`, `test_column.cpp`, `test_engine.cpp`, plus Java-specific
`TimeoutTest`, `ModulePipelineTest` and `OpTensorTest`. `DagTest` also covers
wide/deep ready-queue scheduling and array/varargs node definitions. The
`:examples` module has `RecSysMetricTest` for the search/recommendation graph.

## Search / recommendation metrics demo

```sh
./gradlew :examples:runRecsys
```

`RecSysDemo` is the realistic column-engine sample. It is deliberately split
into independent classes under `io.lark.examples.recsys`, not nested in one
demo class:

```text
recsys/
├─ RecSysDemo.java            # composition + main
├─ MetricResultPrinter.java   # presentation
├─ columns/                   # one class per metric column
├─ node/                      # one class per business node
├─ module/                    # one class per module
└─ data/                      # item-level metric repository
```

The graph computes search/ranking business metrics:

1. **FunnelMetricModule** — raw aggregates → `ctr`, `cvr`, `gmv`,
   `coupon_cost`, `buyagain_cost`, `coupon_roi`.
2. **RankingWeightModule** — combines the metrics into `weight`:
   ```text
   weight = 100*ctr + 50*cvr + 0.01*gmv + 0.5*coupon_roi
            - 0.5*buyagain_cost
   ```
3. **PerformanceSummaryModule** — aggregates `overall_ctr`, `overall_cvr`,
   `total_gmv`, `total_coupon_cost`, `overall_roi`, `overall_buyagain_cost`
   and `average_weight`.

Metric definitions:

| Metric | Formula |
|---|---|
| `ctr` | `clicks / impressions` |
| `cvr` | `orders / clicks` |
| `gmv` | `gmv_fen / 100` |
| `buyagain_cost` | `coupon_cost / buy_again_orders` |
| `coupon_roi` | `gmv / coupon_cost` |
| `weight` | `100*ctr + 50*cvr + 0.01*gmv + 0.5*coupon_roi - 0.5*buyagain_cost` |

All ratios return `0` when the denominator is zero.

There are **no user/item id columns**: the rows are already item-level
aggregates; ids are lookup keys, not inputs to columnar metric computation.

Composition and typed wiring:

```java
FunnelMetricModule metrics = new FunnelMetricModule();
RankingWeightModule ranking = new RankingWeightModule(metrics);
PerformanceSummaryModule summary =
        new PerformanceSummaryModule(metrics, ranking);

try (ColumnPipeline pipeline = new ColumnPipeline(4)) {
    pipeline.add(metrics).add(ranking).add(summary);
    pipeline.compile();
    pipeline.feed(FeedStream.of(new ColumnStream<?>[]{
            metrics.impressions(), metrics.clicks(), metrics.orders(),
            metrics.gmvFen(), metrics.couponCostFen(),
            metrics.buyAgainOrders()
    }, MetricsRepository.rows()));
    pipeline.compute();

    List<Row> rows = pipeline.fetch(FetchStream.of(
            metrics.impressions(), metrics.clicks(), metrics.ctr(),
            metrics.cvr(), metrics.gmv(), metrics.buyAgainCost(),
            metrics.couponRoi(), ranking.weight()));
    System.out.println(pipeline.fetchScalar(summary.overallRoi()));
}
```

A node is a small typed class, for example CTR:

```java
public final class CtrNode extends BinaryNode<Clicks, Impressions, Ctr> {

    public CtrNode(Clicks clicks, Impressions impressions) {
        super(clicks, impressions, Ctr.class);
    }

    @Override
    protected Tensor compute(ColumnContext ctx, Tensor clicks, Tensor impressions) {
        return Ratios.divide(clicks, impressions);
    }
}
```

Sample output:

```text
=== search / recommendation metrics ===
impressions    clicks      ctr      cvr          gmv  buyagain_cost coupon_roi     weight
1000               50   0.0500   0.1000       500.00        10.0000    50.0000    35.0000
2000              100   0.0500   0.2000      3000.00        12.5000    60.0000    68.7500
500                10   0.0200   0.1000        80.00         0.0000    40.0000    27.8000
1500              120   0.0800   0.2500      6000.00         8.0000    75.0000   114.0000
800                40   0.0500   0.2000      1200.00        10.0000    60.0000    52.0000
1200               90   0.0750   0.2000      2500.00         5.0000    83.3333    81.6667
overall_ctr            = 0.058571
overall_cvr            = 0.200000
total_gmv              = 13280.00
total_coupon_cost      = 192.00
overall_roi            = 69.1667
overall_buyagain_cost  = 8.3478
average_weight         = 63.2028
```

The run also emits per-module timing and CPU-pressure statistics through the
same monitoring layer as the DAG examples.

## DAG quick start

```java
NodeRegistry registry = new NodeRegistry();
registry.register("source", SourceNode::new);
registry.register("double", DoubleNode::new);

Graph graph = new GraphBuilder(registry).build(
        new NodeDef("source"),
        new NodeDef("double", "source"));

DefaultContext ctx = new DefaultContext();
ctx.set("seed", 7);

try (Executor executor = new Executor(2, 4)) {
    executor.execute(graph, ctx);
}
```

`NodeDef` accepts dependency arrays / varargs
(`new NodeDef("double", "source")`); use `NodeDef.of(type, id, deps...)` when
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
        .addOp(new SourceOp())
        .addOp(new ScaleOp())
        .dependsOn("source", "scale")
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
public final class Impressions extends LongColumn {
}

public final class Clicks extends LongColumn {
}

public final class Ctr extends DoubleColumn {
}

// Physical / external names can be explicit; otherwise the class name is
// converted to snake_case (Ctr → ctr, CouponRoi → coupon_roi).
public final class BuyAgainCost extends DoubleColumn {
    public BuyAgainCost() {
        super("buyagain_cost");
    }
}
```

`LongColumn`, `DoubleColumn`, `FloatColumn`, `IntColumn` and `BoolColumn` carry
the tensor dtype. `ColumnContext` validates the dtype on every read/write, and
typed feed columns are coerced to their declared dtype.

Classes are preferred over a global column enum: a node constructor can require
`Clicks` or `Impressions` specifically, so passing the wrong metric is a
compile error. An enum can still be used as a schema factory that returns these
classes if a project prefers that style.

### Nodes define the business logic

```java
public final class CtrNode extends BinaryNode<Clicks, Impressions, Ctr> {

    public CtrNode(Clicks clicks, Impressions impressions) {
        super(clicks, impressions, Ctr.class);
    }

    @Override
    protected Tensor compute(ColumnContext ctx, Tensor clicks, Tensor impressions) {
        return Ratios.divide(clicks, impressions);
    }
}
```

Use `UnaryNode<I, O>` and `BinaryNode<A, B, O>` for the common shapes, or
`SourceNode<O>` for a source. Custom nodes extend `ColumnNode` and keep typed
fields; outputs are declared with `fetch(SomeColumn.class)`:

```java
public final class RankingWeightNode extends ColumnNode {
    private final Ctr ctr;
    private final Cvr cvr;
    private final Gmv gmv;
    private final CouponRoi couponRoi;
    private final BuyAgainCost buyAgainCost;
    private final Weight out = fetch(Weight.class);

    public RankingWeightNode(Ctr ctr, Cvr cvr, Gmv gmv,
                             CouponRoi couponRoi, BuyAgainCost buyAgainCost) {
        super(ctr, cvr, gmv, couponRoi, buyAgainCost);
        this.ctr = ctr;
        this.cvr = cvr;
        this.gmv = gmv;
        this.couponRoi = couponRoi;
        this.buyAgainCost = buyAgainCost;
    }

    public void compute(ColumnContext ctx) {
        // Business formula — no feeds()[0] or string output names.
        ctx.write(out, /* ... */);
    }
}
```

### Modules compose typed columns

```java
public final class FunnelMetricModule extends ColumnModule {
    private final Impressions impressions = input(Impressions.class);
    private final Clicks clicks = input(Clicks.class);
    private final CtrNode ctr = add(new CtrNode(clicks, impressions));

    public FunnelMetricModule() {
        output(ctr.out());
    }

    public Impressions impressions() { return impressions; }
    public Clicks clicks() { return clicks; }
    public Ctr ctr() { return ctr.out(); }
}
```

- Node ids are generated by the framework.
- Dependencies are **class/stream references** (`new CtrNode(clicks, impressions)`),
  never node names.
- Modules depend on modules with `dependsOn(OtherModule.class)`; cycles are
  rejected.
- `ColumnNode.feeds()` / `fetches()` and module `nodes()`, `inputs()`,
  `outputs()`, `dependencies()` are arrays because their arity is fixed.
- Rows contain metric data, not user/item ids. Ids are lookup keys; column
  computation operates on measurable business columns.

### Compile, feed, compute, fetch

```java
FunnelMetricModule metrics = new FunnelMetricModule();
try (ColumnPipeline pipeline = new ColumnPipeline()) {
    pipeline.add(metrics);              // or pipeline.scan("com.example")
    pipeline.compile();

    pipeline.feed(FeedStream.of(new ColumnStream<?>[]{
            metrics.impressions(), metrics.clicks(), metrics.orders(),
            metrics.gmvFen(), metrics.couponCostFen(), metrics.buyAgainOrders()
    }, rows));
    pipeline.compute();

    List<Row> out = pipeline.fetch(FetchStream.of(metrics.ctr()));
    double firstCtr = pipeline.fetchScalar(metrics.ctr());
}
```

`FeedStream` / `FetchStream` carry `ColumnStream<?>[]`, so no names are needed
at the API boundary. Missing required feed columns are rejected before
execution.

### Spring-style modules

`@ColumnComponent` marks a `ColumnModule`; the scanner supports class
directories and jars, and needs no Spring dependency:

```java
@ColumnComponent
public final class RecSysModule extends ColumnModule { ... }

pipeline.scan("com.example.features");
RecSysModule recsys = pipeline.module(RecSysModule.class);
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
| `io.lark.column.biz` | typed business API: `ColumnStream<T>`, `LongColumn` / `DoubleColumn` / `FloatColumn` / `IntColumn` / `BoolColumn`, `ColumnNode`, `UnaryNode` / `BinaryNode` / `SourceNode`, `ColumnContext`, `ColumnModule`, `ColumnPipeline`, `FeedStream`, `FetchStream`, `ColumnComponent`, `ColumnComponentScanner` |
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
