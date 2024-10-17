package cn.lary.module.message.dto.stream;

import cn.lary.pkg.wk.constant.WK;
import cn.lary.pkg.wk.dto.message.MessageHeader;
import cn.lary.pkg.wk.dto.message.MessageSendDTO;
import lombok.Data;

import java.nio.charset.StandardCharsets;

@Data
public class CreateRaffleNotifyDTO extends MessageSendDTO {

    public CreateRaffleNotifyDTO (int creator,int danmakuId){
        setHeader(new MessageHeader()
                .setNoPersist(1)
                .setRedDot(0));
        setChannelID(danmakuId);
        setChannelType(WK.ChannelType.stream);
        setFromUID(creator);
        String message = "我发布了一个新的抽奖活动快来看看吧";
        setPayload(message.getBytes(StandardCharsets.UTF_8));
    }
}
