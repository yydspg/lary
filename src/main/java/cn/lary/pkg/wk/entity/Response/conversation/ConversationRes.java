package cn.lary.pkg.wk.entity.Response.conversation;

import cn.lary.pkg.wk.entity.Response.message.Message;
import cn.lary.pkg.wk.entity.core.Channel;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ConversationRes extends Channel {
    private int unread;
    private long timestamp;
    @JsonProperty("last_message")
    private Message lastMessage;
}
