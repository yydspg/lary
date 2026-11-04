# lary — Java 25 DAG + 列式计算引擎

[English README](README.md)

`lary` 是一个独立的 Java 25 实现，包含 DAG 框架、列式计算引擎和排序框架。
核心库不依赖 Spring；`:examples` 是示例工程；`:spring` 是可选的 Spring Boot
集成模块。

## 主要能力

- **DAG / Op**：虚拟线程承载阻塞式业务代码，支持 Node / Op 两层 API、Aspect、
  注册表、KV 与箭头 DSL、统计、超时、fallback 和连续超时隔离。
- **列式计算**：`Column<T>` schema、`ColumnStream<T>`、`ModuleNode<O>` /
  `ColumnModule`、`ColumnPipeline`。
- **排序**：`SequentialRanker`（贪心）、`BeamSearchRanker`（束搜索）和
  `BacktrackingRanker`，支持准入规则、索引评分和稳定基数排序。
- **分层执行**：`OpSpec`（图描述）→ `ComputeNode`（调度/绑定）→
  `TensorKernel`（纯执行算子）→ `Tensor`（数据）。
- **张量**：底层仍是一维原始数组，通过 `TensorShape` 描述 1–3 维形状。
- **SIMD**：算术、标量广播、比较、归约和一元运算使用
  `jdk.incubator.vector`。
- **可观测性**：`RunStats` / `StatsCollector` 记录阶段、形状、节点数、执行模式、
  状态和耗时。

## 构建与运行

```sh
./gradlew clean test :examples:build :spring:build

./gradlew :examples:runSimple          # DAG Node 示例
./gradlew :examples:runOp              # DAG Op + Aspect 示例
./gradlew :examples:runSearch          # 行数据输入 -> PV top-k 搜索图
./gradlew :examples:runRank            # 贪心 / 束搜索排序
./gradlew :examples:runFullDemo        # DAG + 列式计算 + 排序完整链路
./gradlew :examples:runFullBenchmark   # warm-up 后的稳定基准
./gradlew :spring:runSpringSearch      # Spring 自动发现搜索图版本
```

需要 JDK 25。列式计算和测试使用 `jdk.incubator.vector`，构建脚本已经配置。

## 完整 demo：DAG → 列式计算 → 排序

`FullRankingDemo` 演示一次完整请求：

1. **DAG**：`load_request` 与 `load_config` 并行执行，`validate` 依赖两者，
   产出类型化的 `Prepared`。`StatsCollector` 记录每个节点的耗时和状态。
2. **列式计算**：`SearchModule` 从原始行计算 CTR/CVR/GMV/优惠券/重定向/延迟
   指标，执行实验感知业务节点和 `score * perturbation + originalScore`，最后用
   `TopKOp` 按 `pv_id` 选出 top-k。
3. **排序**：`SequentialRanker` 和 `BeamSearchRanker` 在选中的候选上填充最终
   slate，应用品类间隔、品牌配额和预算准入规则。两者都使用
   `IndexedSlotScorer`，候选对象不移动，每个 slot 不分配中间候选列表。

```java
DemoResult result = FullRankingDemo.run(300);

result.prepared().dagStats();    // DAG 阶段耗时与状态
result.column().selectedCount(); // PV top-k 选中的数量
result.column().candidates();    // 按基础分排序的选中商品
result.greedy();                 // RankingResult<ScoredGoods>
result.beam();                   // RankingResult<ScoredGoods>
```

运行 `./gradlew :examples:runFullDemo` 会打印 DAG 瀑布图、列式计算阶段耗时和
CPU 压力，以及贪心/束搜索的最终 slate。默认 300 个候选、top-30。

### 稳定基准

单次 `RunStats` 包含 JIT、类加载和构图开销，不适合直接做吞吐结论。使用：

```sh
./gradlew :examples:runFullBenchmark
./gradlew :examples:runFullBenchmark \
    -Plary.full.candidates=2000 -Plary.full.warmup=20 -Plary.full.iterations=50
```

`FullRankingBenchmark` 先丢弃 warm-up，再输出总请求、DAG、列式计算和两个排序器
的中位数、p95、最小值和最大值。每次请求都会新建 `ColumnPipeline` 和
`SearchModule`，因此包含构图/编译成本。默认 1,000 个候选、8 次 warm-up、
20 次采样。

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
ModuleNode / ColumnModule / ColumnPipeline   业务编排
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
`OpTensor` / `TensorOps` 作为兼容 API 保留。

声明式 `OpNode` 在 `PipelineCompiler` 中直接绑定 `TensorKernel`，避免业务
`ColumnContext` 和额外 `KernelContext` 的中转；只有自定义节点走
`BusinessNodeKernel`。

## 业务节点定义

业务侧通过声明式算子类和类型化 `ColumnStream` 定义节点：

```java
var ctr = node(SearchSchema.CTR.doubleColumn(), RatioOp.class, clicks,
        impressions);
var cvr = node(SearchSchema.CVR.doubleColumn(), RatioOp.class, orders,
        clicks);
var ctcvr = node(SearchSchema.CTCVR.doubleColumn(), MulOp.class, ctr, cvr);
```

- 每个 node 只绑定一个 `ColumnOperator`。
- 输入可以是 module feed，也可以是其他 node 的 `ColumnStream` 输出。
- 内置算子包括 `AddOp`、`SubOp`、`MulOp`、`DivOp`、`RatioOp`、`MulAddOp`、
  `OneMinusOp`、`ScaleOp`、`ExperimentScaleOp`、`VariantSwitchOp`、
  `ParamOp`、`StackOp`、`TopKOp`；`TopKOp` 按 PV/分组列输出 INT64 标记。
- 复杂业务可以扩展 `ColumnOperator` 或 `ModuleNode<O>`；`OpNode` /
  `TensorKernel` 是框架内部细节。

## 搜索示例

`SearchDemo` 模拟一次完整搜索请求：

1. `SearchContext` 保存 `pvId`、`userId`、`query`、`topK` 和
   `ExperimentContext`。
2. `SearchDemo.simulate(context, 1_000)` 生成 1,000 个 `GoodsDO`，每行带曝光、
   点击、订单、GMV、优惠券成本、重定向和延迟计数。
3. `SearchDemo.rows(request)` 把每行转成 `SearchRow(pvId, goods)`；
   `SearchModule` 用 `feed(Column<T>, getter)` 直接构建原始张量。
4. 模块图计算指标、实验感知排序分、`score * perturbation + originalScore`，
   并按 `pv_id` 选出 top 30。
5. 选中列回取为行，再映射为类型化 `SearchResult.RankedGoods`。

```java
SearchContext context = SearchDemo.sampleContext();
SearchRequest request = SearchDemo.simulate(context, 1_000);

SearchResult result = new SearchModule(context.topK(), 4).search(request);
for (SearchResult.RankedGoods goods : result.ranked()) {
    System.out.printf("%d. goods=%d score=%.4f%n",
            goods.rank(), goods.goodsId(), goods.adjustedScore());
}
```

`SearchModule` 是 `@ColumnComponent`。Spring 场景下 `@EnableLaryColumns` 会
发现模块，`ColumnPipelineFactory` 为每次请求创建新的可变 pipeline：

```java
@SpringBootApplication(proxyBeanMethods = false)
@EnableLaryColumns(basePackages = "io.lark.examples.search")
class App {
}
```

运行：`./gradlew :spring:runSpringSearch`。

主要输出列：

| 输出 | 计算 | 形状 |
|---|---|---|
| `ctr` | `clicks / impressions` | 1 维 |
| `cvr` | `orders / clicks` | 1 维 |
| `ctcvr` | `ctr * cvr` | 1 维 |
| `gmv` | `gmv_fen / 100` | 1 维 |
| `buyagain` | `buy_again_orders / orders` | 1 维 |
| `rebuy_same_loss` | `1 - buyagain` | 1 维 |
| `couponroi` | `gmv / coupon_cost` | 1 维 |
| `retargetAd` | 点击占比、成本占比、ROI | 2 维 `[rows, 3]` |
| `delay` | 订单/GMV 准时与延迟占比 | 3 维 `[rows, 2, 2]` |
| `ranking_score` | 加权公式 | 1 维 |
| `adjusted_score` | `ranking_score * perturbation + original_score` | 1 维 |
| `selected` | 按 `pv_id` 分组的 top-k 标记 | 1 维 INT64 |

### 实验感知的业务节点

`ModuleNode<O>` 是带泛型的单输出节点基类。需要多个输入、行级条件或实验参数的
业务规则应该写在业务节点里，而不是 operator 里：

```java
public final class RankingScoreNode extends ModuleNode<Double> {
    @Override
    public void compute(ColumnContext ctx) {
        ExperimentContext experiment = ctx.experiment();
        Tensor score = Compute.mulScalar(ctx.read(ctr),
                experiment.param("rerank.ctr_w", 100.0));
        score = Compute.add(score, Compute.mulScalar(ctx.read(cvr),
                experiment.param("rerank.cvr_w", 50.0)));
        score = Compute.add(score, Compute.mulScalar(ctx.read(gmv),
                experiment.param("rerank.gmv_w", 0.01)));
        ctx.write(out(), score);
    }
}
```

每个请求通过 `ExperimentContext` 携带实验层和参数，
`ColumnPipeline.setExperiment(...)` 将其通过 `ColumnContext` 传给节点。权重或
变体变化不需要重新构图。

Operator 保持小而通用：`AddOp`、`SubOp`、`MulOp`、`DivOp`、`RatioOp`、
`ScaleOp`、`MulAddOp`、`OneMinusOp`、`StackOp`、`TopKOp`，以及实验修饰
`ExperimentScaleOp` / `VariantSwitchOp` / `ParamOp`。条件、多输入或领域相关的
打分逻辑放在业务节点中，例如搜索 demo 的 `RankingScoreNode`、排序 demo 的
`BaseScoreNode` / `PenaltyScoreNode`。

列式计算引擎不再提供公式/表达式 DSL。应用如果需要外部配置，应加载自己的配置，
在启动时选择节点/算子接线或参数；根库不携带表达式树解释器。

如果实验需要完全不同的图拓扑，用 `ExperimentPipelines` 为每个变体注册
pipeline supplier，再按请求路由到对应的新 pipeline。

## 性能设计

三个调度器都缓存结构计划，只在图结构变化时重建：

- `Graph` 在构图时生成 `GraphPlan`（入度、反向邻接、根节点），每次
  `Executor.execute` 只复制原始计数器数组。
- `OpGraph.compile()` 生成可复用的 `Plan`；Aspect 以缓存数组暴露，避免每个 op
  做一次 `List.copyOf`。
- `ComputeGraph.finalizeGraph()` 生成 `Plan`（插入序索引、反向邻接、根节点、
  输出名和模块索引），重复执行不再创建 identity map，也不在热路径用
  `ConcurrentHashMap` 查模块。
- ready queue 直接存节点引用，避免 index 装箱。
- `Executor` / `OpExecutor` 使用 `FutureTask.done()` 统一处理成功、失败和
  “启动前被取消”，超时/中止不会漏完成，也不再有每节点 `CompletableFuture`
  链。
- `ColumnPipeline` 的声明式 `OpNode` 直接绑定 kernel；`FeedMapping` 预计算
  getter/dtype 数组并直接遍历行列表。

阻塞型列节点可设置 `setVirtualComputeThreads(true)`（Spring：
`lary.column.virtual-threads: true`）；否则使用按 `compute-workers` 固定大小
的平台线程池。每个并发运行使用独立 `ColumnPipeline`。

## 目录

```text
lary/
├─ src/main/java/io/lark/
│  ├─ metric/      Event / Monitor
│  ├─ dag/         DAG / Op 调度
│  ├─ rank/        贪心 / 束搜索 / 回溯排序
│  └─ column/
│     ├─ tensor/    Tensor / TensorShape / store / FeedFetch / TensorKernel / TensorKernels
│     ├─ data/      Cell / Row
│     ├─ compute/   Compute / VectorOps / Simd
│     ├─ op/        OpSpec + 兼容 OpTensor/TensorOps
│     ├─ exec/      ComputeNode / ComputeGraph
│     ├─ biz/       ModuleNode / Module / Pipeline
│     │  ├─ schema/     Column / NodeSchema
│     │  ├─ operator/   通用 operator
│     │  ├─ flow/       FeedStream / FetchStream
│     │  ├─ component/  @ColumnComponent + scanner
│     │  └─ experiment/ ExperimentPipelines
│     └─ monitor/   RunStats / ColumnStatsCollector
├─ examples/
│  └─ src/main/java/io/lark/examples/
│     ├─ search/    搜索 model / schema / module / node / result
│     ├─ rank/      排序示例
│     └─ demo/      完整链路 demo + 基准（model/dag/column/rank）
└─ spring/          可选 Spring Boot 集成
```

## 维护约定

行为或 API 变更需要同步更新：

1. `README.md` / `README.zh-CN.md`；
2. `.opencode/skills/java-port-sync/SKILL.md`；
3. 测试，以及用户可见变更对应的示例。

Java 版本以 C++ `../lark` 的 `dag/`、`column/` 为语义来源；本仓库不修改 C++。
