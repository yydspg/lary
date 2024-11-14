package cn.lary.module.message.dto.stream;


import cn.lary.external.wk.constant.WK;
import cn.lary.external.wk.dto.channel.WKChannelCreateDTO;
import lombok.Data;

@Data
public class CreateDanmakuWKChannelDTO extends WKChannelCreateDTO {

    public CreateDanmakuWKChannelDTO(long danmakuId) {
        setCid(danmakuId);
        setType(WK.CHANNEL.TYPE.STREAM);
    }
}
