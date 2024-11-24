package cn.lary.message.dto.stream;


import cn.lary.message.external.wk.constant.WK;
import cn.lary.message.external.wk.dto.channel.WKChannelCreateDTO;
import lombok.Data;

@Data
public class CreateDanmakuWKChannelDTO extends WKChannelCreateDTO {

    public CreateDanmakuWKChannelDTO(long danmakuId) {
        setCid(danmakuId);
        setType(WK.CHANNEL.TYPE.STREAM);
    }
}
