package cn.lary.message.dubbo;

import cn.lary.api.message.YutakMessageService;
import cn.lary.api.message.dto.YutakAddSubscriberMessage;
import cn.lary.api.message.dto.YutakBuildChannelMessage;
import cn.lary.api.message.dto.YutakDropSubscriberMessage;
import cn.lary.api.message.dto.YutakMessage;
import cn.lary.api.message.vo.YutakMessageChannel;
import cn.lary.message.channel.dto.ChannelBuildDTO;
import cn.lary.message.channel.entity.LaryChannel;
import cn.lary.message.external.wk.dto.channel.SubscribersAddDTO;
import cn.lary.message.external.wk.dto.channel.SubscribersRemoveDTO;
import cn.lary.message.external.wk.dto.message.MessageSendDTO;
import cn.lary.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class YutakMessageServiceImpl implements YutakMessageService {

    private final MessageService messageService;


    @Override
    public void send(YutakMessage message) {
            messageService.send(new MessageSendDTO(message));
    }

    @Override
    public YutakMessageChannel buildChannel(YutakBuildChannelMessage message) {
        LaryChannel channel = messageService.saveOrUpdateChannel(new ChannelBuildDTO(message));
        return new YutakMessageChannel()
                .setType(channel.getType())
                .setStatus(channel.getStatus())
                .setUid(channel.getUid())
                .setCid(channel.getCid())
                .setRid(channel.getRid());
    }


    @Override
    public void addSubscriber(YutakAddSubscriberMessage message) {
            messageService.addSubscriber(new SubscribersAddDTO(message));
    }

    @Override
    public void dropSubscriber(YutakDropSubscriberMessage message) {
            messageService.dropSubscriber(new SubscribersRemoveDTO(message));
    }
}
