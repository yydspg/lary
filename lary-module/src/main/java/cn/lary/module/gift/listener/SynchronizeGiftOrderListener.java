package cn.lary.module.gift.listener;

import cn.lary.module.gift.entity.AnchorFLow;
import cn.lary.module.gift.entity.GiftOrder;
import cn.lary.module.gift.service.AnchorFlowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(consumerGroup = "test",topic = "eee")
public class SynchronizeGiftOrderListener implements RocketMQListener<SynchronizeGiftOrderMessage> {

    private final AnchorFlowService anchorFlowService;

    @Override
    public void onMessage(SynchronizeGiftOrderMessage message) {
        GiftOrder order = message.getGiftOrder();
        AnchorFLow income = new AnchorFLow().of(order);
        anchorFlowService.save(income);
    }
}