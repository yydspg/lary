# Repository Rules

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
