package cn.lary.pkg.wk.dto.channel;

import cn.lary.pkg.wk.entity.core.WKChannel;
import lombok.Data;

@Data
public class WKChannelInfoReq extends WKChannel {
    private int large;
    private int ban;
    private int disband;
}
