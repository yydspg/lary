package cn.lary.pkg.wk.entity.Request.conversation;

import cn.lary.pkg.wk.entity.core.Channel;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class SyncUserConversationReq extends Channel {
    private String uid;
    // 会话消息数量
    @JsonProperty("msg_count")
    private long msgCount;
    private List<Channel> larges;
}
