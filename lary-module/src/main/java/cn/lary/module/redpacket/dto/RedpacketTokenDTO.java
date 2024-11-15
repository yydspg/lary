package cn.lary.module.redpacket.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class RedpacketTokenDTO {
    /**
     * record id
     */
    private long rid;

    /**
     * 用户id
     */
    private long uid;

    /**
     * 直播id
     */
    private long sid;

    /**
     * 红包金额
     */
    private BigDecimal amount;
}
