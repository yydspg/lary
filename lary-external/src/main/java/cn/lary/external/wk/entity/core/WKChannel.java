package cn.lary.external.wk.entity.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class WKChannel {
    @JsonProperty("channel_id")
    private int channelId;
    @JsonProperty("channel_type")
    private Integer channelType;

}
