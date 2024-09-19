package cn.lary.pkg.wk.vo.channel;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ChannelMaxMessageSeq {
    @JsonProperty("message_seq")
    private long msgSeq;
}
