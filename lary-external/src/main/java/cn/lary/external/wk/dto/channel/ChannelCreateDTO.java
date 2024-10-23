package cn.lary.external.wk.dto.channel;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
@Data
public  class ChannelCreateDTO  {

    @JsonProperty("channel_id")
    private int channelId;

    @JsonProperty("channel_type")
    private int channelType;

    private int large;

    private int ban;

    private List<Integer> subscribers;
 }
