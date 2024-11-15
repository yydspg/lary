package cn.lary.external.wk.dto.conversation;

import cn.lary.external.wk.entity.core.WKChannel;
import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class SyncUserConversationDTO extends WKChannel {
    private Long uid;
    // 会话消息数量
    @JSONField(format="msg_count")
    private long msgCount;
    private List<WKChannel> larges;
}
