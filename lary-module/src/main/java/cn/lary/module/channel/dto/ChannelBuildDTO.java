package cn.lary.module.channel.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class ChannelBuildDTO {

    private int type;

    private long uid;

    private long rid;

    private List<Long> subscribers;
}
