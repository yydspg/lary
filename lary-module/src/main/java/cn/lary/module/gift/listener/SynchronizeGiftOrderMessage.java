package cn.lary.module.gift.listener;

import cn.lary.module.gift.entity.GiftOrder;
import cn.lary.module.message.service.AbstractAsyncRocketMessage;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.rocketmq.client.producer.SendCallback;

@Data
@Accessors(chain = true)
public class SynchronizeGiftOrderMessage extends AbstractAsyncRocketMessage {

    private GiftOrder giftOrder;


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
