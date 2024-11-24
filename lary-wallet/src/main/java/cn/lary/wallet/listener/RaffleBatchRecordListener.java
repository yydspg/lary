package cn.lary.wallet.listener;

import cn.lary.common.dto.ResponsePair;
import cn.lary.wallet.constant.RAFFLE;
import cn.lary.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;

@Slf4j
@RequiredArgsConstructor
@RocketMQMessageListener(consumerGroup = "lary-wallet-raffle",topic = "lary-wallet-raffle")
public class RaffleBatchRecordListener implements RocketMQListener<RaffleBatchRecordMessage> {

    private final WalletService walletService;

    @Override
    public void onMessage(RaffleBatchRecordMessage message) {
        if (message.getCategory() == RAFFLE.MONEY_FIX) {
            ResponsePair<Void> pair = walletService.batchOutcomeFixTransfer(message.getFixTransferDTO());
            if (pair.isFail()){
                log.error("process raffle fix transfer FAIL,message:{}",pair.getMsg());
            }
        }
        if (message.getCategory() == RAFFLE.MONEY_RANDOM) {
            ResponsePair<Void> pair = walletService.batchOutcomeRandomTransfer(message.getRandomTransfer());
            if (pair.isFail()){
                log.error("process raffle random transfer FAIL,message:{}",pair.getMsg());
            }
        }
    }
}
