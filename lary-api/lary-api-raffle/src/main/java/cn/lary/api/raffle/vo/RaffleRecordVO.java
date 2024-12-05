package cn.lary.api.raffle.vo;

import cn.lary.api.raffle.entity.RaffleRecord;
import lombok.Data;

@Data
public class RaffleRecordVO {
    /**
     * 用户id
     */
    private long uid;

    /**
     * 直播id
     */
    private long sid;
    /**
     * 抽奖id
     */
    private long rid;
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
    private int sync;

    private long createAt;

    public RaffleRecordVO(RaffleRecord data) {
        this.uid = data.getUid();
        this.sid = data.getSid();
        this.rid = data.getRid();
        this.toUid = data.getToUid();
        this.content= data.getContent();
        this.sync = data.getSync();
        this.createAt = data.getCreateAt();
    }
}
