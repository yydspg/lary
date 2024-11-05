package cn.lary.module.wallet.listener;

import cn.lary.module.common.constant.LARY;
import cn.lary.module.message.service.AbstractAsyncRocketMessage;
import cn.lary.module.wallet.dto.BatchOutcomeFixTransferDTO;
import cn.lary.module.wallet.dto.BatchOutcomeRandomTransferDTO;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.rocketmq.client.producer.SendCallback;

@Data
@Accessors(chain = true)
public class RaffleBatchRecordMessage  extends AbstractAsyncRocketMessage {


    private BatchOutcomeRandomTransferDTO randomTransfer;
    private BatchOutcomeFixTransferDTO fixTransferDTO;
    private int category;

    @Override
    public int getBusinessSign() {
        return LARY.BUSINESS.TRANSFER.RAFFLE;
    }

    @Override
    public String getLogData() {
        return "";
    }

    @Override
    public SendCallback getSendCallback() {
        return null;
    }

    @Override
    public long getTimeOut() {
        return 0;
    }

    @Override
    public int getDelayLevel() {
        return 0;
    }
}
