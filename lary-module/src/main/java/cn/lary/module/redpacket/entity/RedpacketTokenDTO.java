package cn.lary.module.redpacket.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class RedpacketTokenDTO {

    private long id;

    /**
     * 用户id
     */
    private long uid;

    /**
     * 直播id
     */
    private long streamId;

    /**
     * 红包金额
     */
    private BigDecimal amount;
}
