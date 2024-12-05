package cn.lary.api.stream.vo;


import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class JoinLiveVO {

    private long danmakuId;

    private String specify;
    /**
     * 回调 srsToken
     */
    private String token;
//
//    @JSONField(format="raffle")
//    private RaffleEventCache raffle;
//
//
//    @JSONField(format="redpacket")
//    private RedpacketEventCache redPacket;

}
