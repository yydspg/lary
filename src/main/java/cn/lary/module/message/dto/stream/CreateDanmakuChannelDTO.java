package cn.lary.module.message.dto.stream;

import cn.lary.pkg.wk.constant.WK;
import cn.lary.pkg.wk.dto.channel.ChannelCreateDTO;
import lombok.Data;

@Data
public class CreateDanmakuChannelDTO extends ChannelCreateDTO {

    public CreateDanmakuChannelDTO(int channelId) {
        setChannelId(channelId);
        setChannelType(WK.ChannelType.stream);
    }
}
