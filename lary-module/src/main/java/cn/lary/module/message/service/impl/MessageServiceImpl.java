package cn.lary.module.message.service.impl;

import cn.lary.external.wk.api.WKChannelService;
import cn.lary.external.wk.api.WKMessageService;
import cn.lary.external.wk.api.WKUserService;
import cn.lary.external.wk.api.WkRouteService;
import cn.lary.external.wk.dto.channel.WKChannelCreateDTO;
import cn.lary.external.wk.dto.channel.SubscribersAddDTO;
import cn.lary.external.wk.dto.message.MessageSendDTO;
import cn.lary.external.wk.dto.user.UpdateTokenDTO;
import cn.lary.external.wk.vo.route.RouteVO;
import cn.lary.module.channel.dto.ChannelBuildDTO;
import cn.lary.module.channel.entity.LaryChannel;
import cn.lary.module.channel.service.LaryChannelService;
import cn.lary.module.message.service.AbstractAsyncRocketMessage;
import cn.lary.module.message.service.AbstractSyncRocketMessage;
import cn.lary.module.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

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
    private final TransactionTemplate transactionTemplate;

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

    @Override
    public RouteVO getRoute(long uid) {
        try {
             return wkRouteService.getRoute().body();
        }catch (Exception e){
            log.error("get route error,uid:{},flag:{}",uid,e.getMessage());
            return null;
        }
    }

    @Override
    public void syncSendRocketMessage(AbstractSyncRocketMessage message) {
        GenericMessage rocketMQMessage = new GenericMessage<>(message);
        try {
            rocketMQTemplate.syncSend("lary:raffle-close",rocketMQMessage,message.getTimeOut(),message.getDelayLevel());
        } catch (Exception e) {
            log.error("sync send {} message error,reason:{}",message.getBusinessSign(),e.getMessage());
        }
        log.info("sync send {} message success,:{}",message.getBusinessSign(),message.getLogData());
    }

    @Override
    public void asyncSendRocketMessage(AbstractAsyncRocketMessage message) {
        GenericMessage rocketMQMessage = new GenericMessage<>(message);
        try {
            rocketMQTemplate.asyncSend("lary:raffle-close",rocketMQMessage,message.getSendCallback(),message.getTimeOut(),message.getDelayLevel());
        } catch (Exception e) {
            log.error("async send {} message error,reason:{}",message.getBusinessSign(),e.getMessage());
        }
        log.info("async send {} message success,:{}",message.getBusinessSign(),message.getLogData());
    }

}
