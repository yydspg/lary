package cn.lary.module.message.dto.stream;


import cn.lary.external.wk.constant.WK;
import cn.lary.external.wk.dto.message.MessageHeader;
import cn.lary.external.wk.dto.message.MessageSendDTO;
import lombok.Data;

import java.nio.charset.StandardCharsets;

@Data
public class JoinRoomDTO extends MessageSendDTO {

    public JoinRoomDTO(int uid,String name,int channelId){
        setHeader(new MessageHeader()
                .setNoPersist(1)
                .setRedDot(0));
        setFromUID(uid);
        String content = name +"来了";
        setPayload(content.getBytes(StandardCharsets.UTF_8));
        setChannelID(channelId);
        setChannelType(WK.CHANNEL.TYPE.STREAM);
    }
}
