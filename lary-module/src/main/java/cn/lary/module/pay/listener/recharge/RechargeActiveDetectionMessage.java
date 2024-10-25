package cn.lary.module.pay.listener.recharge;

import cn.lary.module.common.constant.LARY;
import cn.lary.module.pay.listener.AbstractActiveDetectionMessage;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.rocketmq.client.producer.SendCallback;

@Data
@Accessors(chain = true)
public class RechargeActiveDetectionMessage extends AbstractActiveDetectionMessage {


    @Override
    public int getBusinessSign() {
        return LARY.BUSINESS.PAYMENT.RECHARGE;
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
