package cn.lary.message.service;


import cn.lary.message.channel.dto.ChannelBuildDTO;
import cn.lary.message.channel.entity.LaryChannel;
import cn.lary.message.external.wk.dto.channel.SubscribersAddDTO;
import cn.lary.message.external.wk.dto.channel.SubscribersRemoveDTO;
import cn.lary.message.external.wk.dto.channel.WKChannelCreateDTO;
import cn.lary.message.external.wk.dto.message.MessageSendDTO;
import cn.lary.message.external.wk.dto.user.UpdateTokenDTO;


public interface MessageService {
    /**
     * 发送消息<br>
     * wukongIM支持临时消息发送<br>
     * 临时消息无需channelId,IM自动生成
     * @param dto {@link MessageSendDTO}
     */
    void  send(MessageSendDTO dto);

    /**
     * 创建频道
     * @param dto {@link WKChannelCreateDTO}
     */
    LaryChannel saveOrUpdateChannel(ChannelBuildDTO dto);

    /**
     * 添加订阅者
     * @param dto {@link SubscribersAddDTO}
     */
    void addSubscriber(SubscribersAddDTO dto);

    /**
     * 更新token
     * @param dto {@link UpdateTokenDTO}
     */
    void updateToken(UpdateTokenDTO dto);

//    /**
//     * 获取IM的节点
//     * @param uid U
//     * @return OK
//     */
//    RouteVO getRoute(long uid);

//
//    void syncSendRocketMessage(AbstractSyncRocketMessage message);
//
//    void asyncSendRocketMessage(AbstractAsyncRocketMessage message);

    void dropSubscriber(SubscribersRemoveDTO dto);
}
