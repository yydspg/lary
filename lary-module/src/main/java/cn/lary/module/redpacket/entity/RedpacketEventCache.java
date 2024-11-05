package cn.lary.module.redpacket.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class RedpacketEventCache {
    /**
     * 直播id
     */
    private long stream;

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
}
