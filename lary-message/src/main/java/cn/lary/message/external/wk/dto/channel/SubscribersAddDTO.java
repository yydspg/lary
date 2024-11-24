package cn.lary.message.external.wk.dto.channel;

import cn.lary.api.message.dto.YutakAddSubscriberMessage;
import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

import java.util.List;

@Data
public class SubscribersAddDTO  {

    @JSONField(format="channel_id")
    private long channelId;

    @JSONField(format="channel_type")
    private int channelType;

    private int reset;

    @JSONField(format="temp_subscriber")
    private int tempSubscribers;

    private List<Long> subscribers;

    public SubscribersAddDTO() {}

    public SubscribersAddDTO(YutakAddSubscriberMessage message){
        channelId = message.getCid();
        channelType = message.getType();
        reset = message.getReset();
        subscribers = message.getSubscribers();
    }

}
