package cn.lary.message.service.impl;

import cn.lary.message.channel.dto.ChannelBuildDTO;
import cn.lary.message.channel.entity.LaryChannel;
import cn.lary.message.channel.service.LaryChannelService;
import cn.lary.message.external.wk.api.WKChannelService;
import cn.lary.message.external.wk.api.WKMessageService;
import cn.lary.message.external.wk.api.WKUserService;
import cn.lary.message.external.wk.api.WkRouteService;
import cn.lary.message.external.wk.dto.channel.SubscribersAddDTO;
import cn.lary.message.external.wk.dto.channel.SubscribersRemoveDTO;
import cn.lary.message.external.wk.dto.channel.WKChannelCreateDTO;
import cn.lary.message.external.wk.dto.message.MessageSendDTO;
import cn.lary.message.external.wk.dto.user.UpdateTokenDTO;
import cn.lary.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final WKMessageService wkMessageService;
    private final WKChannelService wkChannelService;
    private final WKUserService wkUserService;
    private final WkRouteService wkRouteService;
    private final RocketMQTemplate rocketMQTemplate;
    private final LaryChannelService laryChannelService;

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
    public LaryChannel saveOrUpdateChannel(ChannelBuildDTO dto) {

        LaryChannel channel = new LaryChannel()
                .setType(dto.getType())
                .setRid(dto.getRid())
                .setUid(dto.getUid());
        LaryChannel data = laryChannelService.build(channel);
        try{
            WKChannelCreateDTO wkChannel = new WKChannelCreateDTO()
                    .setType(dto.getType())
                    .setCid(data.getCid())
                    .setSubscribers(dto.getSubscribers());
            wkChannelService.createOrUpdate(wkChannel);
        }catch (Exception e){
            log.error("create or update channel error,channelId:{},type:{}",channel.getCid(),channel.getType());
        }
        log.info("create or update channel success,channelId:{},type:{}", channel.getCid(),channel.getType());
        return channel;
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


    @Override
    public void updateToken(UpdateTokenDTO dto) {
        try {
            wkUserService.updateToken(dto);
        }catch (Exception e){
            log.error("update srsToken error,uid:{},flag:{}",dto.getUid(),dto.getFlag());
        }
        log.info("update srsToken success,uid:{}", dto.getUid());
    }

//    @Override
//    public RouteVO getRoute(long uid) {
//        try {
//             return wkRouteService.getRoute().body();
//        }catch (Exception e){
//            log.error("get route error,uid:{},flag:{}",uid,e.getMessage());
//            return null;
//        }
//    }


    @Override
    public void dropSubscriber(SubscribersRemoveDTO dto) {
        try{
            wkChannelService.removeSubscribers(dto);
        }catch (Exception e){
            log.error("remove subscribers error,channelId:{},type:{},reason:{}",dto.getChannelId(),dto.getChannelType(),e.getMessage());
        }
        log.info("remove subscribers success channelId:{},type:{}", dto.getChannelId(),dto.getChannelType());

    }

}
