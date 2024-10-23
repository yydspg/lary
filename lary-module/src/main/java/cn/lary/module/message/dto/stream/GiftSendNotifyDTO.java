package cn.lary.module.message.dto.stream;

import cn.lary.external.wk.constant.WK;
import cn.lary.external.wk.dto.message.MessageHeader;
import cn.lary.external.wk.dto.message.MessageSendDTO;

import java.nio.charset.StandardCharsets;

public class GiftSendNotifyDTO extends MessageSendDTO {

    public GiftSendNotifyDTO(int danmakuId,int uid,String username,
                             String giftName,int giftCount) {
        setHeader(new MessageHeader()
                .setNoPersist(1)
                .setRedDot(0));
        setFromUID(uid);
        setChannelID(danmakuId);
        setChannelType(WK.CHANNEL.TYPE.STREAM);
        String content =  username + "送出了" + giftCount+ "个" + giftName;
        setPayload(content.getBytes(StandardCharsets.UTF_8));
    }
}
