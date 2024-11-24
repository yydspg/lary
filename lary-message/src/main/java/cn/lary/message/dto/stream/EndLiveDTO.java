package cn.lary.message.dto.stream;


import cn.lary.message.external.wk.constant.WK;
import cn.lary.message.external.wk.dto.message.MessageHeader;
import cn.lary.message.external.wk.dto.message.MessageSendDTO;
import lombok.Data;

import java.nio.charset.StandardCharsets;

@Data
public class EndLiveDTO extends MessageSendDTO {

    public EndLiveDTO(long uid,long danmakuId) {
        setHeader(new MessageHeader()
                .setNoPersist(1)
                .setRedDot(0));
        setFromUID(uid);
        setChannelID(danmakuId);
        setChannelType(WK.CHANNEL.TYPE.STREAM);
        String content =   "主播已经离开，稍后再来哦";
        setPayload(content.getBytes(StandardCharsets.UTF_8));
    }
}
