package cn.lary.external.wk.dto.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class MessageSendDTO {
    @JsonProperty("header")
    private MessageHeader header;
    @JsonProperty("client_msg_no")
    private String clientMsgNo;
    @JsonProperty("stream_no")
    private String streamNo;
    @JsonProperty("from_uid")
    private int fromUID;
    @JsonProperty("channel_id")
    private int channelID;
    @JsonProperty("channel_type")
    private int channelType;
    //过期时间
    private int expire;
    // 订阅者
    private List<Integer> subscribers;
    // message content
    private byte[] payload;

}

