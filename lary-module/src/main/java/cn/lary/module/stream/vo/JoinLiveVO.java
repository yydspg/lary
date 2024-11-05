package cn.lary.module.stream.vo;

import cn.lary.module.raffle.entity.RaffleEventCache;
import cn.lary.module.cache.dto.RedPacketCacheDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class JoinLiveVO {

    private long danmakuId;

    private String specify;
    /**
     * 回调 token
     */
    private String token;

    @JsonProperty("raffle")
    private RaffleEventCache raffle;
    @JsonProperty("redpacket")
    private RedPacketCacheDTO redPacket;

}
