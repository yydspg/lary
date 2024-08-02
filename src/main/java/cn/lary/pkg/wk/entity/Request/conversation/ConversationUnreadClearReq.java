package cn.lary.pkg.wk.entity.Request.conversation;

import cn.lary.pkg.wk.entity.core.Channel;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ConversationUnreadClearReq extends Channel {
    private String uid;
    @JsonProperty("message_seq")
    private int messageSeq;
}
