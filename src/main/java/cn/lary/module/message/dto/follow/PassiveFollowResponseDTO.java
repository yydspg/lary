package cn.lary.module.message.dto.follow;

import cn.lary.pkg.wk.constant.WK;
import cn.lary.pkg.wk.dto.message.MessageHeader;
import cn.lary.pkg.wk.dto.message.MessageSendDTO;

import java.nio.charset.StandardCharsets;

public class PassiveFollowResponseDTO extends MessageSendDTO {
    public PassiveFollowResponseDTO(int uid, int toUid) {
        setHeader(new MessageHeader()
                .setRedDot(1)
                .setNoPersist(1));
        setChannelID(toUid);
        setChannelType(WK.ChannelType.person);
        setFromUID(uid);
        String content ="我接受了你的好友申请，快来和我聊天吧";
        setPayload(content.getBytes(StandardCharsets.UTF_8));
    }
}
