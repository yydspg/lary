package cn.lary.pkg.wk.entity.Request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
@Data
public class SubscribersRemove {
    @JsonProperty("channel_id")
    private String chanelID;
    @JsonProperty("channel_type")
    private byte channelType;
    @JsonProperty("temp_subscriber")
    private int tempSubscribers;
    private List<String> subscribers;
}
