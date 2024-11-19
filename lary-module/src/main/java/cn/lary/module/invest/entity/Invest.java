package cn.lary.module.invest.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 
 * </p>
 *
 * @author paul
 * @since 2024-11-17
 */
@Getter
@Setter
@Accessors(chain = true)
public class Invest implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 投放id
     */
    private Long lid;

    /**
     * 用户id
     */
    private Long uid;

    /**
     * 直播间id
     */
    private Long rid;

    private Integer tag;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 支付状态
     */
    private Integer status;

    /**
     * 投放策略,长期,单次
     */
    private Integer policy;

    /**
     * 持续时间
     */
    private Long duration;

    /**
     * 直播开始投放,直播全程投放,当全程投放生效时,持续时间无效,立刻生效
     */
    private Integer category;
    /**
     * 外部订单id
     */
    private String sn;

    /**
     * 创建时间
     */
    private Long createAt;

    private Long eid;

    private String reason;
    /**
     * 状态
     */
    private Integer sync;
}
