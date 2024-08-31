package cn.lary.pkg.wk.entity.Request.channel;

import cn.lary.pkg.wk.entity.core.Channel;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
@Data
public class SubscribersRemoveReq extends Channel {
    @JsonProperty("temp_subscriber")
    private int tempSubscribers;
    private List<String> subscribers;
}
