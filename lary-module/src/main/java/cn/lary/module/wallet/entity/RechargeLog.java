package cn.lary.module.wallet.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author paul
 * @since 2024-09-13
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("recharge_log")
public class RechargeLog implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 充值id，充值编号
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户uid
     */
    private Integer uid;

    /**
     * 购买状态 0 未支付，1 已支付 2 取消支付 3 支付失败
     */
    private Integer status;

    /**
     * 是否同步，比如wallet是否同步成功
     */
    private Boolean isSync;

    /**
     * 完成时间
     */
    private LocalDateTime completeAt;

    /*
    外部的支付订单号
     */
    private String sn;

    /**
     * 花费
     */
    private Long cost;

    /**
     * 获得的虚拟货币数目
     */
    private Long starNum;

    /**
     * 异步通知地址
     */
    private String notifyUrl;

    /**
     * 上游异常原因
     */
    private String failReason;

    /**
     * 充值事件id
     */
    private Integer eventId;
    /**
     * 支付来源
     */
    private Integer clientType;

    private Boolean isDelete;


    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createAt;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateAt;
}
