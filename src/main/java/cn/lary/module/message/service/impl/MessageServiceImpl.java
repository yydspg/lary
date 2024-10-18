package cn.lary.module.message.service.impl;

import cn.lary.module.message.service.MessageService;
import cn.lary.pkg.wk.api.WKChannelService;
import cn.lary.pkg.wk.api.WKMessageService;
import cn.lary.pkg.wk.api.WKUserService;
import cn.lary.pkg.wk.api.WkRouteService;
import cn.lary.pkg.wk.dto.channel.ChannelCreateDTO;
import cn.lary.pkg.wk.dto.channel.SubscribersAddDTO;
import cn.lary.pkg.wk.dto.message.MessageSendDTO;
import cn.lary.pkg.wk.dto.user.UpdateTokenDTO;
import cn.lary.pkg.wk.vo.route.RouteVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final WKMessageService wkMessageService;
    private final WKChannelService wkChannelService;
    private final WKUserService wkUserService;
    private final WkRouteService wkRouteService;

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

    @Override
    public void updateToken(UpdateTokenDTO dto) {
        try {
            wkUserService.updateToken(dto);
        }catch (Exception e){
            log.error("update token error,uid:{},flag:{}",dto.getUid(),dto.getFlag());
        }
        log.info("update token success,uid:{}", dto.getUid());
    }

    @Override
    public RouteVO getRoute(int uid) {
        try {
             return wkRouteService.getRoute().body();
        }catch (Exception e){
            log.error("get route error,uid:{},flag:{}",uid,e.getMessage());
            return null;
        }
    }
}
