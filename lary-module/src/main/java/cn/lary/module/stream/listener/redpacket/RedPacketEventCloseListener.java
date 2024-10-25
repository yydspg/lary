package cn.lary.module.stream.listener.redpacket;

import cn.lary.module.stream.service.RedPacketService;
import cn.lary.module.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(topic = "",consumerGroup = "springboot_consumer_group")
public class RedPacketEventCloseListener implements RocketMQListener<RedPacketCloseMessage> {

    private final RedPacketService redPacketService;
    private final WalletService walletService;

    /**
     * 处理红包关闭,此为定时处理
     * @param message {@link RedPacketCloseMessage}
     */
    @Override
    public void onMessage(RedPacketCloseMessage message) {
        log.info("receive redpacket close event message:{}", message.toString());

    }
}
