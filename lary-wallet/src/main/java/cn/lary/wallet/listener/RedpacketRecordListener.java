package cn.lary.wallet.listener;

import cn.lary.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(consumerGroup = "lary-wallet-redpacket",topic = "lary-wallet:redpacket")
public class RedpacketRecordListener implements RocketMQListener<RedpacketRecordMessage> {

//    private final RedpacketRecordService redpacketRecordService;
    private final WalletService walletService;
    private final TransactionTemplate transactionTemplate;

    @Override
    public void onMessage(RedpacketRecordMessage message) {
//        String token = message.getToken();
//        try {
//            String data = StringKit.decrypt(token);
//            RedpacketTokenDTO dto = JSONKit.fromJSON(data, RedpacketTokenDTO.class);
//            transactionTemplate.executeWithoutResult(status -> {
//                RedpacketRecord record = redpacketRecordService.lambdaQuery()
//                        .select(RedpacketRecord::getSync)
//                        .eq(RedpacketRecord::getRid, dto.getRid())
//                        .one();
//                if (record != null && record.getSync() == LARY.SYNC_STATUS.SUCCESS) {
//                    return ;
//                }
//                redpacketRecordService.save(new RedpacketRecord()
//                        .setRid(dto.getRid())
//                        .setAmount(dto.getAmount())
//                        .setUid(dto.getUid())
//                        .setSid(dto.getSid())
//                        .setSync(LARY.SYNC_STATUS.SUCCESS));
//                walletService.systemOutcomeTransfer(new SystemOutcomeTransferDTO()
//                        .setAmount(dto.getAmount())
//                        .setTransfer(WALLET.TRANSFER.POCKET)
//                        .setMembers(List.of(message.getUid())));
//        });
//        } catch (Exception e) {
//            log.error(e.getMessage());
//        }
    }
}
