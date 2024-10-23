package cn.lary.external.wk.vo.conversation;

import cn.lary.external.wk.entity.core.WKChannel;
import cn.lary.external.wk.vo.message.Message;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class SyncUserConversationVO extends WKChannel {
    private int unread;
    private long timestamp;
    @JsonProperty("last_msg_seq")
    private int lastMsgSeq;
    @JsonProperty("last_client_msg_no")
    private String lastClientMsgNo;
    @JsonProperty("offset_msg_seq")
    private long offsetMsgSeq;
    @JsonProperty("readed_to_msg_seq")
    private int readedToMsgSeq;
    private long version;
    private List<Message> recents;
}
