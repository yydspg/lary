package cn.lary.pkg.wk.entity.Request.channel;

import cn.lary.pkg.wk.entity.core.Channel;
import lombok.Data;

@Data
public class ChannelInfoReq extends Channel {
    private int large;
    private int ban;
    private int disband;
}
