package cn.lary.pkg.wk.dto.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SyncAckReq {
    private String uid;
    // 最后一次同步的 message_seq ,
    @JsonProperty("last_message_seq")
    private long lastMessageSeq;
}
