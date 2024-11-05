package cn.lary.module.redpacket.listener;

import cn.lary.module.common.constant.LARY;
import cn.lary.module.message.service.AbstractAsyncRocketMessage;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.rocketmq.client.producer.SendCallback;

@Data
@Accessors(chain = true)
public class RedpacketLocalRuleMessage extends AbstractAsyncRocketMessage {

    private int category;

    private long uid;

    private int limit;


    @Override
    public int getBusinessSign() {
        return LARY.BUSINESS.CACHE.REDPACKET;
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
