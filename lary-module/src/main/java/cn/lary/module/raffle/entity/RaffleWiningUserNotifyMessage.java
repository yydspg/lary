package cn.lary.module.raffle.entity;

import cn.lary.external.wk.dto.message.MessageHeader;
import cn.lary.external.wk.dto.message.MessageSendDTO;
import cn.lary.module.common.constant.LARY;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class RaffleWiningUserNotifyMessage extends MessageSendDTO {

    public RaffleWiningUserNotifyMessage(long uid, List<Long> users,String content) {
        setChannelID(uid);
        setFromUID(uid);
        setChannelType(LARY.CHANNEL.TYPE.TEMP);
        setHeader(new MessageHeader().setRedDot(1));
        setSubscribers(users);
        setPayload(content.getBytes(StandardCharsets.UTF_8));
    }
}
