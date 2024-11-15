package cn.lary.external.wk.dto.message;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SyncDTO {
    private long uid;
    //客户端最大消息序列号
    @JSONField(format="message_seq")
    private long messageSeq;
    //消息数量限制
    private int limit;
}
