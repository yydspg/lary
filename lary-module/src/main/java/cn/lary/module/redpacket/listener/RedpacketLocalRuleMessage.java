package cn.lary.module.redpacket.listener;


import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.rocketmq.client.producer.SendCallback;
import cn.lary.module.message.service.AbstractAsyncRocketMessage;

@Data
@Accessors(chain = true)
public class RedpacketLocalRuleMessage extends AbstractAsyncRocketMessage {

    private int category;

    private long uid;

    private int limit;
    @Override
    public int getBusinessSign() {
        return 0;
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
