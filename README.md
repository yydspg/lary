## stream project
直播，购物,聊天项目
### structure
1. [SRS](https://github.com/ossrs/srs)
2. [WuKongIM](https://github.com/WuKongIM/WuKongIM)
3. [SMS4j](https://github.com/dromara/SMS4J)
4. [sensitive-word](https://github.com/houbb/sensitive-word)
5. [canal](https://github.com/alibaba/canal)
6. [mybatis-plus](https://github.com/baomidou/mybatis-plus)
7. [retrofit-spring-boot-starter](https://github.com/LianjiaTech/retrofit-spring-boot-starter)
8. springboot,mysql,redis,rocketmq.etc
## timeline
start 2023/9/1

1. 移除jetcache
2. 移除redisTemplate
3. 移除kvBuilder
4. 添加自实现idGenerator
5. 优化System.currentMills
6. 使用redis订阅替换mq订阅本地缓存事件
## business
- 用户 y 
- 群组 y
- 直播 y
- 直播弹幕 y
- 支付 y
- 直播抽奖 y
- 钱包 y
- 订单全局二级索引 y
- 敏感词 y
- 直播红包 y
- 热点检测 x
- 直播首页 x
- 付费排行傍 x     
- 用户帖子 x
- 加群付费 x
- 购物首页推荐 x
- 直播首页推荐 x
- 直播间人气 x
- 直播间点赞数 x
- 用户等级 x
- 直播踢人 x
- 动态线程池 x
- 支付订单分表 y
- 直播内容审查 x
- 直播主页问题 x

## aim
v2 distribute,micro-service