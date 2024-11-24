package cn.lary.message.channel.dto;

import cn.lary.api.message.dto.YutakBuildChannelMessage;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class ChannelBuildDTO {

    private int type;

    private long uid;

    private long rid;

    private List<Long> subscribers;

    public ChannelBuildDTO() {}

    public ChannelBuildDTO(YutakBuildChannelMessage message){
        this.uid = message.getUid();
        this.rid = message.getRid();
        this.type = message.getType();
        this.subscribers = message.getSubscribers();
    }
}
