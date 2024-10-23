package cn.lary.external.wk.vo.channel;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ChannelMaxMessageVO {
    @JsonProperty("message_seq")
    private long msgSeq;
}
