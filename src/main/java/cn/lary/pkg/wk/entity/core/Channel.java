package cn.lary.pkg.wk.entity.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Channel {
    @JsonProperty("channel_id")
    private String chanelID;
    @JsonProperty("channel_type")
    private byte channelType;
}
