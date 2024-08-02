package cn.lary.pkg.wk.entity.Request.channel;

import cn.lary.pkg.wk.entity.core.Channel;
import lombok.Data;

import java.util.List;
@Data
public  class ChannelCreateReq extends Channel {
    private int large;
    private int ban;
    private List<String> subscribers;
 }
