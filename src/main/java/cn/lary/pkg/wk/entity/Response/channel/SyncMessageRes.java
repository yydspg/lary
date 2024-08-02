package cn.lary.pkg.wk.entity.Response.channel;

import cn.lary.pkg.wk.entity.Response.message.Message;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class SyncMessageRes {
    @JsonProperty("start_message_seq")
    private long startMessageSeq;
    @JsonProperty("end_message_seq")
    private long endMessageSeq;
    private int more;
    private List<Message> messages;
}
