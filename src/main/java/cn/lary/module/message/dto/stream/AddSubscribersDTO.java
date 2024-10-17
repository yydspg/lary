package cn.lary.module.message.dto.stream;

import cn.lary.pkg.wk.constant.WK;
import cn.lary.pkg.wk.dto.channel.SubscribersAddDTO;
import lombok.Data;

import java.util.List;

@Data
public class AddSubscribersDTO extends SubscribersAddDTO {
    public AddSubscribersDTO(int channelId, List<Integer> subscribers) {
        setChannelId(channelId);
        setSubscribers(subscribers);
        setChannelType(WK.ChannelType.stream);
    }
}
