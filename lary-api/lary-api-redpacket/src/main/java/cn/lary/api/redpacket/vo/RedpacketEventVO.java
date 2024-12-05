package cn.lary.api.redpacket.vo;

import cn.lary.api.redpacket.dto.RedpacketEventCache;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RedpacketEventVO {
    /**
     * 直播id
     */
    private long sid;

    /**
     * 用户uid
     */
    private long uid;

    /**
     * 红包金额
     */
    private BigDecimal amount;

    /**
     * 红包标题
     */
    private String title;

    /**
     * 红包人数
     */
    private int num;


    /**
     * 开启时间
     */
    private LocalDateTime startAt;

    /**
     * 持续时间
     */
    private long duration;

    public RedpacketEventVO() {}

    public RedpacketEventVO(RedpacketEventCache event) {
        this.sid = event.getSid();
        this.uid = event.getUid();
        this.amount = event.getAmount();
        this.title = event.getTitle();
        this.num = event.getNum();
    }

}
