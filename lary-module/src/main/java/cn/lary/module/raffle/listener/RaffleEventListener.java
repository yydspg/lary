package cn.lary.module.raffle.listener;

import cn.lary.module.cache.component.RaffleCacheComponent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(consumerGroup = "lary:redpacket:event",topic = "lary-redpacket-event")
public class RaffleEventListener implements RocketMQListener<RaffleEventMessage> {

    private final RaffleCacheComponent raffleCacheComponent;

    @Override
    public void onMessage(RaffleEventMessage message) {
        raffleCacheComponent.setRaffle(message.getCache().getUid(), message.getCache());
    }
}
