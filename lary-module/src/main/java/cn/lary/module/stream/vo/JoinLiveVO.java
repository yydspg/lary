package cn.lary.module.stream.vo;

import cn.lary.module.stream.dto.RaffleCacheDTO;
import cn.lary.module.stream.dto.RedPacketCacheDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class JoinLiveVO {

    private int danmakuId;

    private String specify;
    /**
     * 回调 token
     */
    private String token;

    @JsonProperty("raffle")
    private RaffleCacheDTO raffle;
    @JsonProperty("redpacket")
    private RedPacketCacheDTO redPacket;

}
