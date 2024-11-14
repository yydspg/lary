package cn.lary.external.wk.dto.message;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class MessageSendDTO {
    @JSONField(format="header")
    private MessageHeader header;
    @JSONField(format="client_msg_no")
    private String clientMsgNo;
    @JSONField(format="stream_no")
    private String streamNo;
    @JSONField(format="from_uid")
    private long fromUID;
    @JSONField(format="channel_id")
    private long channelID;
    @JSONField(format="channel_type")
    private int channelType;
    //过期时间
    private int expire;
    // 订阅者
    private List<Long> subscribers;
    // message content
    private byte[] payload;

}

