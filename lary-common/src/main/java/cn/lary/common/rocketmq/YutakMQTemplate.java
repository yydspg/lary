package cn.lary.common.rocketmq;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.support.GenericMessage;

@Slf4j
public class YutakMQTemplate {

    private RocketMQTemplate rocketMQTemplate;

    public YutakMQTemplate() {}

    public YutakMQTemplate(RocketMQTemplate rocketMQTemplate) {
        this.rocketMQTemplate = rocketMQTemplate;
    }

    public final void syncSendRocketMessage(AbstractSyncRocketMessage message) {
        GenericMessage rocketMQMessage = new GenericMessage<>(message);
        try {
            rocketMQTemplate.syncSend("lary:raffle-close",rocketMQMessage,message.getTimeOut(),message.getDelayLevel());
        } catch (Exception e) {
            log.error("sync send {} message error,reason:{}",message.getBusinessSign(),e.getMessage());
        }
        log.info("sync send {} message success,:{}",message.getBusinessSign(),message.getLogData());
    }


    public final void asyncSendRocketMessage(AbstractAsyncRocketMessage message) {
        GenericMessage rocketMQMessage = new GenericMessage<>(message);
        try {
            rocketMQTemplate.asyncSend("lary:raffle-close",rocketMQMessage,message.getSendCallback(),message.getTimeOut(),message.getDelayLevel());
        } catch (Exception e) {
            log.error("async send {} message error,reason:{}",message.getBusinessSign(),e.getMessage());
        }
        log.info("async send {} message success,:{}",message.getBusinessSign(),message.getLogData());
    }

}
