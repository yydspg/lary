package cn.lary.module.pay.listener;

import cn.lary.module.message.service.AbstractAsyncRocketMessage;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public abstract class AbstractActiveDetectionMessage extends AbstractAsyncRocketMessage {

    private long eventId;

    private int paymentId;

    private long uid;

    private int plugin;

    private int client;
}
