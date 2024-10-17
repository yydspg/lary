package cn.lary.pkg.wk.dto.channel;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
@Data
public  class ChannelCreateDTO  {
    @JsonProperty("channel_id")
    private Integer channelId;
    @JsonProperty("channel_type")
    private Integer channelType;
    private int large;
    private int ban;
    private List<Integer> subscribers;
 }
