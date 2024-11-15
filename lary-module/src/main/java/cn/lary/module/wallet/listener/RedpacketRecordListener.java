package cn.lary.module.wallet.listener;

import cn.lary.common.kit.JSONKit;
import cn.lary.common.kit.StringKit;
import cn.lary.module.common.constant.LARY;
import cn.lary.module.redpacket.entity.RedpacketRecord;
import cn.lary.module.redpacket.dto.RedpacketTokenDTO;
import cn.lary.module.redpacket.service.RedpacketRecordService;
import cn.lary.module.wallet.dto.SystemOutcomeTransferDTO;
import cn.lary.module.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(consumerGroup = "lary-wallet-redpacket",topic = "lary-wallet:redpacket")
public class RedpacketRecordListener implements RocketMQListener<RedpacketRecordMessage> {

    private final RedpacketRecordService redpacketRecordService;
    private final WalletService walletService;
    private final TransactionTemplate transactionTemplate;

    @Override
    public void onMessage(RedpacketRecordMessage message) {
        String token = message.getToken();
        try {
            String data = StringKit.decrypt(token);
            RedpacketTokenDTO dto = JSONKit.fromJSON(data, RedpacketTokenDTO.class);
            transactionTemplate.executeWithoutResult(status -> {
                RedpacketRecord record = redpacketRecordService.lambdaQuery()
                        .select(RedpacketRecord::getSync)
                        .eq(RedpacketRecord::getRid, dto.getRid())
                        .one();
                if (record != null && record.getSync() == LARY.SYNC_STATUS.SUCCESS) {
                    return ;
                }
                redpacketRecordService.save(new RedpacketRecord()
                        .setRid(dto.getRid())
                        .setAmount(dto.getAmount())
                        .setUid(dto.getUid())
                        .setSid(dto.getSid())
                        .setSync(LARY.SYNC_STATUS.SUCCESS));
                walletService.systemOutcomeTransfer(new SystemOutcomeTransferDTO()
                        .setAmount(dto.getAmount())
                        .setTransfer(LARY.WALLET.TRANSFER.POCKET)
                        .setMembers(List.of(message.getUid())));
        });
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
