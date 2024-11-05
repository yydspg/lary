package cn.lary.module.redpacket.dto;

import cn.lary.external.wk.dto.message.MessageHeader;
import cn.lary.external.wk.dto.message.MessageSendDTO;
import cn.lary.module.common.constant.LARY;
import lombok.Data;

import java.nio.charset.StandardCharsets;

@Data
public class RedpacketEventCloseNotifyDTO extends MessageSendDTO {

    public RedpacketEventCloseNotifyDTO(long uid,long danmakuId) {
        setHeader(new MessageHeader()
                .setNoPersist(1)
                .setRedDot(0));
        setChannelID(danmakuId);
        setChannelType(LARY.CHANNEL.TYPE.STREAM);
        setFromUID(uid);
        String content = "cmd:redpacket-close-notify";
        setPayload(content.getBytes(StandardCharsets.UTF_8));
    }
}
