package cn.lary.module.message.dto.stream;


import cn.lary.external.wk.constant.WK;
import cn.lary.external.wk.dto.channel.SubscribersAddDTO;
import lombok.Data;

import java.util.List;

@Data
public class AddSubscribersDTO extends SubscribersAddDTO {

    public AddSubscribersDTO(long danmakuId, List<Long> subscribers) {
        setChannelId(danmakuId);
        setSubscribers(subscribers);
        setChannelType(WK.CHANNEL.TYPE.STREAM);
    }
}
