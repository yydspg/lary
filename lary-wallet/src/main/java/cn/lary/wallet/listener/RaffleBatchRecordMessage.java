package cn.lary.wallet.listener;

import cn.lary.common.constant.LARY;
import cn.lary.common.rocketmq.AbstractAsyncRocketMessage;
import cn.lary.wallet.dto.BatchOutcomeFixTransferDTO;
import cn.lary.wallet.dto.BatchOutcomeRandomTransferDTO;
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
