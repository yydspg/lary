package cn.lary.pkg.wk.entity.Request.message;

import cn.lary.pkg.wk.entity.core.Channel;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class MessageSendReq extends Channel {
    @JsonProperty("header")
    private MessageHeader header;
    @JsonProperty("client_msg_no")
    private String clientMsgNo;
    @JsonProperty("stream_no")
    private String streamNo;
    @JsonProperty("from_uid")
    private String fromUID;
    //过期时间
    private int expire;
    // 订阅者
    private List<String> subscribers;
    // message content
    private byte[] payload;

}

