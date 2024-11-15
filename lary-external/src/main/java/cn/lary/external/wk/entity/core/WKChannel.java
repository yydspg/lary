package cn.lary.external.wk.entity.core;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class WKChannel {
    @JSONField(format="channel_id")
    private long channelId;
    @JSONField(format="channel_type")
    private Integer channelType;

}
