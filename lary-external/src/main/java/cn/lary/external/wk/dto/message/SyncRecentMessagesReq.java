package cn.lary.external.wk.dto.message;

import com.fasterxml.jackson.annotation.JsonProperty;
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
