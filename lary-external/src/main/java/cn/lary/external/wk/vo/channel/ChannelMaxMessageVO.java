package cn.lary.external.wk.vo.channel;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ChannelMaxMessageVO {
    @JSONField(format="message_seq")
    private long msgSeq;
}
