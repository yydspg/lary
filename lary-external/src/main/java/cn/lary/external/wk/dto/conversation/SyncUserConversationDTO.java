package cn.lary.external.wk.dto.conversation;

import cn.lary.external.wk.entity.core.WKChannel;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class SyncUserConversationDTO extends WKChannel {
    private Long uid;
    // 会话消息数量
    @JsonProperty("msg_count")
    private long msgCount;
    private List<WKChannel> larges;
}
