package cn.lary.module.message.dto.stream;


import cn.lary.external.wk.constant.WK;
import cn.lary.external.wk.dto.message.MessageHeader;
import cn.lary.external.wk.dto.message.MessageSendDTO;
import lombok.Data;

import java.nio.charset.StandardCharsets;

@Data
public class CreateRaffleNotifyDTO extends MessageSendDTO {

    public CreateRaffleNotifyDTO (long creator,long danmakuId){
        setHeader(new MessageHeader()
                .setNoPersist(1)
                .setRedDot(0));
        setChannelID(danmakuId);
        setChannelType(WK.CHANNEL.TYPE.STREAM);
        setFromUID(creator);
        String message = "我发布了一个新的抽奖活动快来看看吧";
        setPayload(message.getBytes(StandardCharsets.UTF_8));
    }
}
