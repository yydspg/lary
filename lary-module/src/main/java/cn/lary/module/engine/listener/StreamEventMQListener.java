package cn.lary.module.engine.listener;

import cn.lary.module.common.constant.LARY;
import cn.lary.module.engine.intime.RankOperation;
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
        if (message.getEventType() == LARY.STREAM.EVENT.UP) {
            operation.up(message);
        }
        if (message.getEventType() == LARY.STREAM.EVENT.DOWN) {
            operation.down(message);
        }
    }
}
