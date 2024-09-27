package cn.lary.pkg.wk.dto.channel;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SubscribersAddDTO  {
    @JsonProperty("channel_id")
    private int channelId;
    @JsonProperty("channel_type")
    private int channelType;
    private int reset;
    @JsonProperty("temp_subscriber")
    private int tempSubscribers;
    private List<Integer> subscribers;
}
