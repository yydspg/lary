package cn.lary.api.redpacket.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author paul
 * @since 2024-10-31
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("redpacket_event")
public class RedpacketEvent implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 直播id
     */
    private Long stream;

    /**
     * 用户uid
     */
    private Long uid;

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
    private Integer num;

    /**
     * 红包类型
     */
    private Integer category;

    /**
     * 外部订单号
     */
    private String outTradeNo;

    /**
     * 购买状态 0 未支付，1 已支付 2 取消支付 3 支付失败
     */
    private Integer status;

    /**
     * 同步状态
     */
    private Integer syncStatus;

    /**
     * 完成时间
     */
    private LocalDateTime completeAt;

    /**
     * 异步通知地址
     */
    private String notifyUrl;

    /**
     * 上游异常原因
     */
    private String failReason;

    /**
     * 支付来源
     */
    private Integer payWay;

    /**
     * 开启时间
     */
    private LocalDateTime startAt;

    /**
     * 持续时间
     */
    private Long duration;

    

 
}
