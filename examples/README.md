# lary examples

This Gradle module depends on the root `lary` library and contains runnable
examples. It is intentionally separate from the library build.

```sh
./gradlew :examples:test
./gradlew :examples:runSimple
./gradlew :examples:runOp
./gradlew :examples:runRecsys
```

## Realistic project layout

`io.lark.examples.recsys` is structured as a small production-shaped Java
project for search / recommendation metrics:

```text
recsys/
├─ RecSysDemo.java            composition + main
├─ MetricResultPrinter.java   presentation
├─ columns/                   typed ColumnStream subclasses
├─ node/                      one business node per file
├─ module/                    one module per file
└─ data/                      item-level metric repository
```

The graph computes CTR, CVR, GMV, buy-again cost, coupon ROI and a ranking
weight, then aggregates the portfolio. There are no user/item id columns: ids
are lookup keys, not inputs to columnar metric computation.

`SimplePipeline` and `OpPipeline` are compact tours of the DAG layers.
