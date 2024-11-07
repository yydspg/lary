package cn.lary.module.wallet.listener;

import cn.lary.module.common.constant.LARY;
import cn.lary.module.message.service.AbstractAsyncRocketMessage;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.rocketmq.client.producer.SendCallback;

import java.util.List;

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
