package cn.lary.pkg.wk.vo.conversation;

import cn.lary.pkg.wk.vo.message.Message;
import cn.lary.pkg.wk.entity.core.WKChannel;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ConversationRes extends WKChannel {
    private int unread;
    private long timestamp;
    @JsonProperty("last_message")
    private Message lastMessage;
}
