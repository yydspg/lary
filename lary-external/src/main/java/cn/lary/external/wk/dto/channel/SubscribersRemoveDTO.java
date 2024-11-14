package cn.lary.external.wk.dto.channel;

import cn.lary.external.wk.entity.core.WKChannel;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
@Data
public class SubscribersRemoveDTO extends WKChannel {
    @JSONField(format="temp_subscriber")
    private int tempSubscribers;
    private List<String> subscribers;
}
