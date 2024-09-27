package cn.lary.pkg.wk.dto.conversation;

import cn.lary.pkg.wk.entity.core.WKChannel;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class SyncUserConversationReq extends WKChannel {
    private Integer uid;
    // 会话消息数量
    @JsonProperty("msg_count")
    private long msgCount;
    private List<WKChannel> larges;
}
