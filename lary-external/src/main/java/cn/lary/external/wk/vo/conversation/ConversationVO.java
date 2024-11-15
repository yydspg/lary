package cn.lary.external.wk.vo.conversation;

import cn.lary.external.wk.entity.core.WKChannel;
import cn.lary.external.wk.vo.message.Message;
import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ConversationVO extends WKChannel {
    private int unread;
    private long timestamp;
    @JSONField(format="last_message")
    private Message lastMessage;
}
