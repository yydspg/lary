package cn.lary.pkg.wk.dto.conversation;

import cn.lary.pkg.wk.entity.core.WKChannel;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ConversationUnreadClearReq extends WKChannel {
    private String uid;
    @JsonProperty("message_seq")
    private int messageSeq;
}
