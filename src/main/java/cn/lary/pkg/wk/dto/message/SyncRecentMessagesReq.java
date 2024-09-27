package cn.lary.pkg.wk.dto.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class SyncRecentMessagesReq {
    private Integer uid;
    private List<WKChannelRecentMessage> channels;
    @JsonProperty("msg_count")
    private int msgCount;
    @JsonProperty("order_by_last")
    private int orderByLast;
}
