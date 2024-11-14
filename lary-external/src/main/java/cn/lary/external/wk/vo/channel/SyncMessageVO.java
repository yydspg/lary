package cn.lary.external.wk.vo.channel;

import cn.lary.external.wk.vo.message.Message;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class SyncMessageVO {
    @JSONField(format="start_message_seq")
    private long startMessageSeq;
    @JSONField(format="end_message_seq")
    private long endMessageSeq;
    private int more;
    private List<Message> messages;
}
