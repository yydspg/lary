package cn.lary.module.raffle.listener;

import cn.lary.module.common.constant.LARY;
import cn.lary.module.message.service.AbstractAsyncRocketMessage;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.rocketmq.client.producer.SendCallback;

@Data
@Accessors(chain = true)
public class RaffleRuleLocalCacheMessage extends AbstractAsyncRocketMessage {


    private int shard;

    private long uid;

    private int limit;

    @Override
    public int getBusinessSign() {
        return LARY.BUSINESS.CACHE.RAFFLE;
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
        return 100;
    }

    @Override
    public int getDelayLevel() {
        return 0;
    }
}
