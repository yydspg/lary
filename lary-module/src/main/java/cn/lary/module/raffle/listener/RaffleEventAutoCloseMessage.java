package cn.lary.module.raffle.listener;

import cn.lary.module.common.constant.LARY;
import cn.lary.module.message.service.AbstractAsyncRocketMessage;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.rocketmq.client.producer.SendCallback;

@Data
@Accessors(chain = true)
public class RaffleEventAutoCloseMessage extends AbstractAsyncRocketMessage {

    private long eventId;
    private long uid;
    private long streamId;
    private long raffleId;
    private int category;

    @Override
    public int getBusinessSign() {
        return LARY.BUSINESS.AUTO_CLOSE.RAFFLE;
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
        return 100L;
    }

    @Override
    public int getDelayLevel() {
        return 0;
    }
}
