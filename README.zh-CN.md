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

./gradlew :examples:runSearch          # 行数据输入 -> PV top-k 搜索示例
./gradlew :examples:runRecsys          # 搜索推荐指标示例
./gradlew :examples:runRecsysBenchmark # warm-up 后的稳定基准
./gradlew :examples:runSimple          # DAG Node 示例
./gradlew :examples:runOp              # DAG Op + Aspect 示例
./gradlew :spring:runSpringRecsys      # Spring Boot 版本
./gradlew :spring:runSpringSearch      # Spring 自动发现搜索图版本
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

## 业务节点定义

业务侧通过 `ColumnOps` 定义节点，不接触 `Tensor`、shape 或 kernel：

```java
private final ColumnStream<Double> ctr =
        node(ColumnOps.ratio(SearchMetrics.CTR), clicks, impressions);
private final ColumnStream<Double> cvr =
        node(ColumnOps.ratio(SearchMetrics.CVR), orders, clicks);
private final ColumnStream<Double> ctcvr =
        node(ColumnOps.mul(SearchMetrics.CTCVR), ctr, cvr);
```

- 每个 node 只绑定一个 `ColumnOp`。
- node 输入可以是 module feed，也可以是其他 node 的 `ColumnStream` 输出。
- `feed(...)` / `output(...)` 属于 module，描述整个模块的输入输出边界。
- 内置算子包括 `add`、`sub`、`mul`、`ratio`、`mulAdd`、`oneMinus`、
  `scale`、`topK`；`topK` 按 PV/分组列选取并输出 INT64 标记。
- 复杂自定义公式可以通过扩展 `ColumnOp` 实现，`OpNode`/`TensorKernel` 是框架内部细节。

## 搜索系统示例

`SearchDemo` 模拟一次完整搜索请求：

1. `SearchContext` 保存 `pvId`、`userId`、`query` 和 `topK`。
2. `SearchRepository` 生成一个包含 1,000 个 `GoodsDO` 的 `GoodsList`，
   每行带曝光、点击、订单、GMV、优惠券成本、重定向和延迟计数；`GoodsDO`
   还提供 `ctr()`、`cvr()`、`gmv()`、`couponCost()` 行级访问方法。
3. `SearchFeed` 把 context 和 goods 行展开为带类型的输入流并喂给计算图。
4. 模块图计算指标、生成排序分、执行
   `score * perturbation + originalScore`，最后按 `pv_id` 选出 top 30。
5. 选中的 `goods_id`、调整后分数和选择标记先取回为行数据，再由
   `SearchRankingService` 映射成类型化的 `RankedGoods`。

业务代码不需要自己组装 module，也不需要读 `Row.get(2)` 这种位置化字段：

```java
SearchContext context = SearchRepository.sampleContext();
SearchRequest request = SearchRepository.simulate(context, 1_000);

SearchRankingService service = SearchRankingService.create(context.topK(), 4);
SearchResult result = service.search(request);

for (RankedGoods goods : result.ranked()) {
    System.out.printf("%d. goods=%d score=%.4f%n",
            goods.rank(), goods.goodsId(), goods.adjustedScore());
}
```

使用 Spring 时，模块构造可以完全交给容器。搜索模块都标了
`@ColumnComponent`，`@EnableLaryColumns` 会扫描并注册，`ColumnPipelineFactory`
会自动收集所有 module 并注入构造函数依赖，业务代码不需要手写 module 列表。
仍然显式保留的只有运行时数据和配置：

```java
@SpringBootApplication
@EnableLaryColumns(basePackages = "io.lark.examples.search.module")
class App {
    @Bean SearchRankingOptions options() { return new SearchRankingOptions(30); }
    // SearchInputModule、AdjustedScoreModule、TopKModule 等由 Spring 注入
}
```

`SearchRankingService` 本身也是普通 Spring Bean，pipeline 通过
`ColumnPipelineFactory::create` 按请求创建，避免共享可变 pipeline。运行：
`./gradlew :spring:runSpringSearch`。

| 模块 | 计算 | 输出形状 |
|---|---|---|
| `SearchInputModule` | `pv_id`、`goods_id`、原始计数、perturbation、original_score 输入流；id 同时发布供结果回取 | 1 维输入流 |
| `CtrModule` | `clicks / impressions` | 1 维 |
| `CvrModule` | `orders / clicks` | 1 维 |
| `CtCvrModule` | `ctr * cvr` | 1 维 |
| `RebuySameLossModule` | `1 - buyagain` | 1 维 |
| `BuyAgainModule`（buyagain） | `buy_again_orders / orders` | 1 维 |
| `CouponRoiModule` | `gmv / coupon_cost` | 1 维 |
| `RetargetAdModule` | 点击占比、成本占比、ROI | 2 维 `[rows, 3]` |
| `GmvModule` | `gmv_fen / 100` | 1 维 |
| `DelayModule` | 订单/GMV 准时与延迟占比 | 3 维 `[rows, 2, 2]` |
| `RankingScoreModule` | `100*ctr + 50*cvr + 0.01*gmv + 0.5*couponroi - 0.5*rebuy_same_loss` | 1 维 |
| `AdjustedScoreModule` | `ranking_score * perturbation + original_score` | 1 维 |
| `TopKModule` | 按 `pv_id` 分组的 top-k 选择标记 | 1 维 INT64 |

PV 级别整合也是一个普通框架节点：`ColumnOps.topK` 接收分数列和 `pv_id`
分组列，输出每行的 INT64 选择标记。示例运行单个 PV、1,000 个候选商品；测试
会喂入两个 PV、每个 1,000 个商品，并校验每个 PV 恰好选中 30 行。分数相同
时按原始行序稳定排序，因此重复运行结果一致。

运行：

```sh
./gradlew :examples:runSearch
```

示例会输出：

- 按调整后分数排序的 top 30 商品及其 `goods_id`；
- `retargetAd` 的 `1000x3` 张量形状及前几行指标；
- `delay` 的 `1000x2x2` 张量形状及前几行订单/GMV 的准时/延迟占比；
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

- 一个 Module 可以通过 `dependsOn(A.class, B.class, ...)` 依赖多个 Module；
  Module 依赖图必须是有向无环图，出现环会在 `compile()` 时报错。
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
│     ├─ tensor/    Tensor / TensorShape / store / FeedFetch / TensorKernel / TensorKernels
│     ├─ data/      Cell / Row
│     ├─ compute/   Compute / VectorOps / Simd
│     ├─ op/        OpSpec + 兼容 OpTensor/TensorOps
│     ├─ exec/      ComputeNode / ComputeGraph
│     ├─ biz/       ColumnNode / Module / Pipeline
│     └─ monitor/   RunStats / ColumnStatsCollector
├─ examples/
│  └─ src/main/java/io/lark/examples/
│     ├─ search/   搜索排序示例（行数据输入 -> PV top-k）
│     └─ recsys/   搜索推荐指标示例
└─ spring/         可选 Spring Boot 集成
```

## 维护约定

行为或 API 变更需要同步更新：

1. `README.md` / `README.zh-CN.md`；
2. `.opencode/skills/java-port-sync/SKILL.md`；
3. 测试，以及用户可见变更对应的示例。

Java 版本以 C++ `../lark` 的 `dag/`、`column/` 为语义来源；本仓库不修改 C++。
