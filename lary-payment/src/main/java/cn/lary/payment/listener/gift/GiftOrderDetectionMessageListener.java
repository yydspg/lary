package cn.lary.payment.listener.gift;

import cn.lary.payment.listener.AbstractDetectionMessageListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(consumerGroup = "test",topic = "ss")
public class GiftOrderDetectionMessageListener implements RocketMQListener<GiftOrderActiveDetectionMessage> {

    private final AbstractDetectionMessageListener detectionMessageListener;
    @Override
    public void onMessage(GiftOrderActiveDetectionMessage message) {
        detectionMessageListener.process(message);
    }
}
