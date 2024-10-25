package cn.lary.module.gift.listener;

import cn.lary.module.common.constant.LARY;
import cn.lary.module.gift.entity.AnchorIncome;
import cn.lary.module.gift.entity.GiftOrder;
import cn.lary.module.gift.service.AnchorIncomeService;
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

    private final AnchorIncomeService anchorIncomeService;

    @Override
    public void onMessage(SynchronizeGiftOrderMessage message) {
        GiftOrder order = message.getGiftOrder();
        AnchorIncome income = new AnchorIncome().of(order);
        anchorIncomeService.save(income);
    }
}
