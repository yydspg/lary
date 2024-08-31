package cn.lary.pkg.wk.entity.Request.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SyncReq {
    private String uid;
    //客户端最大消息序列号
    @JsonProperty("message_seq")
    private long messageSeq;
    //消息数量限制
    private int limit;
}
