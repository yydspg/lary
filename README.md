# lary — Java 25 DAG + column engine

中文文档：[README.zh-CN.md](README.zh-CN.md)

A standalone Java 25 implementation of the DAG framework and column engine
originally developed in the C++ **lark** repository. The library and the demo
applications are separate Gradle modules.

- **DAG**: virtual threads (Project Loom) for coroutine-style blocking business
  code, Node / Op layers, aspects, registries, KV + arrow DSL, statistics, and
  optional timeout / cancellation with business fallback and repeated-timeout
  quarantine.
- **Column engine**: feed → compute → fetch, `Column<T>` schema definitions and
  typed `ColumnStream<T>` handles, `ModuleNode<O>` / `ColumnModule` business API,
  graph ops (`OpSpec`) plus executable tensor kernels (`TensorKernel`), hidden
  `ComputeNode` execution nodes, multi-dtype tensors with 1–3-D shapes,
  zero-tensor nodes, Q8_0 quantization and monitoring. Legacy `OpTensor` /
  `FloatAddTensor` APIs remain as compatibility adapters.
- **SIMD**: column arithmetic, scalar broadcasting, comparisons, reductions and
  unary kernels use **`jdk.incubator.vector`**.
- **Spring Boot (optional)**: the separate `:spring` module provides auto-configuration,
  `@ColumnComponent` bean discovery, constructor injection between modules and a
  `ColumnPipelineFactory`. The core library and `:examples` stay dependency-free.

Package names remain `io.lark.*`; the project/repository is `lary`.

## Layout

```text
lary/
├─ build.gradle.kts              # library
├─ settings.gradle.kts           # includes :examples and :spring
├─ src/main/java/io/lark/
│  ├─ metric/      Event / Monitor abstraction
│  ├─ dag/         DAG framework (Node + Op layers)
│  ├─ rank/        greedy / beam / backtracking slate ranking
│  └─ column/
│     ├─ tensor/    Tensor / TensorShape / store / FeedFetch / TensorKernel / TensorKernels
│     ├─ data/      Cell / Row
│     ├─ compute/   Compute / VectorOps / Simd
│     ├─ op/        OpSpec + legacy OpTensor/TensorOps
│     ├─ exec/      ComputeNode / ComputeGraph
│     ├─ biz/       ModuleNode / Formula / Module / Pipeline
│     ├─ backend/   backend registry
│     └─ monitor/   RunStats / ColumnStatsCollector
├─ src/test/java/io/lark/
│  ├─ metric/
│  ├─ dag/
│  ├─ rank/
│  └─ column/
├─ examples/                     # separate Gradle application module
│  ├─ build.gradle.kts
│  └─ src/main/java/io/lark/examples/
│     ├─ SimplePipeline.java     # DAG Node tour
│     ├─ OpPipeline.java         # Op / Aspect tour
│     ├─ rank/                   # greedy / beam / backtracking demo
│     ├─ search/                 # typed search schema + module + demo
│     │  ├─ SearchDemo.java      # request model, rows and CLI
│     │  ├─ SearchSchema.java    # Column<T> definitions
│     │  ├─ SearchModule.java    # feeds, operators, formula, PV top-k
│     │  └─ SearchResult.java    # typed result records
│     └─ demo/                   # complete end-to-end demo + benchmark
│        ├─ FullRankingDemo.java
│        └─ FullRankingBenchmark.java
└─ spring/                       # optional Spring Boot integration
   ├─ build.gradle.kts
   └─ src/main/java/io/lark/spring/
      ├─ EnableLaryColumns.java
      ├─ ColumnComponentRegistrar.java
      ├─ LaryColumnAutoConfiguration.java
      └─ ColumnPipelineFactory.java
```

The root project is the reusable library; `:examples` is an independent
application module that depends on it. `:spring` depends on the library and
Spring Boot but is not pulled in by either of them.

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
./gradlew build :examples:build :spring:build

./gradlew :examples:runSearch    # row feed -> PV top-k search graph
./gradlew :examples:runRank      # greedy / beam slot ranking
./gradlew :examples:runFullDemo  # DAG + column + ranking end to end
./gradlew :examples:runFullBenchmark # warmed-up throughput for the full demo
./gradlew :examples:runSimple    # DAG Node layer
./gradlew :examples:runOp        # DAG Op + Aspect layer
./gradlew :spring:runSpringSearch # auto-discovered search graph through Spring Boot
```

The test suite mirrors the C++ suites: `test_dag.cpp`, `test_op.cpp`,
`test_op_dsl.cpp`, `test_column.cpp`, `test_engine.cpp`, plus Java-specific
`TimeoutTest`, `ModulePipelineTest` and `OpTensorTest`. `DagTest` and `OpTest`
also cover wide/deep ready-queue scheduling and array/varargs node definitions;
`ModulePipelineTest` covers fluent/columnar boundaries, plan reuse after a
failed recompile, module snapshots, and retryable module definitions;
`ExecutionGraphTest` covers the separated `OpSpec` + `TensorKernel` path and
the compatibility `OpTensor` path. `SequentialRankerTest` and
`BeamSearchRankerTest` cover admission lifecycles, indexed scoring and stable
sorting. The `:examples` module has `SearchDemoTest` and
`FullRankingDemoTest` for the row-fed search/top-k graph and the complete
DAG + column + ranking path; `:spring` has Spring context tests for scanning,
constructor injection and the prototype pipeline.

## End-to-end ranking demo

```sh
./gradlew :examples:runFullDemo
```

`FullRankingDemo` is the complete demo. It runs one request through all three
layers and prints every stage's statistics and output:

1. **DAG (`Node`)** — `load_request` and `load_config` run concurrently, then
   `validate` depends on both and publishes a typed `Prepared` value. The run
   reports per-node timing through `StatsCollector`.
2. **Column (`ColumnModule`)** — `SearchModule` feeds raw request rows, computes
   CTR/CVR/GMV/coupon/retarget/delay columns, applies the experiment-aware
   `F` formula, applies `score * perturbation + originalScore`, and selects the
   top-k goods per `pv_id` with `TopKOp`.
3. **Ranking (`rank`)** — greedy `SequentialRanker` and
   `BeamSearchRanker` fill the final slate from the column-selected candidates
   with category gap, brand quota and budget admissions. Both use
   `IndexedSlotScorer`, so candidate objects never move and no intermediate
   candidate list is allocated per slot.

```java
FullRankingDemo.Result result = FullRankingDemo.run(300);

result.prepared().dagStats();       // Node-stage timing + statuses
result.column().selectedCount();    // PV top-k selection size
result.column().candidates();       // selected goods, sorted by base score
result.greedy();                    // RankingResult<ScoredGoods>
result.beam();                      // RankingResult<ScoredGoods>
```

The search graph itself is a normal typed module:

```java
SearchModule module = new SearchModule(topK, workers);

try (ColumnPipeline pipeline = new ColumnPipeline(workers)) {
    pipeline.add(module)
            .setExperiment(request.context().experiment())
            .executeTensors(module.feedTensorMap(SearchDemo.rows(request)),
                    FetchStream.of(
                            module.shared(SearchSchema.ADJUSTED_SCORE.doubleColumn()),
                            module.shared(SearchSchema.SELECTED.longColumn())),
                    ColumnRunContext.of(request.context()));

    Tensor adjusted = pipeline.fetchTensor(
            SearchSchema.ADJUSTED_SCORE.columnName());
    Tensor selected = pipeline.fetchTensor(
            SearchSchema.SELECTED.columnName());
}
```

A typical run prints the DAG waterfall, column phase timings and CPU pressure,
then the greedy and beam slates with their final scores and deltas. The default
run uses 300 candidates and a top-30 slate.

### Steady-state benchmark

A single `RunStats` sample includes JIT compilation, class loading and module
construction. For throughput, use the warmed-up benchmark:

```sh
./gradlew :examples:runFullBenchmark
./gradlew :examples:runFullBenchmark \
    -Plary.full.candidates=2000 -Plary.full.warmup=20 -Plary.full.iterations=50
```

`FullRankingBenchmark` discards warm-up iterations, then reports median, p95,
min and max for the total request, the DAG stage, the column stage and both
rankers. It creates a fresh `ColumnPipeline` and `SearchModule` per request, so
the numbers include graph build/compile cost. Defaults are 1,000 candidates,
8 warm-up iterations and 20 measured iterations. Compare configurations on the
same machine and JDK rather than treating absolute numbers as a hardware
baseline.

See `examples/README.md` for the full demo layout.

## Tensor-aware search / retargeting demo

```sh
./gradlew :examples:runSearch
```

`SearchDemo` models a search request end to end:

1. `SearchContext` carries `pvId`, `userId`, `query`, `topK` and an
   `ExperimentContext`.
2. `SearchDemo.simulate(context, 1_000)` generates a `GoodsList` of `GoodsDO`
   rows with impressions, clicks, orders, GMV, coupon cost, retargeting
   counters and delay counters. `GoodsDO` also exposes row-level `ctr()`,
   `cvr()`, `gmv()` and `couponCost()` helpers; the graph recomputes metrics
   from raw feed columns.
3. `SearchDemo.rows(request)` flattens each goods row into a typed
   `SearchRow(pvId, goods)`; `SearchModule` maps them to primitive tensors with
   `feed(Column<T>, getter)`.
4. The graph computes metrics, composes the experiment-aware ranking score,
   applies `score * perturbation + originalScore`, and selects the top 30 goods
   for each `pv_id`.
5. The selected columns are fetched back as rows and mapped to typed
   `SearchResult.RankedGoods` items.

```java
SearchDemo.SearchContext context = SearchDemo.sampleContext();
SearchDemo.SearchRequest request = SearchDemo.simulate(context, 1_000);

SearchResult result = new SearchModule(context.topK(), 4).search(request);
for (SearchResult.RankedGoods goods : result.ranked()) {
    System.out.printf("%d. goods=%d score=%.4f%n",
            goods.rank(), goods.goodsId(), goods.adjustedScore());
}
```

`SearchModule` is a single `@ColumnComponent` module that declares every feed,
node, formula and output. With Spring, `@EnableLaryColumns` discovers it and
`ColumnPipelineFactory` supplies a fresh mutable pipeline per request; the
application does not hand-assemble module lists:

```java
@SpringBootApplication(proxyBeanMethods = false)
@EnableLaryColumns(basePackages = "io.lark.examples.search")
class App {
}
```

The Spring demo obtains the module and factory and calls
`module.search(pipeline, request)`; it runs with
`./gradlew :spring:runSpringSearch`.

The graph outputs are ordinary typed columns:

| Output | Formula | Shape |
|---|---|---|
| `ctr` | `clicks / impressions` | 1-D |
| `cvr` | `orders / clicks` | 1-D |
| `ctcvr` | `ctr * cvr` | 1-D |
| `gmv` | `gmv_fen / 100` | 1-D |
| `buyagain` | `buy_again_orders / orders` | 1-D |
| `rebuy_same_loss` | `1 - buyagain` | 1-D |
| `couponroi` | `gmv / coupon_cost` | 1-D |
| `retargetAd` | click share, cost share and ROI per row | 2-D `[rows, 3]` |
| `delay` | order/GMV on-time and delayed shares | 3-D `[rows, 2, 2]` |
| `ranking_score` | `100*ctr + 50*cvr + 0.01*gmv + 0.5*couponroi - 0.5*rebuy_same_loss` | 1-D |
| `adjusted_score` | `ranking_score * perturbation + original_score` | 1-D |
| `selected` | top-`k` flag per `pv_id` | 1-D INT64 |

PV-level integration is a regular business node: `TopKOp` receives the score
column and the `pv_id` group column, then emits an INT64 selection flag per
row. The demo runs one PV with 1,000 candidates; `SearchDemoTest` feeds two PVs
with 1,000 goods each and verifies 30 selected rows per PV. Ties are broken by
original row order, so repeated runs are deterministic.

The demo prints the selected top-30 goods with their adjusted scores, then
fetches the retargeting and delay tensors with `fetchTensor(...)` so their
shapes and row-major values are visible.

### Formula and experiment orchestration

`ModuleNode<O>` is the typed single-output node base. A module can declare a
formula node directly; `FormulaNode` collects its column inputs, so the graph
dependency wiring stays automatic:

```java
NumExpr control = F.sum(
        F.weighted(F.col(ctr), "rerank.ctr_w", 100.0),
        F.weighted(F.col(cvr), "rerank.cvr_w", 50.0),
        F.weighted(F.col(gmv), "rerank.gmv_w", 0.01));

NumExpr couponBoost = F.when(
        F.and(
                F.variant("rerank", "coupon_boost"),
                F.gt(F.col(couponRoi), F.val(0.0))),
        F.weighted(F.col(gmv), "rerank.coupon_boost_w", 0.02),
        F.val(0.0));

score = formula(SearchSchema.RANKING_SCORE.doubleColumn(),
        F.chooseVariant("rerank",
                Map.of("control", control,
                        "coupon_boost", control.add(couponBoost)),
                control));
```

`ExperimentContext` is installed per request (`SearchContext` carries it in
the demo), and `ColumnPipeline.setExperiment(...)` exposes it to every formula
node. `F.param(...)` changes coefficients, `F.variant(...)` selects an
experiment layer, `F.when(...)` gates by row features, `F.caseWhen(...)` covers
multi-branch rules, and `F.chooseVariant(...)` selects a whole formula branch.
Expressions are typed (`NumExpr`, `BoolExpr`) and evaluated with columnar
compute primitives; branches do not rebuild the graph.

For configuration-driven formulas, `FormulaConfig.of(map)` accepts a
JSON/YAML-friendly tree and compiles it against typed column handles. The
demo keeps the formula in `SearchModule.rankingFormula(...)`; applications that
need external configuration can deserialize the same tree with Jackson,
SnakeYAML or their own loader and compile it at startup:

```java
FormulaConfig config = FormulaConfig.of(Map.of(
        "op", "sum",
        "terms", List.of(
                Map.of("op", "weighted", "column", "ctr",
                        "key", "rerank.ctr_w", "default", 100.0),
                Map.of("op", "weighted", "column", "cvr",
                        "key", "rerank.cvr_w", "default", 50.0))));

NumExpr score = config.compileNum(Map.of("ctr", ctr, "cvr", cvr));
```

Limits are explicit: this layer is for row-level score/eligibility formulas.
PV-level aggregation, model inference and cross-row windows still belong in
custom `ModuleNode`s. For a genuinely different graph topology, register one
pipeline supplier per variant with `ExperimentPipelines` and route the request
to the matching fresh pipeline. `ColumnNode` remains for legacy multi-output
nodes; new single-output business nodes should extend `ModuleNode<O>`.

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
independent nodes concurrent. The graph's indegrees, reverse adjacency and root
set are compiled once into a `GraphPlan`; each run allocates only the mutable
counters. `Schedule.run(ctx, PoolKind.COMPUTE, work)` is the pool-hop
equivalent of the C++ `co_await Schedule(...)`.

`StatsCollector` records per-node (and per-op) status/timing without
serializing completion on a stats lock. `ExecutionStats.summary()` now includes
success, fallback, failed and skipped counts in addition to the waterfall.

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
- `graphTimeout(...)` sets a whole-run deadline. If a node or op's effective
  deadline is the graph deadline, its timeout also aborts the run, so
  `lastRunAborted()` is reliable even when the task timer beats the watchdog.
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

### Define the schema once

No empty class per column. Declare immutable `Column<T>` definitions in one
schema file (the example uses an enum with typed accessors):

```java
public enum SearchSchema {
    IMPRESSIONS("impressions", DType.INT64),
    CLICKS("clicks", DType.INT64),
    CTR("ctr", DType.FLOAT64),
    CTCVR("ctcvr", DType.FLOAT64);

    private final Column<?> column;

    SearchSchema(String name, DType dtype) {
        this.column = new Column<>(name, dtype);
    }

    public Column<Long> longColumn() {
        return (Column<Long>) column;      // dtype-checked in the real demo
    }

    public Column<Double> doubleColumn() {
        return (Column<Double>) column;
    }
}
```

A `Column<T>` is a **value object / port**, not a node. The framework creates
a `ColumnStream<T>` handle from it when a module declares an input or a node
declares an output:

```java
ColumnStream<Long> impressions = feed(SearchSchema.IMPRESSIONS.longColumn());
ColumnStream<Double> ctr = required(SearchSchema.CTR.doubleColumn());
```

`ColumnContext` validates the tensor dtype on read/write, and typed feed
columns are coerced to their declared dtype. This is intentionally preferred
over one empty subclass per metric: the schema stays in one place while the
generic type still prevents mixing long and double columns.

### Nodes define the business logic

The preferred business API is a declarative `ColumnOperator` class or instance
plus typed `ColumnStream` handles:

```java
var ctr = node(SearchSchema.CTR.doubleColumn(), RatioOp.class, clicks,
        impressions);
var cvr = node(SearchSchema.CVR.doubleColumn(), RatioOp.class, orders,
        clicks);
var ctcvr = node(SearchSchema.CTCVR.doubleColumn(), MulOp.class, ctr, cvr);
```

`ctcvr` is a node output wired from two other node outputs. Each node binds one
operator; the module owns the graph structure and its feed/fetch streams. No
`Tensor`, tensor shape or kernel type appears in normal module code. Built-ins
include `AddOp`, `SubOp`, `MulOp`, `DivOp`, `RatioOp`, `MulAddOp`,
`OneMinusOp`, `ScaleOp`, `ExperimentScaleOp`, `VariantSwitchOp`, `ParamOp`,
`StackOp` and `TopKOp`; `TopKOp` groups by a PV/group column and emits INT64
selection flags.

For custom formulas or aggregations, implement `ColumnOperator`; `OpNode` and
`TensorKernel` remain internal framework details. The `UnaryNode` /
`BinaryNode` / custom `ColumnNode` extension API is still supported for
advanced nodes, and `ModuleNode<O>` is the typed single-output base.

### Modules compose typed columns

A module implements the three ordered build phases and keeps typed fields:

```java
@ColumnComponent
public final class CtrModule extends ColumnModule<SearchRow> {
    private ColumnStream<Long> impressions;
    private ColumnStream<Long> clicks;
    private ColumnStream<Double> ctr;

    @Override
    protected void buildFeeds(ColumnGraphContext context) {
        impressions = feed(SearchSchema.IMPRESSIONS.longColumn(),
                row -> row.goods().impressions());
        clicks = feed(SearchSchema.CLICKS.longColumn(),
                row -> row.goods().clicks());
    }

    @Override
    protected void buildNodes(ColumnGraphContext context) {
        ctr = node(SearchSchema.CTR.doubleColumn(), RatioOp.class,
                clicks, impressions);
    }

    @Override
    protected void buildFetches(ColumnGraphContext context) {
        fetch(ctr);
    }

    public ColumnStream<Double> ctr() {
        return published(SearchSchema.CTR.doubleColumn());
    }
}
```

`feed` / `node` / `fetch` are the declarative forms of `input` / `add` /
`output`; the older names remain as aliases. `feed(Column, getter)` also
declares the primitive row mapping, so `feedTensorMap(rows)` builds tensors
without `Cell`/`Row` objects.

- `published(Column<T>)` exposes one fetch output by schema definition, so a
  consumer module gets a typed `ColumnStream<T>` without casts or strings.
- `feedMap()` / `fetchMap()` are read-only `name -> ColumnStream<?>` views for
  boundary adapters and inspection. Business compute should keep using typed
  fields; the maps do not remove dtype checks by themselves.
- Node ids are generated by the framework.
- Dependencies are **stream references** (`node(..., clicks, impressions)`),
  never node names.
- Modules may depend on multiple modules with
  `dependsOn(FirstModule.class, SecondModule.class, ...)`; the module graph is a
  DAG and cycles are rejected.
- `ColumnNode` / `ModuleNode` `feeds()` / `fetches()` and module `nodes()`,
  `inputs()`, `outputs()`, `dependencies()` return snapshots; their arity is
  fixed after construction. Internal scheduler/compiler paths use
  package-private views.
- Rows contain metric data, not user/item ids. Ids are lookup keys; column
  computation operates on measurable business columns.

Module-to-module wiring stays typed. `required(Column<T>)` resolves a shared
node output produced by an earlier module:

```java
@ColumnComponent
public final class ScaledCtrModule extends ColumnModule<SearchRow> {
    @Override
    protected void build() {
        dependsOn(CtrModule.class);
        super.build();
    }

    @Override
    protected void buildNodes(ColumnGraphContext context) {
        var ctr = required(SearchSchema.CTR.doubleColumn());
        node(SearchSchema.RANKING_SCORE.doubleColumn(),
                new ScaleOp(100.0), ctr);
    }

    @Override
    protected void buildFetches(ColumnGraphContext context) {
        fetch(SearchSchema.RANKING_SCORE.doubleColumn());
    }
}
```

### Compile, feed, compute, fetch

```java
FunnelMetricModule metrics = new FunnelMetricModule();
try (ColumnPipeline pipeline = new ColumnPipeline()) {
    pipeline.add(metrics).compile();     // or pipeline.scan("com.example")

    // Fluent phases: feedRows uses the compiled required-feed order.
    pipeline.feedRows(rows).compute();

    // Columnar inputs avoid row/cell conversion (ownership transfers).
    // pipeline.feedTensors(Map.of("impressions", Tensor.fromLongs(...)));

    List<Row> out = pipeline.fetch(FetchStream.of(metrics.ctr()));
    double firstCtr = pipeline.fetchScalar(metrics.ctr());
    Tensor firstTensor = pipeline.fetchTensor(metrics.ctr());

    // Column-oriented result, keyed by name.
    Map<String, List<Cell>> columns =
            pipeline.fetchColumns(metrics.ctr());
}
```

`FeedStream` / `FetchStream` remain the typed API and carry
`ColumnStream<?>[]`. `feedRows` builds the feed from the compiled required
columns; `feed(Map)` accepts named cell lists; `feedTensors` accepts pre-built
columnar tensors; `fetchColumns` returns named cell lists and `fetchTensor` /
`fetchTensors` return read-only tensors without row conversion.
`fetchColumns()` (no args) returns every compiled output and supports outputs
with different shapes, unlike the row-oriented `fetch()`. Missing required feed
columns are rejected before execution.

A `ColumnPipeline` is mutable and not thread-safe. Create one pipeline per
concurrent run; reusing one pipeline sequentially is fine, and the compiled
plan is reused when the requested outputs do not change. For one-shot use,
`run(feed)` returns every compiled output and `run(feed, outputs...)` prunes to
the requested streams.

### Performance and execution model

The column engine is CPU-bound by default, so `compute-workers` (or
`new ColumnPipeline(workers)`) bounds a fixed platform daemon pool; `0` means
available processors. `ComputeGraph.executeAsync` uses a dependency-counter
ready queue: only nodes whose dependencies are complete are submitted, running
op bodies execute directly on pool threads, and no thread is parked per
dependency. Independent nodes still run concurrently, and sequential
`ComputeGraph.execute` remains available for single-threaded runs.

Compilation is separated into `PipelineCompiler` and an immutable
`PipelinePlan`. Adding a module invalidates the plan; compiling the same
requested outputs again reuses it. The ready-queue schedulers precompute integer
dependent indices, avoiding identity-map lookups on every completed edge.

All three schedulers now cache their structural plan:

- `Graph` builds a `GraphPlan` once (indegrees, reverse adjacency and roots);
  every `Executor.execute` copies only the primitive counters.
- `OpGraph.compile()` builds a reusable `Plan` for `OpExecutor`, and aspects are
  exposed as a cached array instead of a defensive `List.copyOf` per op.
- `ComputeGraph.finalizeGraph()` builds a `Plan` with insertion-order indexes,
  reverse adjacency, roots, output names and module indexes. Repeated runs skip
  all identity maps and per-module `ConcurrentHashMap` lookups.
- The ready queues store node references directly, so node indexes are not
  boxed on enqueue/dequeue.
- `Executor` and `OpExecutor` submit a `FutureTask` wrapper whose `done()`
  covers success, failure and cancellation-before-start. Timeout/abort paths
  therefore complete exactly once without a per-node `CompletableFuture`
  chain.
- Declarative `OpNode`s bind their `TensorKernel` directly in
  `PipelineCompiler`; only custom/formula nodes pay for the business
  `ColumnContext` adapter. This removes two context allocations per operator
  node and one array round-trip per run.

If a column node performs blocking I/O, enable virtual compute threads with
`pipeline.setVirtualComputeThreads(true)` (Spring:
`lary.column.virtual-threads: true`). The ready queue is unchanged, but
blocking nodes no longer occupy one of a small number of platform workers.
`compute-workers` remains the concurrency hint reported by monitoring. For a
well-bounded blocking workload you can keep the fixed pool and raise the worker
count instead.

The separate DAG `OpExecutor` now uses the same dependency-counter ready queue:
an op is submitted only after its dependencies complete, its body runs on the
dag pool, and a shared timer schedules deadlines. `OpExecutor.execute` simply
joins `executeAsync`. Its default pool is virtual-thread-per-task; a
caller-provided pool is not shut down by `close()`.

Boundary conversion avoids redundant copies without changing the public
copying defaults:

- `feed` transfers ownership of the freshly built feed table to the pipeline
  store; callers must not mutate their feed tensors after feeding.
- `fetch` returns read-only views of result tensors. `fetchColumns` snapshots
  the cell values into immutable lists; do not mutate the tensors behind them.
- `TensorStore.addTable(table)` and `toTable(names)` still clone, as before.
  Only the pipeline paths use the internal no-copy overloads.
- `feedTable` writes primitive buffers directly, and `fetchColumns` reads one
  column at a time instead of transposing through `List<Row>`.
- `fetchRows` builds one `Cell[]` per output row instead of an intermediate
  `ArrayList` and defensive copy; `fetchTensor` skips row conversion entirely.
- `feedTensors` skips the cell transpose for callers that already have
  columnar tensors.
- `fetchScalar` reads the first Q8_0 block without dequantizing the whole
  tensor.

Module definitions are initialized idempotently: reentrant and failed
`build()` calls cannot leave a module half-initialized, and `feedMap()` /
`fetchMap()` cache immutable name views after initialization. Feed tensor
conversion precomputes the per-column getter/dtype arrays once per module and
iterates the row list directly, avoiding list indexing and repeated dtype
checks in the inner loop.

Node monitoring events carry a `module` attribute, so `RunStats` attributes
ops to the owning module even though node ids are generated. Integer
`neg`/`abs`/`square` and comparison-mask writes are also SIMD kernels now.
`RunStats` also reports feed/fetch shape, computed node count, platform/virtual
mode, total elapsed time, and per-op `success` / `fallback` / `failed` status.
Node-completion samples are collected in concurrent queues so monitoring does
not serialize the scheduler on a stats lock.

A single `RunStats` run measures one feed/compute/fetch cycle, including JIT
and class-loading effects on short nodes. Use warm-up iterations and multiple
samples for throughput decisions; `:examples:runFullBenchmark` does this with
median and p95 reporting across the DAG, column and ranking stages.
Sub-millisecond per-op timings remain sensitive to JIT compilation, CPU
frequency and monitoring overhead.

### Classpath discovery without Spring

`@ColumnComponent` marks a `ColumnModule`; the built-in scanner supports class
directories and jars and needs no Spring dependency:

```java
@ColumnComponent
public final class SearchModule extends ColumnModule { ... }

pipeline.scan("com.example.features");
SearchModule search = pipeline.module(SearchModule.class);
```

This scanner requires a no-argument constructor. It is not the Spring
integration; use the `:spring` module when modules need constructor injection.

### Spring Boot integration (optional `:spring` module)

The core library and `:examples` stay dependency-free. Add the separate module
in a Spring Boot application:

```kotlin
implementation(project(":spring"))
```

Then enable scanning:

```java
@SpringBootApplication
@EnableLaryColumns(basePackages = "com.example.features")
class Application {
}
```

`@EnableLaryColumns` registers `@ColumnComponent` modules as Spring beans with
constructor autowiring, so this works:

```java
@ColumnComponent
public final class RankingWeightModule extends ColumnModule {
    public RankingWeightModule(FunnelMetricModule funnel) { ... }
}
```

If no packages are given, the annotated class's package is used.
`lary.column.scan-packages` is always added; modules registered with ordinary
Spring `@Component` or `@Bean` definitions are collected as well.

Auto-configuration exposes:

- `ColumnPipelineFactory` — creates a fresh pipeline from the managed modules.
  Inject this rather than sharing a mutable pipeline.
- `ColumnPipeline` — a prototype bean for convenience; prefer the factory for
  one run per feed/compute/fetch cycle.

```java
try (ColumnPipeline pipeline = context.getBean(ColumnPipelineFactory.class).create()) {
    pipeline.compile().feed(feed).compute();
    return pipeline.fetch(FetchStream.of(metrics.ctr()));
}
```

Configuration:

```yaml
lary:
  column:
    scan-packages: [com.example.features]
    compute-workers: 4   # 0 = available processors
    virtual-threads: false # true for blocking column nodes
    enabled: true
```

Run the Spring demos:

```sh
./gradlew :spring:runSpringSearch
```

It lives under `spring/src/test/java/io/lark/spring/` and uses the same
`SearchModule` as the `:examples` demo. The demo has no manual module list:
component scanning supplies the `ColumnModule` bean and the application calls
`module.search(pipeline, request)` with a factory-created per-request pipeline.


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

### Tensor shapes

`Tensor` is still backed by one flat primitive array, but now carries a
`TensorShape` of rank 1–3:

```java
TensorShape shape = TensorShape.of(2, 3, 4); // [batch, candidate, feature]
Tensor a = Tensor.fromDoubles(shape, 1, 2, 3, /* ... 24 values */);
Tensor b = Tensor.fromDoubles(shape, 4, 5, 6, /* ... 24 values */);

Tensor sum = Compute.add(a, b);
Tensor product = Compute.mul(a, b);
assert sum.shape().equals(shape);

// Right-aligned broadcasting, e.g. [2,3] + [3] or [2,3] + [2,1].
Tensor matrix = Tensor.fromDoubles(TensorShape.of(2, 3), 1, 2, 3, 4, 5, 6);
Tensor row = Tensor.fromDoubles(TensorShape.of(3), 10, 20, 30);
Tensor broadcast = Compute.add(matrix, row);
assert broadcast.shape().equals(TensorShape.of(2, 3));
```

Element-wise binary add/sub/mul/div and comparisons use right-aligned NumPy-style
broadcasting; add/mul are commutative in operand order, while sub/div keep
operand order. The common ranking expression `score * perturbation +
originalScore` is available as a three-input operation:

```java
Tensor adjusted = Compute.mulAdd(score, perturbation, originalScore);
// equivalently: Compute.fma(...); not a hardware fused-rounding operation
```

The same operation is available declaratively as `OpSpec("mul_add", ...)` (or
`"fma"`), and the legacy `TensorOps` factory accepts both names. Scalar ops,
unary kernels, casts and quantization preserve input shape. Incompatible
dimensions fail fast, even when flat element counts happen to match.
`Tensor.zeros(shape, dtype)` creates a zero tensor without going through a
business module.

The op layer supports both declarative and typed zeros:

```java
OpTensor zeros = TensorOps.create(new OpSpec("zeros", List.of(), List.of("z"),
        Map.of("dtype", "float64", "shape", "2,3")));
zeros.compute(new OpContext(store));
```

Business modules can use `ZerosNode`:

```java
ColumnStream<Double> zeros =
        node(new ZerosNode<>(FEATURES, TensorShape.of(rows, 3))).out();
```

`TensorOps` also accepts `const` with a `shape` attribute; a comma-separated
constant keeps its historical element count when only `size` is supplied.

### Typed tensor operations

The operation layer is separate from execution nodes:

```java
TensorStore store = new TensorStore();
store.set("a", Tensor.fromFloats(1, 2, 3));
store.set("b", Tensor.fromFloats(10, 20, 30));

OpSpec addSpec = new OpSpec("add", List.of("a", "b"), List.of("sum"));
TensorKernel add = TensorKernels.create(addSpec);
add.compute(new KernelContext(store,
        new Tensor[]{store.get("a"), store.get("b")},
        result -> store.set("sum", result)));

OpSpec mulSpec = new OpSpec("mul", List.of("a", "b"), List.of("product"));
TensorKernels.create(mulSpec).compute(new KernelContext(store,
        new Tensor[]{store.get("a"), store.get("b")},
        result -> store.set("product", result)));
```

New code should use `OpSpec` + `TensorKernel`. The legacy `OpTensor` /
`FloatAddTensor` / `TensorOps` APIs remain available as compatibility adapters;
`ComputeNode` can bind either form.

### Layered execution: OpSpec → ComputeNode → TensorKernel → Tensor

```text
ModuleNode / ColumnModule / ColumnPipeline   business orchestration
             ↓
OpSpec                                      low-level graph description
  type / inputs / outputs / attrs
             ↓
ComputeNode / ComputeGraph                  scheduling + store binding
             ↓
TensorKernel / KernelContext                actual computation logic
             ↓
Tensor (tensor package)                     flat storage + shape
```

`OpSpec` describes what the graph should run. `TensorKernel` receives resolved
inputs through `KernelContext` and emits a tensor result; it does not know node
ids, dependency edges, or input/output names. `ComputeNode` binds named inputs,
calls the kernel, and writes the declared output.

```java
OpSpec spec = new OpSpec("add", List.of("a", "b"), List.of("sum"));
TensorKernel kernel = TensorKernels.create(spec);
graph.addNode("sum", "module", spec, kernel);
```

Business modules compile to an `OpSpec("business", ...)` plus either the
declarative `OperatorKernel` (for `OpNode`) or `BusinessNodeKernel` (for
custom/formula nodes); the rest of the pipeline never sees business-node
internals. `OpTensor` / `TensorOps` are kept for source compatibility and are
marked legacy in the source documentation.

`Simd.summary()` reports the active Vector API species. SIMD kernels live in
`io.lark.column.compute.VectorOps`.

## Column architecture

| Package | Responsibility |
|---|---|
| `io.lark.column.tensor` | `DType`, `BlockQ8_0`, `Tensor`, `TensorShape`, `TensorTable`, `TensorStore`, `FeedFetch`, `TensorKernel`, `KernelContext`, `TensorKernels`, `FallibleTensorKernel` |
| `io.lark.column.data` | `Cell`, `Row` |
| `io.lark.column.compute` | `Compute`, `VectorOps`, `Simd` |
| `io.lark.column.op` | `OpSpec` graph descriptor; legacy `OpTensor`, `OpContext`, `TensorOps`, `ZerosTensor` and typed compatibility ops (`FloatAddTensor`, …) |
| `io.lark.column.exec` | `ComputeNode` (binds `OpSpec` + `TensorKernel`), `ComputeGraph`, `ExecutionContext` — scheduling layer |
| `io.lark.column.biz` | typed business API: `Column<T>`, `ColumnStream<T>`, `ColumnOperator` + declarative operators (`biz/op/*`), `ModuleNode<O>` / `FormulaNode` / `F` / `FormulaConfig` (typed formulas, external config and experiments), `ColumnNode`, `UnaryNode` / `BinaryNode` / `SourceNode` / `ZerosNode` (advanced), `ColumnContext`, `ColumnModule`, `ColumnPipeline`, `PipelineCompiler` / `PipelinePlan`, `FeedStream`, `FetchStream`, `ColumnComponent`, `ColumnComponentScanner` |
| `io.lark.column.context` | `ExperimentContext` (per-run variants and parameters) |
| `io.lark.column.backend` | `Backend`, `CpuBackend`, `BackendFactory` |
| `io.lark.column.monitor` | `RunStats`, `ColumnStatsCollector` |
| `io.lark.spring` (`:spring` module) | `EnableLaryColumns`, `ColumnComponentRegistrar`, `LaryColumnAutoConfiguration`, `ColumnPipelineFactory` — optional Spring Boot integration |

## Maintenance rule

Every behavior or API change must be committed together with:

1. updated `README.md` and `README.zh-CN.md` (feature, usage, limitations);
2. an updated `.opencode/skills/java-port-sync/SKILL.md` (mapping, invariants,
   verification);
3. tests and, when user-facing, an example.

The sync skill is the operational checklist for mirroring changes from the
upstream C++ `lark` modules.

## Licensing

MIT, matching the upstream lark project. See `LICENSE`.
