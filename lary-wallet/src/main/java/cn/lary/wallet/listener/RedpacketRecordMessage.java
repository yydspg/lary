package cn.lary.wallet.listener;

import cn.lary.common.constant.LARY;
import cn.lary.common.rocketmq.AbstractAsyncRocketMessage;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.rocketmq.client.producer.SendCallback;

@Data
@Accessors(chain = true)
public class RedpacketRecordMessage extends AbstractAsyncRocketMessage {

    private String token;

    private long uid;

    @Override
    public int getBusinessSign() {
        return LARY.BUSINESS.TRANSFER.STREAM_GIFT;
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
