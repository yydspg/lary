package cn.lary.pkg.wk.entity.Request.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class SyncRecentMessagesReq {
    private String uid;
    private List<ChannelRecentMessage> channels;
    @JsonProperty("msg_count")
    private int msgCount;
    @JsonProperty("order_by_last")
    private int orderByLast;
}
