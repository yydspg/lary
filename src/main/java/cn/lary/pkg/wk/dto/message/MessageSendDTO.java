package cn.lary.pkg.wk.dto.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;
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
    private String fromUID;
    @JsonProperty("channel_id")
    private String chanelID;
    @JsonProperty("channel_type")
    private Integer channelType;
    //过期时间
    private int expire;
    // 订阅者
    private List<String> subscribers;
    // message content
    private byte[] payload;

}

