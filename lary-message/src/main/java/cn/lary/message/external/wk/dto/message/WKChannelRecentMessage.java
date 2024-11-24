package cn.lary.message.external.wk.dto.message;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

@Data
public class WKChannelRecentMessage  {
    @JSONField(format="channel_id")
    private long channelId;

    @JSONField(format="channel_type")
    private int channelType;

    // 最后一次同步的 message_seq ,
    @JSONField(format="last_message_seq")
    private long lastMessageSeq;
}
