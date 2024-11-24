package cn.lary.redpacket.dto;

import cn.lary.api.message.dto.YutakMessage;
import lombok.Data;

@Data
public class RedpacketBuildNotifyDTO extends YutakMessage {

    public RedpacketBuildNotifyDTO(long uid, long danmakuId, long total){
        setNoPersist(1);
        setCid(danmakuId);
//        setType(LARY.CHANNEL.TYPE.STREAM);
        setFid(uid);
        String content = "我发布了价值"+total+"的红包,手慢无哦";
        setPayload(content);
    }
}
