package cn.lary.module.message.dto.follow;

import cn.lary.external.wk.constant.WK;
import cn.lary.external.wk.dto.message.MessageHeader;
import cn.lary.external.wk.dto.message.MessageSendDTO;

import java.nio.charset.StandardCharsets;

public class ActiveFollowResponseDTO extends MessageSendDTO {
    public ActiveFollowResponseDTO(long uid,long toUid,String uidName) {
        setHeader(new MessageHeader()
                .setRedDot(1));
        setChannelID(toUid);
        setFromUID(uid);
        setChannelType(WK.CHANNEL.TYPE.PERSON);
        String content = uidName + "关注了你";
        setPayload(content.getBytes(StandardCharsets.UTF_8));
    }
}
