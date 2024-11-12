package cn.lary.module.comment.listener;

import cn.lary.module.common.constant.LARY;
import cn.lary.module.message.service.AbstractSyncRocketMessage;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class BehaviorMessage extends AbstractSyncRocketMessage {

    private long cid;
    private long rid;
    private int category;

    @Override
    public int getBusinessSign() {
        return LARY.COMMENT.BEHAVIOR.COMMENT;
    }

    @Override
    public String getLogData() {
        return "";
    }

    @Override
    public long getTimeOut() {
        return 0;
    }

    @Override
    public int getDelayLevel() {
        return 0;
    }
}
