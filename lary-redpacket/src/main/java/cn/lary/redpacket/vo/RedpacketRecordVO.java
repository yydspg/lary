package cn.lary.redpacket.vo;

import cn.lary.redpacket.entity.RedpacketRecord;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RedpacketRecordVO {
    /**
     * 直播id
     */
    private long sid;

    /**
     * 红包金额
     */
    private BigDecimal amount;

    /**
     * 同步状态
     */
    private int sync;

    public RedpacketRecordVO() {}

    public RedpacketRecordVO(RedpacketRecord record) {
        this.sid = record.getSid();
        this.amount = record.getAmount();
        this.sync = record.getSync();
    }
}
