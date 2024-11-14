package cn.lary.module.stream.vo;

import cn.lary.module.raffle.dto.RaffleEventCache;
import cn.lary.module.redpacket.entity.RedpacketEventCache;
import com.alibaba.fastjson.annotation.JSONField;
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

    @JSONField(format="raffle")
    private RaffleEventCache raffle;


    @JSONField(format="redpacket")
    private RedpacketEventCache redPacket;

}
