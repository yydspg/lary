package cn.lary.module.pay.listener;

import cn.lary.module.message.service.AbstractAsyncRocketMessage;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public abstract class AbstractActiveDetectionMessage extends AbstractAsyncRocketMessage {

    private int eventId;

    private int paymentId;

    private int uid;

    private int plugin;

    private int client;
}
