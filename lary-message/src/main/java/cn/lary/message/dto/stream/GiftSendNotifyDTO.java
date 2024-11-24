package cn.lary.message.dto.stream;

import cn.lary.message.external.wk.constant.WK;
import cn.lary.message.external.wk.dto.message.MessageHeader;
import cn.lary.message.external.wk.dto.message.MessageSendDTO;

import java.nio.charset.StandardCharsets;

public class GiftSendNotifyDTO extends MessageSendDTO {

    public GiftSendNotifyDTO(long danmakuId,long uid,String username,
                             long giftId,int giftCount) {
        setHeader(new MessageHeader()
                .setNoPersist(1)
                .setRedDot(0));
        setFromUID(uid);
        setChannelID(danmakuId);
        setChannelType(WK.CHANNEL.TYPE.STREAM);
        String content =  username + "送出了" + giftCount+ "个[" + giftId+"]";
        setPayload(content.getBytes(StandardCharsets.UTF_8));
    }
}
