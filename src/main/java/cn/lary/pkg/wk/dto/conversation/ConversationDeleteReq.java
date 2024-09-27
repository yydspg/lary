package cn.lary.pkg.wk.dto.conversation;

import cn.lary.pkg.wk.entity.core.WKChannel;
import lombok.Data;

@Data
public class ConversationDeleteReq extends WKChannel {
    private Integer uid;
}

