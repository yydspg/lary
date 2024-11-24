package cn.lary.comment.listener;

import cn.lary.comment.constant.COMMENT;
import cn.lary.common.rocketmq.AbstractSyncRocketMessage;
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
        return COMMENT.BEHAVIOR.COMMENT;
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
