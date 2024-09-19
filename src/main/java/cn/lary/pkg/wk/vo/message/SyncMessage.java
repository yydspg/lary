package cn.lary.pkg.wk.vo.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class SyncMessage {
    @JsonProperty("start_message_seq")
    private long startMessageSeq;
    @JsonProperty("end_message_seq")
    private long endMessageSeq;
    // 是否还有更多 1.是0,否
    private int more;
    private List<Message> messages;
}
