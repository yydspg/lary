package cn.lary.module.engine.listener;

import cn.lary.module.common.constant.LARY;
import cn.lary.module.message.service.AbstractAsyncRocketMessage;
import lombok.Data;
import org.apache.rocketmq.client.producer.SendCallback;

@Data
public class StreamEventMessage extends AbstractAsyncRocketMessage {

    private long sid;

    private int tag;

    private double score;

    private String url;

    private String title;

    private String username;

    private String avatar;

    private long uid;

    private int eventType;

    @Override
    public int getBusinessSign() {
        return LARY.BUSINESS.STREAM.UP;
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
