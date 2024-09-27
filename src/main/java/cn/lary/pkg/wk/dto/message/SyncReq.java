package cn.lary.pkg.wk.dto.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SyncReq {
    private Integer uid;
    //客户端最大消息序列号
    @JsonProperty("message_seq")
    private long messageSeq;
    //消息数量限制
    private int limit;
}
