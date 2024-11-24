package cn.lary.payment.listener.recharge;

import cn.lary.payment.listener.AbstractDetectionMessageListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(consumerGroup = "test",topic = "test")
public class RechargeDetectionMessageListener implements RocketMQListener<RechargeActiveDetectionMessage> {


    private final AbstractDetectionMessageListener detectionMessageListener;
    @Override
    public void onMessage(RechargeActiveDetectionMessage message) {
        detectionMessageListener.process(message);
    }
}
