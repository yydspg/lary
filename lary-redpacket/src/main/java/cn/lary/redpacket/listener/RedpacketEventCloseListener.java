package cn.lary.redpacket.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(topic = "lary-redpacket-event-close",consumerGroup = "lary-redpacket-event-close")
public class RedpacketEventCloseListener implements RocketMQListener<RedpacketEventAutoCloseMessage> {

//    private final MessageService messageService;
//    private final RedpacketEventService redpacketEventService;
//    private final EventService eventService;
    private final TransactionTemplate transactionTemplate;
    @Override
    public void onMessage(RedpacketEventAutoCloseMessage message) {

//        transactionTemplate.executeWithoutResult(status -> {
//            redpacketEventService.lambdaUpdate()
//                    .set(RedpacketEvent::getStatus, REDPACKET.STATUS.COMMIT)
//                    .eq(RedpacketEvent::getId,message.getRedPacketId());
//            eventService.commit(message.getRedPacketId());
//        });
//        messageService.send(new RedpacketEventCloseNotifyDTO(message.getUid(),message.getDanmakuId()));

    }
}
