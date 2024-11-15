package cn.lary.external.wk.dto.message;

import cn.lary.external.wk.entity.core.WKChannel;
import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class WKChannelRecentMessage extends WKChannel {
    // 最后一次同步的 message_seq ,
    @JSONField(format="last_message_seq")
    private long lastMessageSeq;
}
