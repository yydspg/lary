package cn.lary.message.external.wk.vo.conversation;

import cn.lary.message.external.wk.vo.message.Message;
import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

import java.util.List;

@Data
public class SyncUserConversationVO {
    @JSONField(format="channel_id")
    private long channelId;

    @JSONField(format="channel_type")
    private int channelType;

    private int unread;
    private long timestamp;
    @JSONField(format="last_msg_seq")
    private int lastMsgSeq;
    @JSONField(format="last_client_msg_no")
    private String lastClientMsgNo;
    @JSONField(format="offset_msg_seq")
    private long offsetMsgSeq;
    @JSONField(format="readed_to_msg_seq")
    private int readedToMsgSeq;
    private long version;
    private List<Message> recents;
}
