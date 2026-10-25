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
  (`src/main/java/io/lark/...`, tests under `src/test/java/io/lark/...`),
  `:examples` is an independent application module under
  `examples/src/main/java/io/lark/examples/...`, and `:spring` is an optional
  Spring Boot integration module under `spring/src/main/java/io/lark/spring/...`.
- Keep the root library and `:examples` self-contained: JDK 25 + JUnit 5. Only
  `:spring` may depend on Spring. Run examples with
  `./gradlew :examples:runRecsys` (or the other `:examples:` tasks); run the
  Spring demo with `./gradlew :spring:runSpringRecsys`.
- Column compute uses `jdk.incubator.vector`; keep the
  `--add-modules jdk.incubator.vector` flags in all three `build.gradle.kts`
  files.
- DAG business code uses virtual threads and blocking methods; do not turn
  `Node.run` / `Op.execute` back into callback-style APIs.
- Column business code declares `Column<T>` schema constants and uses
  `ColumnStream<T>` handles plus `ColumnNode` / `UnaryNode` / `BinaryNode` /
  `SourceNode` and `ColumnContext`; keep `io.lark.column.exec` and
  `io.lark.column.op` out of the business API. Business compute must not use
  string outputs or positional `feeds()[n]`. Single-output nodes expose a
  public static `NodeSchema<O> SCHEMA` and no explicit constructor; modules bind
  inputs with `node(new XNode(), inputs...)`. Modules compose nodes with
  `feed` / `node` / `fetch` and use `published(Column<T>)` for typed
  module-to-module wiring; `feedMap()` / `fetchMap()` are read-only boundary
  views. `OpTensor` is a typed tensor operation (`FloatAddTensor`,
  `FloatMultiTensor`, …); `ComputeNode` is the execution node that wraps it.
