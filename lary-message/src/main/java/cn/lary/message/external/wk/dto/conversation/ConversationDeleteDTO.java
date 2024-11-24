package cn.lary.message.external.wk.dto.conversation;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

@Data
public class ConversationDeleteDTO  {
    @JSONField(format="channel_id")
    private long channelId;

    @JSONField(format="channel_type")
    private int channelType;

    private Long uid;
}

