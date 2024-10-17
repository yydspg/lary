package cn.lary.module.message.service;

import cn.lary.pkg.wk.dto.channel.ChannelCreateDTO;
import cn.lary.pkg.wk.dto.channel.SubscribersAddDTO;
import cn.lary.pkg.wk.dto.message.MessageSendDTO;

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
}
