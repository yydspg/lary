package cn.lary.pkg.wk.dto.message;

import cn.lary.pkg.wk.entity.core.WKChannel;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class WKChannelRecentMessage extends WKChannel {
    // 最后一次同步的 message_seq ,
    @JsonProperty("last_message_seq")
    private long lastMessageSeq;
}
