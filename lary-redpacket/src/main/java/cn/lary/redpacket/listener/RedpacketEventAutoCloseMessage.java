package cn.lary.redpacket.listener;

import cn.lary.common.constant.LARY;
import cn.lary.common.rocketmq.AbstractAsyncRocketMessage;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.rocketmq.client.producer.SendCallback;

@Data
@Accessors(chain = true)
public class RedpacketEventAutoCloseMessage extends AbstractAsyncRocketMessage {

    private long eventId;
    private long uid;
    private long streamId;
    private long redPacketId;
    private long danmakuId;

    @Override
    public int getBusinessSign() {
        return LARY.BUSINESS.AUTO_CLOSE.READ_PACKET;
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
