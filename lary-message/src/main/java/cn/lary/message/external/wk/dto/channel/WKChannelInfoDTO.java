package cn.lary.message.external.wk.dto.channel;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

@Data
public class WKChannelInfoDTO {
    @JSONField(format="channel_id")
    private long channelId;

    @JSONField(format="channel_type")
    private int channelType;

    private int large;
    private int ban;
    private int disband;
}