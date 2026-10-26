# lary — Java 25 DAG + 列式计算引擎

[English README](README.md)

`lary` 是一个独立的 Java 25 实现，包含 DAG 框架和列式计算引擎。核心库不依赖
Spring；`:examples` 是示例工程；`:spring` 是可选的 Spring Boot 集成模块。

## 主要能力

- **DAG / Op**：虚拟线程承载阻塞式业务代码，支持 Node / Op 两层 API、Aspect、
  注册表、KV 与箭头 DSL、统计、超时、fallback 和连续超时隔离。
- **列式计算**：`Column<T>` schema、`ColumnStream<T>`、`ColumnNode` /
  `ColumnModule`、`ColumnPipeline`。
- **分层执行**：`OpSpec`（图描述）→ `ComputeNode`（调度/绑定）→
  `TensorKernel`（纯执行算子）→ `Tensor`（数据）。
- **张量**：底层仍是一维原始数组，通过 `TensorShape` 描述 1–3 维形状，例如
  `[batch, candidate, feature]`。
- **SIMD**：算术、标量广播、比较、归约和一元运算使用
  `jdk.incubator.vector`。
- **可观测性**：`RunStats` / `StatsCollector` 记录阶段、形状、节点数、执行模式、
  状态和耗时。

## 构建与运行

```sh
./gradlew clean test :examples:build :spring:build

./gradlew :examples:runSearch          # 张量感知的搜索/重定向示例
./gradlew :examples:runRecsys          # 搜索推荐指标示例
./gradlew :examples:runRecsysBenchmark # warm-up 后的稳定基准
./gradlew :examples:runSimple          # DAG Node 示例
./gradlew :examples:runOp              # DAG Op + Aspect 示例
./gradlew :spring:runSpringRecsys      # Spring Boot 版本
```

需要 JDK 25。列式计算和测试使用 `jdk.incubator.vector`，构建脚本已经配置。

## 张量形状

```java
TensorShape shape = TensorShape.of(2, 3, 4); // 最高 3 维
Tensor a = Tensor.fromDoubles(shape, 1, 2, 3, /* ... 共 24 个值 */);
Tensor b = Tensor.fromDoubles(shape, 4, 5, 6, /* ... 共 24 个值 */);

Tensor sum = Compute.add(a, b);
Tensor product = Compute.mul(a, b);

assert sum.shape().equals(shape);
```

- 二元 add/sub/mul/div 和比较支持右对齐的 NumPy 风格广播，例如
  `[2,3] + [3]`、`[2,3] + [2,1]`。
- add/mul 满足交换律；sub/div 保持操作数顺序。无法广播的维度会立即报错。
- 三元运算 `score * perturbation + originalScore` 使用
  `Compute.mulAdd(score, perturbation, originalScore)`；Op 层对应
  `mul_add` / `fma`。
- 标量运算、一元运算、cast、quantize/dequantize 会保留输入形状。
- `Tensor.zeros(shape, dtype)` 创建任意 1–3 维置零张量。
- `fetchTensor(...)` 直接返回列式 `Tensor`，不经过行/单元格转换。

## 分层执行

```text
ColumnNode / ColumnModule / ColumnPipeline   业务编排
             ↓
OpSpec                                      底层计算图描述
             ↓
ComputeNode / ComputeGraph                  调度与 store 绑定
             ↓
TensorKernel / KernelContext                实际执行算子
             ↓
Tensor                                      数据容器（扁平数组 + shape）
```

新的内部执行路径使用 `OpSpec` + `TensorKernel`。`TensorKernel` 只接收
`KernelContext` 中已解析的输入并输出张量，不接触节点 id、依赖边或输入输出名。
二元和三元算子共享广播规则；`mul_add` 就是常见的
`score * perturbation + originalScore`。`OpTensor` / `TensorOps` 作为兼容
API 保留。

Op 层支持声明式零张量：

```java
OpTensor zeros = TensorOps.create(new OpSpec(
        "zeros", List.of(), List.of("z"),
        Map.of("dtype", "float64", "shape", "2,3")));
zeros.compute(new OpContext(store));
```

业务层可以使用 `ZerosNode`：

```java
ColumnStream<Double> zeros =
        node(new ZerosNode<>(FEATURES, TensorShape.of(rows, 3))).out();
```

## 搜索系统示例

`SearchDemo` 模拟一个搜索/重定向系统，主要模块如下：

| 模块 | 计算 | 输出形状 |
|---|---|---|
| `CtrModule` | `clicks / impressions` | 1 维 |
| `CvrModule` | `orders / clicks` | 1 维 |
| `CtCvrModule` | `ctr * cvr` | 1 维 |
| `RebuySameLossModule` | `1 - buyagain` | 1 维 |
| `BuyAgainModule`（buyagain） | `buy_again_orders / orders` | 1 维 |
| `CouponRoiModule` | `gmv / coupon_cost` | 1 维 |
| `RetargetAdModule` | 点击占比、成本占比、ROI | 2 维 `[rows, 3]` |
| `GmvModule` | `gmv_fen / 100` | 1 维 |
| `DelayModule` | 订单/GMV 准时与延迟占比 | 3 维 `[rows, 2, 2]` |

运行：

```sh
./gradlew :examples:runSearch
```

示例会输出：

- CTR、CVR、CTCVR、buyagain、rebuy_same_loss、couponroi、GMV 的一维结果表；
- `retargetAd` 的 `5x3` 张量形状及每行三个指标；
- `delay` 的 `5x2x2` 张量形状及订单/GMV 的准时/延迟占比；
- `RunStats` 阶段耗时、节点数、成功/失败状态和 CPU 压力。

## 业务 API 使用建议

```java
try (ColumnPipeline pipeline = new ColumnPipeline(4)) {
    pipeline.addAll(modules)
            .compile()
            .feed(feed)
            .compute();

    List<Row> rows = pipeline.fetch(FetchStream.of(ctr, cvr, gmv));
    Tensor retarget = pipeline.fetchTensor(retargetAd);
    Map<String, List<Cell>> allColumns = pipeline.fetchColumns();
}
```

- 每个并发运行使用独立 `ColumnPipeline`；pipeline 本身不是线程安全的。
- 已有列式数据时使用 `feedTensors(Map<String, Tensor>)`，避免 cell 转置。
- 大结果或张量结果优先使用 `fetchTensor` / `fetchTensors`。
- 性能对比使用 `:examples:runRecsysBenchmark`，不要用单次小样本 `RunStats`
  直接判断吞吐。
- 阻塞型列节点可设置 `setVirtualComputeThreads(true)`，或提高
  `compute-workers`。

## 目录

```text
lary/
├─ src/main/java/io/lark/
│  ├─ metric/      Event / Monitor
│  ├─ dag/         DAG / Op 调度
│  └─ column/
│     ├─ data/      Tensor / TensorShape / store / row-cell
│     ├─ compute/   Compute / VectorOps / Simd
│     ├─ kernel/    TensorKernel / KernelContext / TensorKernels
│     ├─ op/        OpSpec + 兼容 OpTensor/TensorOps
│     ├─ exec/      ComputeNode / ComputeGraph
│     ├─ biz/       ColumnNode / Module / Pipeline
│     └─ monitor/   RunStats / ColumnStatsCollector
├─ examples/
│  └─ src/main/java/io/lark/examples/
│     ├─ search/   张量感知的搜索/重定向示例
│     └─ recsys/   搜索推荐指标示例
└─ spring/         可选 Spring Boot 集成
```

## 维护约定

行为或 API 变更需要同步更新：

1. `README.md` / `README.zh-CN.md`；
2. `.opencode/skills/java-port-sync/SKILL.md`；
3. 测试，以及用户可见变更对应的示例。

Java 版本以 C++ `../lark` 的 `dag/`、`column/` 为语义来源；本仓库不修改 C++。
