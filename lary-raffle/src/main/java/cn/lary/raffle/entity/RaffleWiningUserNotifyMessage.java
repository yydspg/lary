package cn.lary.raffle.entity;

import cn.lary.api.message.dto.YutakMessage;
import cn.lary.common.constant.LARY;


import java.nio.charset.StandardCharsets;
import java.util.List;

public class RaffleWiningUserNotifyMessage extends YutakMessage {

    public RaffleWiningUserNotifyMessage(long uid, List<Long> users,String content) {
        setCid(uid);
        setFid(uid);
//        setType(LARY.CHANNEL.TYPE.TEMP);
        setRedDot(1);
        setSubscribers(users);
        setPayload(content);
    }
}
