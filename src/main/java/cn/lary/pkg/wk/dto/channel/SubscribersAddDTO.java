package cn.lary.pkg.wk.dto.channel;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SubscribersAddDTO  {
    @JsonProperty("channel_id")
    private String channelId;
    @JsonProperty("channel_type")
    private Integer channelType;
    private int reset;
    @JsonProperty("temp_subscriber")
    private int tempSubscribers;
    private List<String> subscribers;
}
