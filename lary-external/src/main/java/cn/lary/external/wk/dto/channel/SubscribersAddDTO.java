package cn.lary.external.wk.dto.channel;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscribersAddDTO  {
    @JSONField(format="channel_id")
    private long channelId;
    @JSONField(format="channel_type")
    private int channelType;
    private int reset;
    @JSONField(format="temp_subscriber")
    private int tempSubscribers;
    private List<Long> subscribers;
}
