package cn.lary.message.external.wk.vo.message;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

import java.util.List;

@Data
public class SyncMessage {
    @JSONField(format="start_message_seq")
    private long startMessageSeq;
    @JSONField(format="end_message_seq")
    private long endMessageSeq;
    // 是否还有更多 1.是0,否
    private int more;
    private List<Message> messages;
}
