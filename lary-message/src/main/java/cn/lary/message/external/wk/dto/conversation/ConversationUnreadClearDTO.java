package cn.lary.message.external.wk.dto.conversation;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ConversationUnreadClearDTO  {
    @JSONField(format="channel_id")
    private long channelId;

    @JSONField(format="channel_type")
    private int channelType;

    private Long uid;
    @JSONField(format="message_seq")
    private int messageSeq;
}
