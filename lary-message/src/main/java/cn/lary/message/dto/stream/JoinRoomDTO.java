package cn.lary.message.dto.stream;


import cn.lary.message.external.wk.constant.WK;
import cn.lary.message.external.wk.dto.message.MessageHeader;
import cn.lary.message.external.wk.dto.message.MessageSendDTO;
import lombok.Data;

import java.nio.charset.StandardCharsets;

@Data
public class JoinRoomDTO extends MessageSendDTO {

    public JoinRoomDTO(long uid,String name,long danmakuId){
        setHeader(new MessageHeader()
                .setNoPersist(1)
                .setRedDot(0));
        setFromUID(uid);
        String content = name +"来了";
        setPayload(content.getBytes(StandardCharsets.UTF_8));
        setChannelID(danmakuId);
        setChannelType(WK.CHANNEL.TYPE.STREAM);
    }
}
