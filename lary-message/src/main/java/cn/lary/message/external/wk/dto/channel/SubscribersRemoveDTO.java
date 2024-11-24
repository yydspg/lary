package cn.lary.message.external.wk.dto.channel;

import cn.lary.api.message.dto.YutakDropSubscriberMessage;
import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data
public class SubscribersRemoveDTO  {
    @JSONField(format="channel_id")
    private long channelId;

    @JSONField(format="channel_type")
    private int channelType;

    @JSONField(format="temp_subscriber")
    private int tempSubscribers;
    private List<String> subscribers;

    public SubscribersRemoveDTO() {}

    public SubscribersRemoveDTO(YutakDropSubscriberMessage message){
        channelId = message.getCid();
        channelType = message.getType();
        tempSubscribers = message.getSubscribers().size();
        subscribers = new ArrayList<>();
    }
}
