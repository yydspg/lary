package cn.lary.message.external.wk.dto.conversation;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

import java.util.List;

@Data
public class SyncUserConversationDTO  {
    @JSONField(format="channel_id")
    private long channelId;

    @JSONField(format="channel_type")
    private int channelType;

    private Long uid;
    // 会话消息数量
    @JSONField(format="msg_count")
    private long msgCount;
//    private List<WKChannel> larges;
}
