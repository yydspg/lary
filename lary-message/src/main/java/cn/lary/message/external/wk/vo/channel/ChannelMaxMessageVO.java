package cn.lary.message.external.wk.vo.channel;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

@Data
public class ChannelMaxMessageVO {
    @JSONField(format="message_seq")
    private long msgSeq;
}
