package cn.lary.module.message.service;

import lombok.Data;
import org.apache.rocketmq.client.producer.SendCallback;

@Data
public abstract class AbstractAsyncRocketMessage {


    public abstract int getBusinessSign();

    public abstract String getLogData();

    public abstract SendCallback getSendCallback();

    public abstract long getTimeOut();

    public abstract int getDelayLevel();
}
