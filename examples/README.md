# lary examples

This Gradle module depends on the root `lary` library and contains runnable
examples. It is intentionally separate from the library build.

```sh
./gradlew :examples:test
./gradlew :examples:runDemo
./gradlew :examples:runSimple
./gradlew :examples:runOp
./gradlew :examples:runNodes
./gradlew :examples:runRisk
```

## Realistic project layout

`io.lark.examples.risk` is structured as a small production-shaped Java
project:

```text
risk/
├─ RiskEngineDemo.java        composition + main
├─ RiskResultPrinter.java     presentation
├─ columns/                   typed ColumnStream subclasses
├─ node/                      one business node per file
├─ module/                    one module per file
└─ data/                      transaction repository
```

The other files (`LaryDemo`, `SimplePipeline`, `OpPipeline`,
`BusinessNodeExample`) are compact guided tours of the library layers.
