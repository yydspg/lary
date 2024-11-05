package cn.lary.module.raffle.vo;

import cn.lary.module.raffle.entity.RaffleRecord;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RaffleRecordVO {
    /**
     * 用户id
     */
    private long uid;

    /**
     * 直播id
     */
    private long streamId;
    /**
     * 抽奖id
     */
    private long raffleId;
    /**
     * 用户id
     */
    private long toUid;

    /**
     * 抽奖物品描述
     */
    private String content;

    /**
     * 同步状态
     */
    private int syncStatus;

    private LocalDateTime createAt;

    public RaffleRecordVO(RaffleRecord data) {
        this.uid = data.getUid();
        this.streamId = data.getStreamId();
        this.raffleId = data.getRaffleId();
        this.toUid = data.getToUid();
        this.content= data.getContent();
        this.syncStatus = data.getSyncStatus();
        this.createAt = data.getCreateAt();
    }
}
