# Repository Rules

## Maintenance contract

Every behavior or API change must update, in the same commit:

1. `README.md` — feature, usage and limitations.
2. `.opencode/skills/java-port-sync/SKILL.md` — mapping, invariants and
   verification steps.
3. Tests, plus an example when the change is user-facing.

## Java port of lark

- The C++ `dag/` and `column/` modules in the sibling `../lark` repository are
  the source of truth.
- When changing lark or lary, load the `java-port-sync` skill
  (`.opencode/skills/java-port-sync/SKILL.md`) and update the implementation,
  tests, examples and docs together.
- This is a multi-project Gradle build: the root project is the library
  (`src/main/java/io/lark/...`, tests under `src/test/java/io/lark/...`) and
  `:examples` is an independent application module under
  `examples/src/main/java/io/lark/examples/...`.
- Keep the project self-contained: JDK 25 + JUnit 5. Run examples with
  `./gradlew :examples:runRisk` (or the other `:examples:` tasks).
- Column compute uses `jdk.incubator.vector`; keep the
  `--add-modules jdk.incubator.vector` flags in both `build.gradle.kts` files.
- DAG business code uses virtual threads and blocking methods; do not turn
  `Node.run` / `Op.execute` back into callback-style APIs.
- Column business code uses typed `ColumnStream` subclasses (`LongColumn`,
  `DoubleColumn`, …), `ColumnNode` / `UnaryNode` / `BinaryNode` / `SourceNode`
  and `ColumnContext`; keep `io.lark.column.exec` and `io.lark.column.op` out of
  the business API. Business compute must not use string outputs or positional
  `feeds()[n]`. `OpTensor` is a typed tensor operation (`FloatAddTensor`,
  `FloatMultiTensor`, …); `ComputeNode` is the execution node that wraps it.
