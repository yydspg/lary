package cn.lary.pkg.wk.entity.Response.message;

import cn.lary.pkg.wk.entity.Request.message.MessageHeader;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Message {
    private MessageHeader header;
    private byte setting;
    @JsonProperty("message_id")
    private long messageID;
    // 服务端消息 ID 全局唯一
    @JsonProperty("message_idstr")
    private String messageIDStr;
    @JsonProperty("client_msg_no")
    private String clientMsgNo;
    @JsonProperty("stream_no")
    private String streamNo;
    @JsonProperty("stream_seq")
    private int streamSeq;
    @JsonProperty("stream_flag")
    private byte streamFlag;
    @JsonProperty("message_seq")
    private long messageSeq;
    @JsonProperty("from_uid")
    private String fromUID;
    @JsonProperty("channel_id")
    private String channelID;
    @JsonProperty("channel_type")
    private byte channelType;
    private String topic;
    private int expire;
    private int timestamp;
    private byte[] payload;
}
