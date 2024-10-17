package cn.lary.module.message.service.impl;

import cn.lary.core.dto.ResponsePair;
import cn.lary.kit.BizKit;
import cn.lary.module.message.service.MessageService;
import cn.lary.pkg.wk.api.WKChannelService;
import cn.lary.pkg.wk.api.WKMessageService;
import cn.lary.pkg.wk.dto.channel.ChannelCreateDTO;
import cn.lary.pkg.wk.dto.channel.SubscribersAddDTO;
import cn.lary.pkg.wk.dto.message.MessageSendDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final WKMessageService wkMessageService;
    private final WKChannelService wkChannelService;

    @Override
    public void send(MessageSendDTO dto) {
        try {
            wkMessageService.send(dto);
        }catch (Exception e){
            log.error("send message error,channelId:{},type:{},reason:{}",dto.getChannelID(),dto.getChannelType(),e.getMessage());
        }
        log.info("send message success,channelID:{},type:{}", dto.getChannelID(),dto.getChannelType());
    }

    @Override
    public void saveOrUpdateChannel(ChannelCreateDTO dto) {
        try{
            wkChannelService.createOrUpdate(dto);
        }catch (Exception e){
            log.error("create or update channel error,channelId:{},type:{}",dto.getChannelId(),dto.getChannelType());
        }
        log.info("create or update channel success,channelId:{},type:{}", dto.getChannelId(),dto.getChannelType());
    }

    @Override
    public void addSubscriber(SubscribersAddDTO dto) {
        try{
            wkChannelService.addSubscribers(dto);
        }catch (Exception e){
            log.error("add subscribers error,channelId:{},type:{},reason:{}",dto.getChannelId(),dto.getChannelType(),e.getMessage());
        }
        log.info("add subscribers success channelId:{},type:{}", dto.getChannelId(),dto.getChannelType());
    }
}
