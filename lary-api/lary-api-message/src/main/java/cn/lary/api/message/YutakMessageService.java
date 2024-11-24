package cn.lary.api.message;

import cn.lary.api.message.dto.YutakAddSubscriberMessage;
import cn.lary.api.message.dto.YutakBuildChannelMessage;
import cn.lary.api.message.dto.YutakDropSubscriberMessage;
import cn.lary.api.message.dto.YutakMessage;
import cn.lary.api.message.vo.YutakMessageChannel;

public interface YutakMessageService {

    void send(YutakMessage message);

    YutakMessageChannel buildChannel(YutakBuildChannelMessage channel);

    void addSubscriber(YutakAddSubscriberMessage message);

    void dropSubscriber(YutakDropSubscriberMessage message);

}
