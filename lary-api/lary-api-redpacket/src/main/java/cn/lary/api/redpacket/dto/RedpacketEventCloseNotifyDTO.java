package cn.lary.api.redpacket.dto;


import cn.lary.api.message.dto.YutakMessage;
import lombok.Data;



@Data
public class RedpacketEventCloseNotifyDTO extends YutakMessage {

    public RedpacketEventCloseNotifyDTO(long uid,long danmakuId) {
        setNoPersist(0);
        setCid(danmakuId);
//        setType(LARY.CHANNEL.TYPE.STREAM);
        setFid(uid);
        String content = "cmd:redpacket-close-notify";
        setPayload(content);
    }
}
