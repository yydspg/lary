package cn.lary.module.redpacket.vo;

import cn.lary.module.redpacket.entity.RedpacketRecord;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RedpacketRecordVO {
    /**
     * 直播id
     */
    private Long streamId;

    /**
     * 红包金额
     */
    private BigDecimal amount;

    /**
     * 同步状态
     */
    private Integer syncStatus;

    public RedpacketRecordVO() {}

    public RedpacketRecordVO(RedpacketRecord record) {
        this.streamId = record.getStreamId();
        this.amount = record.getAmount();
        this.syncStatus = record.getSyncStatus();
    }
}
