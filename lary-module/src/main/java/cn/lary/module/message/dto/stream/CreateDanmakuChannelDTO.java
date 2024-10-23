package cn.lary.module.message.dto.stream;


import cn.lary.external.wk.constant.WK;
import cn.lary.external.wk.dto.channel.ChannelCreateDTO;
import lombok.Data;

@Data
public class CreateDanmakuChannelDTO extends ChannelCreateDTO {

    public CreateDanmakuChannelDTO(int channelId) {
        setChannelId(channelId);
        setChannelType(WK.CHANNEL.TYPE.STREAM);
    }
}
