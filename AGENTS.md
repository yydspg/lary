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
- This is a single Gradle project (no subprojects): sources live under
  `src/main/java/io/lark/...`, tests under `src/test/java/io/lark/...`.
- Keep the project self-contained: JDK 25 + JUnit 5.
- Column compute uses `jdk.incubator.vector`; keep the
  `--add-modules jdk.incubator.vector` flags in `build.gradle.kts`.
- DAG business code uses virtual threads and blocking methods; do not turn
  `Node.run` / `Op.execute` back into callback-style APIs.
- Column business code uses `ColumnNode` + `ColumnContext`; keep
  `io.lark.column.exec` and `io.lark.column.op` out of the business API.
  `OpTensor` is a typed tensor operation (`FloatAddTensor`,
  `FloatMultiTensor`, …); `ComputeNode` is the execution node that wraps it.
