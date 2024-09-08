package cn.lary.pkg.wk.entity.Request.conversation;

import cn.lary.pkg.wk.entity.core.Channel;
import lombok.Data;

@Data
public class ConversationDeleteReq extends Channel {
    private String uid;
}

