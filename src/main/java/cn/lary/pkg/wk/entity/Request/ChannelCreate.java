package cn.lary.pkg.wk.entity.Request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
@Data
public  class ChannelCreate {
    @JsonProperty("channel_id")
    private String chanelID;
    @JsonProperty("channel_type")
    private byte channelType;
    private int large;
    private int ban;
    private List<String> subscribers;
 }
