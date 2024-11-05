package cn.lary.module.redpacket.listener;

import cn.lary.module.cache.component.RedpacketCacheComponent;
import cn.lary.module.redpacket.entity.RedpacketRuleCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedpacketLocalRuleListener implements RocketMQListener<RedpacketLocalRuleMessage> {

    private final RedpacketCacheComponent redpacketCacheComponent;

    @Override
    public void onMessage(RedpacketLocalRuleMessage message) {

        redpacketCacheComponent.setRule(message.getUid(),new RedpacketRuleCache()
                .setCategory(message.getCategory())
                .setLimit(new AtomicInteger(message.getLimit()))
        );
    }
}
