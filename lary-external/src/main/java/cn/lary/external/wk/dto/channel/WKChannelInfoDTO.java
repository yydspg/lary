package cn.lary.external.wk.dto.channel;

import cn.lary.external.wk.entity.core.WKChannel;
import lombok.Data;

@Data
public class WKChannelInfoDTO extends WKChannel {
    private int large;
    private int ban;
    private int disband;
}
