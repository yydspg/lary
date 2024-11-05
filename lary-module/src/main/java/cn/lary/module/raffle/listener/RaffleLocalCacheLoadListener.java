package cn.lary.module.raffle.listener;

import cn.lary.module.cache.component.RaffleCacheComponent;
import cn.lary.module.raffle.entity.RaffleRuleCache;
import lombok.RequiredArgsConstructor;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@RocketMQMessageListener(consumerGroup = "lary:raffle:local:cache",topic = "lary-raffle-local-cache")
public class RaffleLocalCacheLoadListener implements RocketMQListener<RaffleRuleLocalCacheMessage> {

    private final RaffleCacheComponent raffleCacheComponent;

    @Override
    public void onMessage(RaffleRuleLocalCacheMessage message) {
        raffleCacheComponent.setRule(message.getUid(),
                new RaffleRuleCache()
                        .setShard(message.getShard())
                        .setLimit(message.getLimit()));
    }
}
