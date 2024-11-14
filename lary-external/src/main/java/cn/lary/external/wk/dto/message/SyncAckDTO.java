package cn.lary.external.wk.dto.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SyncAckDTO {
    private long uid;
    // 最后一次同步的 message_seq ,
    @JSONField(format="last_message_seq")
    private long lastMessageSeq;
}
