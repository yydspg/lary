---
name: Java Port Sync
description: Use when changing the C++ dag or column modules or the Java port in lary — keeps the Java 25 implementation, tests, examples and docs in sync with lark/dag and lark/column.
---

# Java Port Sync

The C++ `dag/` and `column/` modules in the sibling `../lark` repository are the
source of truth. `lary` is a Java 25 port of both (plus the minimal metric
surface they need), split into the root library and the independent `:examples`
application module. Keep behavior, public names, monitoring vocabulary,
validation semantics and event attributes aligned.

## Maintenance contract

Every behavior or API change must update, in the same commit:

1. `README.md` — feature, usage and limitations.
2. This `SKILL.md` — mapping, invariants and verification.
3. Tests, plus an example when the change is user-facing.

Do not treat documentation/skill updates as optional follow-ups.

## When to use this skill

- A change touches `../lark/dag/include/`, `../lark/dag/src/`,
  `../lark/dag/README.md`, `../lark/column/include/`, `../lark/column/src/`,
  `../lark/column/README.md`, or the DAG / column tests
  (`../lark/tests/test_dag.cpp`, `test_op.cpp`, `test_op_dsl.cpp`,
  `test_column.cpp`, `test_engine.cpp`).
- A change touches `src/main/java/io/lark/dag`,
  `src/main/java/io/lark/column`, `src/main/java/io/lark/metric`, or
  `examples/src/main/java/io/lark/examples`.
- Someone asks to port a DAG / column feature to Java or verify parity.
- A change touches the Gradle modules (`settings.gradle.kts`,
  `examples/build.gradle.kts`) or the example project layout.

## Read the reference first

Before editing Java, read the changed C++ header/source **and** the matching
test. Tests are the behavioral contract.

### DAG mapping

| C++ | Java |
|---|---|
| `dag/include/dag/node.h`, `src/node.cpp` | `src/main/java/io/lark/dag/.../Node.java`, `NodeStatus.java` |
| `dag/include/dag/graph.h`, `graph_builder.h` | `Graph.java`, `GraphBuilder.java`, `NodeDef.java` |
| DAG ready-queue scheduler (Java-first) | `Executor.java` dependency counters + `Graph.nodeArray()`; adjacency is precomputed as integer indices |
| `dag/include/dag/node_registry.h` | `NodeRegistry.java` |
| `dag/include/dag/executor.h`, `src/executor.cpp` | `Executor.java` |
| `dag/include/dag/stats.h` | `StatsCollector.java`, `ExecutionStats.java`, `NodeRunStats.java` |
| `dag/include/dag/i_context.h`, `default_context.h` | `IContext.java`, `DefaultContext.java` |
| `dag/include/dag/schedule.h` | `Schedule.java` |
| `dag/include/dag/op.h` | `Op.java`, `OpAspect.java`, `OpStatus.java` |
| `dag/include/dag/op_graph.h` | `OpGraph.java` |
| `dag/include/dag/op_executor.h` | `OpExecutor.java` |
| DAG op ready-queue scheduler (Java-first) | `OpExecutor.java` dependency counters + per-op timeout scheduler; op bodies run on the dag pool, not a driver thread |
| `dag/include/dag/op_registry.h` | `OpRegistry.java` |
| `dag/include/dag/op_graph_builder.h`, `src/op_graph_builder.cpp` | `OpGraphBuilder.java`, `OpDef.java`, `DslParser.java` |
| Timeout / cancellation (Java-first) | `TimeoutPolicy.java`, `TimeoutAction.java`, `TimeoutTracker.java`, `DagTimeoutException.java`, `GraphAbortedException.java` |
| `tests/test_dag.cpp` | `DagTest.java` |
| `tests/test_op.cpp` | `OpTest.java` |
| `tests/test_op_dsl.cpp` | `OpDslTest.java` |
| Java timeout coverage | `TimeoutTest.java` |

### Column mapping

| C++ | Java |
|---|---|
| `column/include/column/types.h` | `src/main/java/io/lark/column/.../DType.java`, `BlockQ8_0.java` |
| `column/include/column/tensor.h`, `src/tensor.cpp` | `Tensor.java`, `TensorShape.java` (flat storage + 1–3-D row-major shape) |
| `tensor_table.h`, `tensor_store.h` | `TensorTable.java`, `TensorStore.java` |
| `feed_fetch.h`, `src/feed_fetch.cpp` | `Cell.java`, `Row.java`, `FeedFetch.java` |
| `column/include/column/compute/*`, `src/compute/*` | `compute/Compute.java`, `compute/VectorOps.java` |
| `compute/simd.h` | `Simd.java` + `compute/VectorOps.java` (`jdk.incubator.vector`) |
| `exec/tensor_op.h`, `exec/ops*.cpp` | `op/OpTensor.java`, `TensorOps.java`, `OpSpec.java`, `OpContext.java`, `FallibleOpTensor.java` |
| Typed tensor ops (Java-first) | `op/FloatAddTensor.java`, `FloatMultiTensor.java`, `FloatSubTensor.java`, `DoubleAddTensor.java`, `IntAddTensor.java`, `LongMultiTensor.java`, …; `op/ZerosTensor.java` |
| Zero-tensor business node | `biz/ZerosNode.java` |
| `exec/exec_node.h`, `exec_node.cpp` | `exec/ComputeNode.java` (hidden execution node) |
| `exec/compute_graph.h`, `compute_graph.cpp` | `exec/ComputeGraph.java` |
| Column ready-queue scheduler (Java-first) | `exec/ComputeGraph.java` dependency counters + `ComputeNode.dependencyView()`; fixed platform pool sized by `ColumnPipeline.computeWorkers`, optional virtual compute threads for blocking nodes |
| `context/execution_context.h` | `exec/ExecutionContext.java` |
| Business Node / Module / Stream (Java-first) | `biz/ColumnNode.java`, `ColumnContext.java`, `ColumnStream.java`, `ColumnModule.java`, `ColumnPipeline.java`, `PipelineCompiler.java`, `PipelinePlan.java`, `BusinessNodeOp.java` |
| Typed column schema (Java-first) | `biz/Column.java` (`Column<T>` value definitions), `biz/ColumnStream.java` (graph handles); schema constants e.g. `RecSysMetrics.COUPON_ROI` |
| Typed node bases (Java-first) | `biz/NodeSchema.java`, `biz/UnaryNode.java`, `BinaryNode.java`, `SourceNode.java`; custom nodes extend `ColumnNode` with typed fields |
| Feed / fetch boundaries | `biz/FeedStream.java`, `biz/FetchStream.java` (both hold `ColumnStream<?>[]`) |
| Dependency-free components | `biz/ColumnComponent.java`, `biz/ColumnComponentScanner.java` (on modules; no-arg constructors only) |
| Spring Boot integration (optional `:spring`) | `spring/src/main/java/io/lark/spring/`: `EnableLaryColumns.java`, `ColumnComponentRegistrar.java`, `LaryColumnAutoConfiguration.java`, `ColumnPipelineFactory.java`; auto-config imports file under `META-INF/spring` |
| C++ `biz::Module` / `SubGraph` / DSL / `Pipeline` | **intentionally removed in Java**; replaced by `ColumnModule` + `ColumnNode` + `ColumnStream` |
| `backend/backend.h`, `backend.cpp` | `backend/Backend.java`, `CpuBackend.java`, `BackendFactory.java` |
| `monitor.h`, `monitor.cpp` | `monitor/RunStats.java`, `monitor/ColumnStatsCollector.java` |
| `tests/test_column.cpp` | `TensorComputeTest.java` |
| Tensor-op coverage | `OpTensorTest.java` |
| Module / stream / pruning / component coverage | `ModulePipelineTest.java` |
| `tests/test_engine.cpp` | `ExecutionGraphTest.java` |
| DAG demos | `examples/src/main/java/io/lark/examples/SimplePipeline.java`, `OpPipeline.java` (`:examples:runSimple`, `:examples:runOp`) |
| Realistic multi-class demo | `examples/src/main/java/io/lark/examples/recsys/**` (`:examples:runRecsys`), split into `schema/`, `node/`, `module/`, `data/` |
| Warmed-up benchmark | `examples/src/main/java/io/lark/examples/recsys/RecSysBenchmark.java` (`:examples:runRecsysBenchmark`), median/p95 after warm-up |
| Tensor-aware search demo | `examples/src/main/java/io/lark/examples/search/**` (`:examples:runSearch`), modules `CtrModule`, `CvrModule`, `CtCvrModule`, `RebuySameLossModule`, `BuyAgainModule`, `CouponRoiModule`, `RetargetAdModule`, `GmvModule`, `DelayModule`; 2-D/3-D outputs |
| Realistic demo test | `examples/src/test/java/io/lark/examples/recsys/RecSysMetricTest.java`, mock edge-case feed `MockMetrics.java` |
| Spring demo / tests | `spring/src/test/java/io/lark/spring/example/SpringRecSysDemo.java` (`:spring:runSpringRecsys`), `LaryColumnAutoConfigurationTest.java`, `LaryColumnPropertyScanTest.java`, fixtures under `spring/src/test/java/io/lark/spring/fixture/` |
| `metric/include/metric/metric.h` | `src/main/java/io/lark/metric/.../Event.java`, `Monitor.java`, built-ins |

## Workflow

1. Read the matching C++ header/source and test before editing Java.
2. Update layers in dependency order: data (`Tensor`/store) → compute →
   `op` → `exec` → `biz` → examples/docs.
3. Keep the business API free of `io.lark.column.exec` and
   `io.lark.column.op` imports; `BusinessNodeOp` is the only adapter.
4. Add or update a test for every behavior change, plus a runnable example for
   user-facing API changes.
5. Update `README.md` and this file in the same commit.
6. Run the verification commands below; confirm the demo output, pruning,
   cycle detection, fallback and stats.

## Invariants to preserve

### DAG

- Node status transitions: PENDING → RUNNING → SUCCESS / FAILED /
  TIMED_OUT, with fallback producing a degraded success and not poisoning
  dependents.
- Validation rejects unknown dependencies, duplicate node ids, missing
  dependencies and cycles.
- `NodeDef` stores dependencies as an array. `new NodeDef(type, deps...)` and
  `NodeDef.of(type, id, deps...)` are the concise forms; the `List` overloads
  remain for compatibility.
- The Node executor uses a dependency-counter ready queue. It starts only nodes
  whose dependencies are complete; there is no dependency-waiting virtual
  thread per node. Running node bodies use virtual threads, and graph
  completion is a countdown over all nodes.
- Disabled nodes are skipped and release their dependents; abort skips pending
  nodes and cancels running bodies, so the run still drains.
- Independent nodes may run concurrently; dependency order and data flow are
  preserved.
- Timeouts interrupt the worker, record the timeout, optionally invoke
  fallback, quarantine repeated offenders and abort the graph when configured.
  A shared timer schedules Node deadlines rather than a driver thread per node.
  If a node or op timer fires exactly at the graph deadline, the timeout handler
  also marks the run aborted, so `lastRunAborted()` does not depend on the
  watchdog winning the race.
- `OpExecutor` uses the same dependency-counter ready queue: an op is submitted
  only after its dependencies complete, its body runs directly on the dag pool,
  and completion releases dependents. No driver virtual thread is parked per
  op. A single daemon timer thread schedules op deadlines and dispatches the
  timeout handler; `execute` joins `executeAsync`.
- Blocking `Node.run` / `Op.execute` bodies run on virtual threads; do not
  reintroduce callback-style APIs.
- Op aspects may skip an op without failing the graph; async execution still
  completes the graph.
- Monitoring events keep the C++ names/attributes; stats remain optional.
  `StatsCollector` accepts both `node.*` and `op.*` terminal events, appends
  records to a concurrent queue, and `ExecutionStats` exposes
  success/fallback/failed/skipped counts.

### Column

- `TensorShape` stores 1–3 row-major dimensions; storage remains one flat
  primitive array. Element-wise binary ops use right-aligned NumPy-style
  broadcasting; add/mul are commutative in operand order, sub/div keep order.
  Scalar/unary/cast/quantize operations preserve shape. `Tensor.zeros(TensorShape,
  dtype)` and `ZerosTensor` / `ZerosNode` create zero tensors. `TensorOps`
  `const` and `zeros` accept a `shape` attr.
- dtypes: INT32, INT64, FLOAT32, FLOAT64, Q8_0; binary promotion:
  int32+int64 → int64, mixed int/float → float (int64 → float64),
  float32+float64 → float64; division → float64.
- Q8_0 block: 32 int8 values + one float scale; quantize/dequantize/dot keep
  the C++ block layout.
- feed: any double cell → FLOAT64, otherwise INT64; nil cells → 0.
- fetch: int columns → long cells; float/Q8 → double cells.
- Ops: add/sub/mul/div, scalar variants, neg/abs/square/sqrt/exp/log,
  sum/mean/count/max/min/stddev, gt/ge/lt/le/eq/neq (+ scalar), select,
  filter, cast, quantize, dequantize, dot, const, identity.
- Execution: dependency ordering, independent nodes concurrent, failed op does
  not poison dependents; placeholder replacement for forward references;
  duplicate column producer / duplicate write / cycle rejection.
- `ComputeGraph.executeAsync` is a dependency-counter ready queue, not a
  virtual thread per node: a node is submitted only when its dependency count
  reaches zero, its op body runs directly on the supplied pool, and completion
  releases dependents. `ColumnPipeline` passes a fixed platform daemon pool
  sized by `computeWorkers` because column kernels are CPU-bound. For blocking
  column nodes call `setVirtualComputeThreads(true)` (Spring
  `lary.column.virtual-threads: true`): the ready queue is unchanged, but the
  pool becomes virtual-thread-per-task and `computeWorkers` stays a monitoring
  hint. A rejected submit drains serially instead of deadlocking; sequential
  `execute` stays on the calling thread.
- Business API: typed `ColumnStream<T>` handles + `ColumnNode` /
  `ColumnContext` / `ColumnModule` / `ColumnPipeline`. Node ids are generated;
  there is no `name()`/`inputs()`/`outputs()` override.
- Schema is declared once as immutable `Column<T>` constants
  (`new Column<>("ctr", DType.FLOAT64)`), not one empty subclass per metric.
  `ColumnContext` validates read/write dtype; typed feed columns are coerced to
  their declared dtype.
- `ColumnStream<T>` handles are created by `fetch(Column<T>)` on nodes and
  `feed(Column<T>)` (alias `input`) on modules. Module outputs are declared with
  `fetch(streams...)` (alias `output`), and `node(...)` aliases `add(...)`.
  Common node shapes use `UnaryNode<I,O>`, `BinaryNode<A,B,O>` and
  `SourceNode<O>`; custom nodes extend `ColumnNode` and keep typed handles.
  Business compute must not use string output names or `feeds()[0]`.
- `NodeSchema<O>` is a record wrapping a single output column. Each typed node
  exposes a `public static final SCHEMA` property built with
  `NodeSchema.of(Column<T>)` and needs no explicit constructor. The no-arg base
  constructor reads that field once per class (cached); the module binds inputs
  with `node(new XNode(), inputs...)` (typed `UnaryNode` / `BinaryNode`
  overloads). The explicit constructor forms remain for custom nodes and dynamic
  output schemas. Custom nodes fetch outputs with `fetch(NodeSchema<T>)`.
- `ColumnModule.published(Column<T>)` returns a typed fetch output by schema
  definition for module-to-module wiring. `feedMap()` / `fetchMap()` are
  read-only `name -> ColumnStream<?>` views for boundary adapters and
  inspection, not for business compute.
- Data dependency is handle/reference based (`new BNode(a.out())`), never
  node-name based. Node `feeds()`/`fetches()` are arrays.
- `ColumnModule` groups nodes and exposes `inputs()`, `outputs()`, `nodes()`,
  `dependencies()` as arrays. Module inputs use `feed(Column<T>)` (alias
  `input`). Modules depend on modules with `dependsOn(OtherModule.class)`;
  module cycles are rejected.
- `FeedStream` / `FetchStream` are the row-in / row-out boundaries and carry
  `ColumnStream<?>[]`. Missing required feed columns are rejected before
  compute. Use `compile(FetchStream)` / `fetchScalar(ColumnStream<?>)` to keep
  pruning and scalar reads typed. `feedRows(List<Row>)` uses the compiled
  required-column order; `feed(Map<String, List<Cell>>)` accepts named columns;
  `fetchColumns(...)` returns named cell lists. `FeedFetch` owns the row/column
  conversion helpers: feed writes primitive buffers directly, fetch reads one
  column without a row transpose, and Q8_0 scalar reads use the first block
  without full dequantization. `feedTensors(Map<String, Tensor>)` skips cell
  conversion; `fetchTensor(...)` returns a read-only typed tensor. `feed` may
  run before compile: implicit compilation re-coerces the stored feed columns
  to their declared dtypes.
- Pipeline feed transfers table ownership to the store and `fetch` returns
  read-only result views; `fetchColumns` snapshots cells into immutable lists.
  `TensorStore.addTable(table)` / `toTable(names)` keep cloning for public
  callers; only `addTable(table, false)` / `toTable(names, false)`
  (pipeline-internal) share tensors. Never mutate a fed tensor after `feed` or
  a fetched tensor after `fetch`.
- `ColumnPipeline` phase methods are fluent (`compile`/`feed`/`compute` return
  the pipeline); `run(FeedStream, ColumnStream<?>...)` is the single-shot typed
  convenience. A pipeline is mutable and not thread-safe; create one per
  concurrent run.
- `ColumnModule` returns array snapshots from public `nodes()`, `inputs()`,
  `outputs()` and `dependencies()`; internal compiler/scheduler code uses
  package-private views. `define()` is idempotent/synchronized and retryable
  after failure; `feedMap()`/`fetchMap()` cache immutable name views.
- Typed dispatcher ops (`AddTensor`, `SubTensor`, `MultiTensor`, `DivTensor`)
  cache their typed delegates instead of allocating one per `compute`.
- The realistic demo targets search/recommendation metrics: CTR =
  clicks/impressions, CVR = orders/clicks, GMV = gmv_fen/100,
  buyagain_cost = coupon_cost/buy_again_orders, coupon_roi = gmv/coupon_cost,
  weight = 100*ctr + 50*cvr + 0.01*gmv + 0.5*coupon_roi - 0.5*buyagain_cost.
  Ratios are zero when the denominator is zero. Rows contain metric
  aggregates, not user/item ids; ids are lookup keys, not columnar inputs.
  Feed samples live in `MetricsRepository`/`MetricsFeed.sample`; tests add
  edge-case mock rows via `MockMetrics`.
- `@ColumnComponent` marks modules; `ColumnPipeline.scan(package)` discovers
  them without a Spring dependency. That core scanner requires a no-arg
  constructor.
- The optional `:spring` module is the only place with Spring dependencies.
  `@EnableLaryColumns` scans `@ColumnComponent` modules through Spring's
  scanner, registers them with constructor autowiring, and always adds
  `lary.column.scan-packages`. Auto-configuration collects `ColumnModule` beans
  (including ordinary `@Component`/`@Bean` modules) into a
  `ColumnPipelineFactory`. `ColumnPipeline` is mutable, so expose it as a
  prototype bean and prefer a fresh factory-created pipeline per run.
- Keep Spring out of the root library and `:examples`; `:spring` may depend on
  `:examples` only from its test source set for the demo.
- Graph pruning: `ColumnPipeline.compile(FetchStream)` keeps the transitive
  producers of requested module outputs; `run(FeedStream, FetchStream)` prunes
  before execution. Module cycles and node-graph cycles are always detected,
  even in a pruned branch.
- `ColumnPipeline.nodeCount()` is registered nodes; `compiledNodeCount()` is
  the pruned execution graph size.
- Tensor ops live in `io.lark.column.op`: `OpTensor` is the typed operation
  abstraction (e.g. `FloatAddTensor`, `FloatMultiTensor`); it stores
  inputs/outputs as arrays. `exec.ComputeNode` wraps an `OpTensor` and handles
  scheduling/status. Do not name execution nodes `OpTensor`.
- `ComputeNode` status: PENDING, SUCCESS, FAILED, FALLBACK. A business
  `ColumnNode.fallback` success yields FALLBACK.
- Monitoring source `"column"`; phase events `feed.start/end`,
  `compute.start/end`, `fetch.start/end`; node events
  `node.start/end/error/fallback` carry `op` and `module`; `module.end` with
  `node_count`; `cpu.pressure` with `pool`, `utilization`, `busy_ns`,
  `wall_ns`, `workers`. Feed/fetch end events carry `rows` and `columns`;
  compute events carry `nodes`, `workers` and `virtual`. `ColumnStatsCollector`
  attributes ops from the event `module` attribute (node-id slash parsing is
  only a fallback), records per-op status, and appends concurrent node samples
  to lock-free queues. `RunStats` exposes phase shape, node count, execution
  mode, total elapsed, and success/fallback/failure counts.
- Vector API: use `jdk.incubator.vector` for element-wise arithmetic, scalar
  broadcast, comparisons, reductions and unary ops, including integer
  `neg`/`abs`/`square` and mask writes. Compile/test/run with
  `--add-modules jdk.incubator.vector`.
- Java adaptation: blocking virtual-thread execution; column op bodies run on
  the configured compute pool (fixed platform by default, virtual on request);
  `ComputeGraph.execute` is sequential, `executeAsync` is the ready-queue path.
  `RunStats` covers one run, so use the warmed-up
  `:examples:runRecsysBenchmark` (median/p95) for throughput comparisons rather
  than a single small demo run.

## Verification

```sh
JAVA_HOME=/path/to/jdk-25 ./gradlew clean test :examples:build :spring:build
JAVA_HOME=/path/to/jdk-25 ./gradlew :examples:runSimple :examples:runOp
JAVA_HOME=/path/to/jdk-25 ./gradlew :examples:runRecsys
JAVA_HOME=/path/to/jdk-25 ./gradlew :examples:runSearch
JAVA_HOME=/path/to/jdk-25 ./gradlew :examples:runRecsysBenchmark \
    -Plary.bench.rows=50000 -Plary.bench.iterations=10
JAVA_HOME=/path/to/jdk-25 ./gradlew :spring:test :spring:runSpringRecsys
```

Use JDK 25. If Gradle cannot find it, set `org.gradle.java.installations.paths`
in `~/.gradle/gradle.properties`.

## Definition of done

- [ ] C++ change/test read and understood.
- [ ] Java implementation updated.
- [ ] Java test added/updated for the behavior.
- [ ] `README.md` updated (feature, usage, limitations).
- [ ] This `SKILL.md` updated (mapping, invariants, verification).
- [ ] Column business code uses `Column<T>` schema definitions and
      `ColumnNode` / `UnaryNode` / `BinaryNode` / `SourceNode`; execution
      (`ComputeNode`) and tensor ops (`OpTensor`) stay out of the business API.
- [ ] No business compute uses string outputs or positional `feeds()[n]`.
- [ ] `./gradlew clean test :examples:build` is green.
- [ ] DAG tests cover wide and deep graphs and both ready-queue schedulers
      (`Executor` and `OpExecutor`).
- [ ] Column tests cover the ready-queue scheduler, monitoring `module`
      attribution, no-copy feed/fetch overload semantics, fluent/columnar
      boundaries, pipeline-plan reuse and module snapshots.
- [ ] Blocking-column behavior is covered when the virtual-thread option
      changes; the benchmark still compiles and runs.
- [ ] Tensor shape/arithmetic changes cover broadcasting, commutativity of
      add/mul, 1-D/2-D/3-D shapes and zero-tensor ops/nodes.
- [ ] Repeated-run/timing changes are checked with
      `:examples:runRecsysBenchmark`, not a single `RunStats` sample.
- [ ] Chinese docs (`README.zh-CN.md`) are updated for user-facing changes.
- [ ] Column examples run with `--add-modules jdk.incubator.vector`.
- [ ] `:examples` still compiles; realistic demo classes remain separated into
      schema/nodes/modules instead of one catch-all class.
- [ ] Docs/mapping updated if API or module layout changed.
- [ ] No C++ edits unless explicitly requested.
