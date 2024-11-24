package cn.lary.redpacket.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class RedpacketEventCache {

    /**
     * 直播id
     */
    private long sid;

    /**
     * 事件id
     */
    private long eid;

    /**
     * 直播间id
     */
    private long rid;

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
    private long startAt;

    /**
     * 持续时间
     */
    private long duration;
}
