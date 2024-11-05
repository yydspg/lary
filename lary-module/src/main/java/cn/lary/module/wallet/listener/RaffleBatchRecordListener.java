package cn.lary.module.wallet.listener;

import cn.lary.common.dto.ResponsePair;
import cn.lary.module.common.constant.LARY;
import cn.lary.module.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;

@Slf4j
@RequiredArgsConstructor
@RocketMQMessageListener(consumerGroup = "test",topic = "eee")
public class RaffleBatchRecordListener implements RocketMQListener<RaffleBatchRecordMessage> {

    private final WalletService walletService;

    @Override
    public void onMessage(RaffleBatchRecordMessage message) {
        if (message.getCategory() == LARY.RAFFLE.MONEY_FIX) {
            ResponsePair<Void> pair = walletService.batchOutcomeFixTransfer(message.getFixTransferDTO());
            if (pair.isFail()){
                log.error("process raffle fix transfer fail,message:{}",pair.getMsg());
            }
        }
        if (message.getCategory() == LARY.RAFFLE.MONEY_RANDOM) {
            ResponsePair<Void> pair = walletService.batchOutcomeRandomTransfer(message.getRandomTransfer());
            if (pair.isFail()){
                log.error("process raffle random transfer fail,message:{}",pair.getMsg());
            }
        }
    }
}
