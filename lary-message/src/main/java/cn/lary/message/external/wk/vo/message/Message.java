package cn.lary.message.external.wk.vo.message;

import cn.lary.message.external.wk.dto.message.MessageHeader;
import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

@Data
public class Message {
    private MessageHeader header;
    private byte setting;
    @JSONField(format="message_id")
    private long messageID;
    // 服务端消息 ID 全局唯一
    @JSONField(format="message_idstr")
    private String messageIDStr;
    @JSONField(format="client_msg_no")
    private String clientMsgNo;
    @JSONField(format="stream_no")
    private String streamNo;
    @JSONField(format="stream_seq")
    private int streamSeq;
    @JSONField(format="stream_flag")
    private byte streamFlag;
    @JSONField(format="message_seq")
    private long messageSeq;
    @JSONField(format="from_uid")
    private String fromUID;
    @JSONField(format="channel_id")
    private String channelID;
    @JSONField(format="channel_type")
    private byte channelType;
    private String topic;
    private int expire;
    private int timestamp;
    private byte[] payload;
}
