package cn.lary.module.redpacket.dto;


import cn.lary.external.wk.dto.message.MessageHeader;
import cn.lary.external.wk.dto.message.MessageSendDTO;
import cn.lary.module.common.constant.LARY;
import lombok.Data;

import java.nio.charset.StandardCharsets;

@Data

public class RedpacketBuildNotifyDTO extends MessageSendDTO {

    public RedpacketBuildNotifyDTO(long uid, long danmakuId, long total){
        setHeader(new MessageHeader()
                .setNoPersist(1)
                .setRedDot(0));
        setChannelID(danmakuId);
        setChannelType(LARY.CHANNEL.TYPE.STREAM);
        setFromUID(uid);
        String content = "我发布了价值"+total+"的红包,手慢无哦";
        setPayload(content.getBytes(StandardCharsets.UTF_8));
    }
}
