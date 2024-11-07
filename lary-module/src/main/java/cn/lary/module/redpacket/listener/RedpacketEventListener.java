package cn.lary.module.redpacket.listener;

import cn.lary.module.cache.component.RedpacketCacheComponent;
import cn.lary.module.redpacket.entity.RedpacketEventCache;
import cn.lary.module.redpacket.entity.RedpacketRuleCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
@RocketMQMessageListener(consumerGroup = "lary:redpacket:event",topic = "lary-redpacket-event")
@RequiredArgsConstructor
public class RedpacketEventListener implements RocketMQListener<RedpacketEventMessage> {

    private final RedpacketCacheComponent redpacketCacheComponent;

    @Override
    public void onMessage(RedpacketEventMessage message) {

        redpacketCacheComponent.setRedpacket(message.getCache().getUid(), message.getCache());

    }
}
