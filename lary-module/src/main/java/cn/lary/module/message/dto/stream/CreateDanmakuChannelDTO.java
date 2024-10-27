package cn.lary.module.message.dto.stream;


import cn.lary.external.wk.constant.WK;
import cn.lary.external.wk.dto.channel.ChannelCreateDTO;
import lombok.Data;

@Data
public class CreateDanmakuChannelDTO extends ChannelCreateDTO {

    public CreateDanmakuChannelDTO(long danmakuId) {
        setChannelId(danmakuId);
        setChannelType(WK.CHANNEL.TYPE.STREAM);
    }
}
