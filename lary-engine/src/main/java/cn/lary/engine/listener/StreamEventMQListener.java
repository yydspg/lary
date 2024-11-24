package cn.lary.engine.listener;

import cn.lary.engine.constant.EVENT;
import cn.lary.engine.intime.RankOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(consumerGroup = "lary:engine:rank",topic = "lary:engine:rank")
public class StreamEventMQListener implements RocketMQListener<StreamEventMessage> {

    private final RankOperation operation;

    @Override
    public void onMessage(StreamEventMessage message) {
        if (message.getEventType() == EVENT.UP) {
            operation.up(message);
        }
        if (message.getEventType() == EVENT.DOWN) {
            operation.down(message);
        }
    }
}
