---
name: Java Port Sync
description: Use when changing the C++ dag or column modules or the Java port in lary — keeps the Java 25 implementation, tests, examples and docs in sync with lark/dag and lark/column.
---

# Java Port Sync

The C++ `dag/` and `column/` modules in the sibling `../lark` repository are the
source of truth. `lary` is a single-project Java 25 port of both (plus the
minimal metric surface they need). Keep behavior, public names, monitoring
vocabulary, validation semantics and event attributes aligned.

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
| `tests/test_dag.cpp` | `DagTest.java` |
| `tests/test_op.cpp` | `OpTest.java` |
| `tests/test_op_dsl.cpp` | `OpDslTest.java` |

### Column mapping

| C++ | Java |
|---|---|
| `column/include/column/types.h` | `src/main/java/io/lark/column/.../DType.java`, `BlockQ8_0.java` |
| `column/include/column/tensor.h`, `src/tensor.cpp` | `Tensor.java` |
| `tensor_table.h`, `tensor_store.h` | `TensorTable.java`, `TensorStore.java` |
| `feed_fetch.h`, `src/feed_fetch.cpp` | `Cell.java`, `Row.java`, `FeedFetch.java` |
| `column/include/column/compute/*`, `src/compute/*` | `compute/Compute.java`, `compute/VectorOps.java` |
| `compute/simd.h` | `Simd.java` + `compute/VectorOps.java` (`jdk.incubator.vector`) |
| `exec/tensor_op.h`, `exec/ops*.cpp` | `exec/TensorOp.java`, `exec/TensorOps.java`, `exec/OpSpec.java`, `exec/OpContext.java` |
| `exec/exec_node.h`, `exec_node.cpp` | `exec/ComputeNode.java` |
| `exec/compute_graph.h`, `compute_graph.cpp` | `exec/ComputeGraph.java` |
| `context/execution_context.h` | `exec/ExecutionContext.java` |
| `biz/module.h`, `module.cpp` | `biz/Module.java`, `biz/SubGraph.java` |
| `biz/dsl.h`, `dsl.cpp` | `biz/ColumnDsl.java` |
| `biz/sub_graph.h` (`ops::`) | `biz/Ops.java` |
| `biz/pipeline.h`, `pipeline.cpp` | `biz/Pipeline.java` |
| `biz/pipeline_compiler.h`, `pipeline_compiler.cpp` | `biz/PipelineCompiler.java` |
| `backend/backend.h`, `backend.cpp` | `backend/Backend.java`, `CpuBackend.java`, `BackendFactory.java` |
| `monitor.h`, `monitor.cpp` | `monitor/RunStats.java`, `monitor/ColumnStatsCollector.java` |
| `tests/test_column.cpp` | `TensorComputeTest.java` |
| `tests/test_engine.cpp` | `ExecutionGraphTest.java`, `PipelineTest.java` |
| `metric/include/metric/metric.h` | `src/main/java/io/lark/metric/.../Event.java`, `Monitor.java`, built-ins |

## Workflow

1. **Identify the delta** in the C++ implementation and tests: class/method,
   status, event action, attribute name, validation order, executor semantics,
   DSL grammar, dtype promotion, kernel behavior.
2. **Port the implementation** under `src/main/java/io/lark/dag`,
   `src/main/java/io/lark/column` or `src/main/java/io/lark/metric`. Keep public
   names and semantics aligned; do not introduce new dependencies beyond
   JDK 25 + JUnit 5 + `jdk.incubator.vector`.
3. **Mirror the tests** under `src/test/java/io/lark/...`. Port the assertion,
   not just the happy path.
4. **Update examples/docs** if the user-facing API changed:
   `src/main/java/io/lark/examples`, `README.md` mapping/limitations.
5. **Run the Java suite** (see Verification) and report the result.
6. **Do not modify the C++ side** unless the user explicitly asks for a
   cross-language change.

## Invariants to preserve

### DAG

- `NodeStatus`: PENDING, RUNNING, SUCCESS, FAILED, FALLBACK, SKIPPED.
- `OpStatus`: PENDING, RUNNING, SUCCESS, SKIPPED, FAILED.
- Monitor source `"dag"`; node actions `node.start/success/failure/fallback/skipped`;
  op actions `op.start/end/error/skipped`.
- Event attributes: `type`, `status`, `started_ns`, `elapsed_ns`, `error`.
- Failed op does not poison dependents; skipped still completes; fallback
  `true` → FALLBACK, `false` → FAILED; batch-disable keeps timing zero.
- Validation: unknown type, duplicate id, missing dependency, self-dependency,
  cycle. Aspect auto-skip before `onBefore`; `onAfter` gets final status.
- Op DSL: `id:Type -> id:Type`, `;`/newline separators, bare name means
  id == type, reusing an id merges dependencies (fan-in).
- Java adaptation: one virtual thread per node/op; blocking business methods;
  `Schedule.run` is the pool-hop equivalent.

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
- Monitoring source `"column"`; phase events `feed.start/end`,
  `compute.start/end`, `fetch.start/end`; node events `node.start/end/error`;
  `module.end` with `node_count`; `cpu.pressure` with `pool`,
  `utilization`, `busy_ns`, `wall_ns`, `workers`.
- DSL: same operators/precedence, literal folding into `*_scalar`, functions
  select/filter/cast/dot/quantize/dequantize and the unary/reduce set.
- Vector API: use `jdk.incubator.vector` for element-wise arithmetic, scalar
  broadcast, comparisons, reductions and unary ops. Compile/test/run with
  `--add-modules jdk.incubator.vector`.
- Java adaptation: blocking virtual-thread drivers; op bodies run on the
  pipeline pool; `ComputeGraph.execute` is sequential, `executeAsync` is the
  virtual-thread path.

## Verification

```sh
JAVA_HOME=/path/to/jdk-25 ./gradlew clean test
JAVA_HOME=/path/to/jdk-25 ./gradlew runSimple runOp
JAVA_HOME=/path/to/jdk-25 ./gradlew runColumn
```

Use JDK 25. If Gradle cannot find it, set `org.gradle.java.installations.paths`
in `~/.gradle/gradle.properties`.

## Definition of done

- [ ] C++ change/test read and understood.
- [ ] Java implementation updated.
- [ ] Java test added/updated for the behavior.
- [ ] `./gradlew clean test` is green.
- [ ] Column examples run with `--add-modules jdk.incubator.vector`.
- [ ] Examples still compile; docs/mapping updated if API changed.
- [ ] No C++ edits unless explicitly requested.
