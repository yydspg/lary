package cn.lary.module.message.dto.stream;

import cn.lary.pkg.wk.constant.WK;
import cn.lary.pkg.wk.dto.message.MessageHeader;
import cn.lary.pkg.wk.dto.message.MessageSendDTO;
import lombok.Data;

import java.nio.charset.StandardCharsets;

@Data
public class EndLiveDTO extends MessageSendDTO {

    public EndLiveDTO(int uid,int channelId) {
        setHeader(new MessageHeader()
                .setNoPersist(1)
                .setRedDot(0));
        setFromUID(uid);
        setChannelID(channelId);
        setChannelType(WK.ChannelType.stream);
        String content =   "主播已经离开，稍后再来哦";
        setPayload(content.getBytes(StandardCharsets.UTF_8));
    }
}
