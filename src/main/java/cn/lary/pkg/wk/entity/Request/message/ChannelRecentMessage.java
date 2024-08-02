package cn.lary.pkg.wk.entity.Request.message;

import cn.lary.pkg.wk.entity.core.Channel;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ChannelRecentMessage extends Channel {
    // 最后一次同步的 message_seq ,
    @JsonProperty("last_message_seq")
    private long lastMessageSeq;
}
