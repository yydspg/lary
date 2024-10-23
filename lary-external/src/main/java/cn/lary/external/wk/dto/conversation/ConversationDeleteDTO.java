package cn.lary.external.wk.dto.conversation;

import cn.lary.external.wk.entity.core.WKChannel;
import lombok.Data;

@Data
public class ConversationDeleteDTO extends WKChannel {
    private Integer uid;
}

