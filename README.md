## stream project

start 2024/9/1

## 业务点
- 用户 y 
- 群组 y
- 直播 y
- 直播弹幕 y
- 支付 y
- 直播抽奖 y
- 直播红包 x
- 加群付费 x
- 用户等级 x
- 钱包扣款 y
- 直播踢人 x
- 动态线程池 x
- 支付订单分表 x
- 直播内容审查 x
- 直播秒杀问题 x
- 直播主页问题 x
- 引入dfa敏感词检测框架 y
## 版本
v1 就做单表的项目，主要现在代码都写在 控制器里了，不好升级
20240915 处理用户关注的逻辑，取消关注需要被关注者同意的逻辑
## 目标
v2 水平分表，做做分布式，目前了解到的mp不太好做扩展