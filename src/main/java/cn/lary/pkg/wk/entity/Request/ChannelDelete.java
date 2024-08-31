package cn.lary.pkg.wk.entity.Request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ChannelDelete {
    @JsonProperty("channel_id")
    private String chanelID;
    @JsonProperty("channel_type")
    private byte channelType;
}
