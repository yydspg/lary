---
name: Java Port Sync
description: Use when changing the C++ dag or column modules or the Java port in lary — keeps the Java 25 implementation, tests, examples and docs in sync with lark/dag and lark/column.
---

# Java Port Sync

The C++ `dag/` and `column/` modules in the sibling `../lark` repository are the
source of truth. `lary` is a single-project Java 25 port of both (plus the
minimal metric surface they need). Keep behavior, public names, monitoring
vocabulary, validation semantics and event attributes aligned.

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
  `src/main/java/io/lark/examples`.
- Someone asks to port a DAG / column feature to Java or verify parity.

## Read the reference first

Before editing Java, read the changed C++ header/source **and** the matching
test. Tests are the behavioral contract.

### DAG mapping

| C++ | Java |
|---|---|
| `dag/include/dag/node.h`, `src/node.cpp` | `src/main/java/io/lark/dag/.../Node.java`, `NodeStatus.java` |
| `dag/include/dag/graph.h`, `graph_builder.h` | `Graph.java`, `GraphBuilder.java`, `NodeDef.java` |
| DAG ready-queue scheduler (Java-first) | `Executor.java` dependency counters + `Graph.dependentsOf`; `Graph.nodeArray()` |
| `dag/include/dag/node_registry.h` | `NodeRegistry.java` |
| `dag/include/dag/executor.h`, `src/executor.cpp` | `Executor.java` |
| `dag/include/dag/stats.h` | `StatsCollector.java`, `ExecutionStats.java`, `NodeRunStats.java` |
| `dag/include/dag/i_context.h`, `default_context.h` | `IContext.java`, `DefaultContext.java` |
| `dag/include/dag/schedule.h` | `Schedule.java` |
| `dag/include/dag/op.h` | `Op.java`, `OpAspect.java`, `OpStatus.java` |
| `dag/include/dag/op_graph.h` | `OpGraph.java` |
| `dag/include/dag/op_executor.h` | `OpExecutor.java` |
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
| `column/include/column/tensor.h`, `src/tensor.cpp` | `Tensor.java` |
| `tensor_table.h`, `tensor_store.h` | `TensorTable.java`, `TensorStore.java` |
| `feed_fetch.h`, `src/feed_fetch.cpp` | `Cell.java`, `Row.java`, `FeedFetch.java` |
| `column/include/column/compute/*`, `src/compute/*` | `compute/Compute.java`, `compute/VectorOps.java` |
| `compute/simd.h` | `Simd.java` + `compute/VectorOps.java` (`jdk.incubator.vector`) |
| `exec/tensor_op.h`, `exec/ops*.cpp` | `op/OpTensor.java`, `TensorOps.java`, `OpSpec.java`, `OpContext.java`, `FallibleOpTensor.java` |
| Typed tensor ops (Java-first) | `op/FloatAddTensor.java`, `FloatMultiTensor.java`, `FloatSubTensor.java`, `DoubleAddTensor.java`, `IntAddTensor.java`, `LongMultiTensor.java`, … |
| `exec/exec_node.h`, `exec_node.cpp` | `exec/ComputeNode.java` (hidden execution node) |
| `exec/compute_graph.h`, `compute_graph.cpp` | `exec/ComputeGraph.java` |
| `context/execution_context.h` | `exec/ExecutionContext.java` |
| Business Node / Module / Stream (Java-first) | `biz/ColumnNode.java`, `ColumnContext.java`, `ColumnStream.java`, `ColumnModule.java`, `ColumnPipeline.java`, `BusinessNodeOp.java` |
| Feed / fetch boundaries | `biz/FeedStream.java`, `biz/FetchStream.java` (both hold `ColumnStream[]`) |
| Business transforms | `biz/ScaleNode.java`, `biz/UnitConvertNode.java` |
| Spring-style components | `biz/ColumnComponent.java`, `biz/ColumnComponentScanner.java` (on modules) |
| C++ `biz::Module` / `SubGraph` / DSL / `Pipeline` | **intentionally removed in Java**; replaced by `ColumnModule` + `ColumnNode` + `ColumnStream` |
| `backend/backend.h`, `backend.cpp` | `backend/Backend.java`, `CpuBackend.java`, `BackendFactory.java` |
| `monitor.h`, `monitor.cpp` | `monitor/RunStats.java`, `monitor/ColumnStatsCollector.java` |
| `tests/test_column.cpp` | `TensorComputeTest.java` |
| Tensor-op coverage | `OpTensorTest.java` |
| Module / stream / pruning / component coverage | `ModulePipelineTest.java` |
| `tests/test_engine.cpp` | `ExecutionGraphTest.java` |
| End-to-end demos | `examples/LaryDemo.java` (`runDemo`), `examples/RiskEngineExample.java` (`runRisk`) |
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
- Blocking `Node.run` / `Op.execute` bodies run on virtual threads; do not
  reintroduce callback-style APIs.
- Op aspects may skip an op without failing the graph; async execution still
  completes the graph.
- Monitoring events keep the C++ names/attributes; stats remain optional.

### Column

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
- Business API: `ColumnNode` + `ColumnContext` + `ColumnStream` +
  `ColumnModule` + `ColumnPipeline`. Node ids are generated; there is no
  `name()`/`inputs()`/`outputs()` override. Inputs are constructor stream
  references; outputs are `output("name")` fields.
- Data dependency is class/stream-reference based (`new BNode(a.out)`), never
  node-name based. Node `feeds()`/`fetches()` are arrays.
- `ColumnModule` groups nodes and exposes `inputs()`, `outputs()`, `nodes()`,
  `dependencies()` as arrays. Modules depend on modules with
  `dependsOn(OtherModule.class)`; module cycles are rejected.
- `FeedStream` / `FetchStream` are the row-in / row-out boundaries and carry
  `ColumnStream[]`. Missing required feed columns are rejected before compute.
- Built-in business transforms: `ScaleNode` (weight) and `UnitConvertNode`
  (factor, e.g. fen → li).
- `@ColumnComponent` marks modules; `ColumnPipeline.scan(package)` discovers
  them without a Spring dependency. Modules need a no-arg constructor.
- Graph pruning: `ColumnPipeline.compile(requiredOutputs)` keeps the transitive
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
  `node.start/end/error/fallback`; `module.end` with `node_count`;
  `cpu.pressure` with `pool`, `utilization`, `busy_ns`, `wall_ns`, `workers`.
- Vector API: use `jdk.incubator.vector` for element-wise arithmetic, scalar
  broadcast, comparisons, reductions and unary ops. Compile/test/run with
  `--add-modules jdk.incubator.vector`.
- Java adaptation: blocking virtual-thread drivers; op bodies run on the
  pipeline pool; `ComputeGraph.execute` is sequential, `executeAsync` is the
  virtual-thread path.

## Verification

```sh
JAVA_HOME=/path/to/jdk-25 ./gradlew clean test
JAVA_HOME=/path/to/jdk-25 ./gradlew runDemo
JAVA_HOME=/path/to/jdk-25 ./gradlew runSimple runOp
JAVA_HOME=/path/to/jdk-25 ./gradlew runNodes
JAVA_HOME=/path/to/jdk-25 ./gradlew runRisk
```

Use JDK 25. If Gradle cannot find it, set `org.gradle.java.installations.paths`
in `~/.gradle/gradle.properties`.

## Definition of done

- [ ] C++ change/test read and understood.
- [ ] Java implementation updated.
- [ ] Java test added/updated for the behavior.
- [ ] `README.md` updated (feature, usage, limitations).
- [ ] This `SKILL.md` updated (mapping, invariants, verification).
- [ ] Column business code still uses `ColumnNode`; execution (`ComputeNode`)
      and tensor ops (`OpTensor`) stay out of the business API.
- [ ] `./gradlew clean test` is green.
- [ ] DAG tests cover wide and deep graphs and the ready-queue scheduler.
- [ ] Column examples run with `--add-modules jdk.incubator.vector`.
- [ ] Examples still compile; docs/mapping updated if API changed.
- [ ] No C++ edits unless explicitly requested.
