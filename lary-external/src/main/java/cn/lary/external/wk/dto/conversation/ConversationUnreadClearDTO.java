package cn.lary.external.wk.dto.conversation;

import cn.lary.external.wk.entity.core.WKChannel;
import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ConversationUnreadClearDTO extends WKChannel {
    private Long uid;
    @JSONField(format="message_seq")
    private int messageSeq;
}
