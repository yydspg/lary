package cn.lary.module.message.dto.stream;

import cn.lary.pkg.wk.constant.WK;
import cn.lary.pkg.wk.dto.message.MessageHeader;
import cn.lary.pkg.wk.dto.message.MessageSendDTO;
import lombok.Data;

import java.nio.charset.StandardCharsets;

@Data

public class CreateRedPacketNotifyDTO extends MessageSendDTO {

    public CreateRedPacketNotifyDTO(int creator,int danmakuId,long total){
        setHeader(new MessageHeader()
                .setNoPersist(1)
                .setRedDot(0));
        setChannelID(danmakuId);
        setChannelType(WK.ChannelType.stream);
        setFromUID(creator);
        String content = "我发布了价值"+total+"的红包,手慢无哦";
        setPayload(content.getBytes(StandardCharsets.UTF_8));
    }
}
