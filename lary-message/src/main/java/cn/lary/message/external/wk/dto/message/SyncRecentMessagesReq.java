package cn.lary.message.external.wk.dto.message;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

import java.util.List;

@Data
public class SyncRecentMessagesReq {
    private Long uid;
    private List<WKChannelRecentMessage> channels;
    @JSONField(format="msg_count")
    private int msgCount;
    @JSONField(format="order_by_last")
    private int orderByLast;
}
