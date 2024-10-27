package cn.lary.module.message.service;


import cn.lary.external.wk.dto.channel.ChannelCreateDTO;
import cn.lary.external.wk.dto.channel.SubscribersAddDTO;
import cn.lary.external.wk.dto.message.MessageSendDTO;
import cn.lary.external.wk.dto.user.UpdateTokenDTO;
import cn.lary.external.wk.vo.route.RouteVO;

public interface MessageService {
    /**
     * 发送消息<br>
     * wukongIM支持临时消息发送<br>
     * 临时消息无需channelId,IM自动生成
     * @param dto {@link MessageSendDTO}
     * @return ok
     */
    void  send(MessageSendDTO dto);

    /**
     * 创建频道
     * @param dto {@link ChannelCreateDTO}
     * @return ok
     */
    void saveOrUpdateChannel(ChannelCreateDTO dto);

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

    /**
     * 获取IM的节点
     * @param uid U
     * @return ok
     */
    RouteVO getRoute(long uid);


    void syncSendRocketMessage(AbstractSyncRocketMessage message);

    void asyncSendRocketMessage(AbstractAsyncRocketMessage message);
}
