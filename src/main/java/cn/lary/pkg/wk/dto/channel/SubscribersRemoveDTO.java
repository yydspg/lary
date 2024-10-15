package cn.lary.pkg.wk.dto.channel;

import cn.lary.pkg.wk.entity.core.WKChannel;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
@Data
public class SubscribersRemoveDTO extends WKChannel {
    @JsonProperty("temp_subscriber")
    private int tempSubscribers;
    private List<String> subscribers;
}
