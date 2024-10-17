package cn.lary.module.message.dto.follow;

import cn.lary.pkg.wk.constant.WK;
import cn.lary.pkg.wk.dto.message.MessageHeader;
import cn.lary.pkg.wk.dto.message.MessageSendDTO;

import java.nio.charset.StandardCharsets;

public class OneWayActiveFollowResponseDTO extends MessageSendDTO {

    public OneWayActiveFollowResponseDTO(int uid,int toUid,String uidName) {
        setHeader(new MessageHeader()
                .setRedDot(1));
        setChannelID(toUid);
        setFromUID(uid);
        setChannelType(WK.ChannelType.person);
        String content = uidName + "关注了你,快来回关吧";
        setPayload(content.getBytes(StandardCharsets.UTF_8));
    }
}
