package cn.lary.external.wk.dto.conversation;

import cn.lary.external.wk.entity.core.WKChannel;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ConversationUnreadClearDTO extends WKChannel {
    private Long uid;
    @JsonProperty("message_seq")
    private int messageSeq;
}
