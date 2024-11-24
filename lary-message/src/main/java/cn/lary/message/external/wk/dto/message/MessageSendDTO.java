package cn.lary.message.external.wk.dto.message;

import cn.lary.api.message.dto.YutakMessage;
import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;
import lombok.experimental.Accessors;

import java.nio.charset.StandardCharsets;
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

    public MessageSendDTO(){}

    public MessageSendDTO(YutakMessage message){
        header = new MessageHeader();
        header.setNoPersist(message.getNoPersist());
        header.setSyncOnce(message.getSyncOnce());
        header.setRedDot(message.getRedDot());
        channelID = message.getCid();
        channelType = message.getType();
        payload = message.getPayload().getBytes(StandardCharsets.UTF_8);
        subscribers = message.getSubscribers();
    }
}

