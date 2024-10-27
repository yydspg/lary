package cn.lary.module.message.dto.follow;

import cn.lary.external.wk.constant.WK;
import cn.lary.external.wk.dto.message.MessageHeader;
import cn.lary.external.wk.dto.message.MessageSendDTO;

import java.nio.charset.StandardCharsets;

public class PassiveFollowResponseDTO extends MessageSendDTO {
    public PassiveFollowResponseDTO(long uid, long toUid) {
        setHeader(new MessageHeader()
                .setRedDot(1)
                .setNoPersist(1));
        setChannelID(toUid);
        setChannelType(WK.CHANNEL.TYPE.PERSON);
        setFromUID(uid);
        String content ="我接受了你的好友申请，快来和我聊天吧";
        setPayload(content.getBytes(StandardCharsets.UTF_8));
    }
}
