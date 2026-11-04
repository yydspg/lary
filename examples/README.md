# lary examples

This Gradle module depends on the root `lary` library and contains runnable
examples. It is intentionally separate from the library build and has no Spring
dependency. The optional `:spring` module runs the same search graph through
Spring Boot (`./gradlew :spring:runSpringSearch`).

```sh
./gradlew :examples:test
./gradlew :examples:runSimple          # Node-layer DAG
./gradlew :examples:runOp              # Op + Aspect DAG
./gradlew :examples:runSearch          # row feed -> PV top-k search graph
./gradlew :examples:runRank            # greedy / beam slot ranking
./gradlew :examples:runFullDemo        # DAG + column + ranking end to end
./gradlew :examples:runFullBenchmark   # warmed-up throughput for the full demo
```

## Demo layout

```text
io.lark.examples/
├─ SimplePipeline.java        DAG Node tour
├─ OpPipeline.java            DAG Op / Aspect tour
├─ rank/                      sequential, beam and backtracking rankers
├─ search/                    typed search schema, module and result facade
│  ├─ SearchDemo.java         request model, deterministic rows and CLI
│  ├─ SearchSchema.java       Column<T> definitions
│  ├─ SearchModule.java       feeds, operators, formula, PV top-k
│  └─ SearchResult.java       typed result records
└─ demo/
   ├─ FullRankingDemo.java    complete request -> metrics -> top-k -> slate
   └─ FullRankingBenchmark.java
```

`SearchModule` declares all feeds with `feed(Column<T>, getter)`, so
`feedTensorMap(rows)` builds primitive tensors without `Cell`/`Row` objects.
`SearchDemoTest` covers the hand-authored request, experiment variants and two
simulated PVs with 1,000 goods each.

`FullRankingDemo` is the complete demo:

1. a `Node` DAG loads the request and ranking config in parallel and validates
   the request;
2. the column graph computes CTR/CVR/GMV/coupon/retarget/delay metrics, applies
   the experiment-aware formula and selects top-k per PV;
3. greedy and beam-search rankers fill the final slate with category, brand and
   budget admissions.

The benchmark reports median/p95 for the DAG, column, ranking and total stages
after unmeasured warm-up iterations:

```sh
./gradlew :examples:runFullBenchmark
./gradlew :examples:runFullBenchmark \
    -Plary.full.candidates=2000 -Plary.full.warmup=20 -Plary.full.iterations=50
```
