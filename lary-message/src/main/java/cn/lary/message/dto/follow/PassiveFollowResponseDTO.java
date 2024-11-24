package cn.lary.message.dto.follow;

import cn.lary.message.external.wk.constant.WK;
import cn.lary.message.external.wk.dto.message.MessageHeader;
import cn.lary.message.external.wk.dto.message.MessageSendDTO;

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
