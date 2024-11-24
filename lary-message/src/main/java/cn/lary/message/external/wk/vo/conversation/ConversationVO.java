package cn.lary.message.external.wk.vo.conversation;

import cn.lary.message.external.wk.vo.message.Message;
import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

@Data
public class ConversationVO {
    @JSONField(format="channel_id")
    private long channelId;

    @JSONField(format="channel_type")
    private int channelType;

    private int unread;
    private long timestamp;
    @JSONField(format="last_message")
    private Message lastMessage;
}
