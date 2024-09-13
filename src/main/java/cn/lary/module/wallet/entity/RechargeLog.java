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

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 充值id，充值编号
     */
    private String rechargeId;

    /**
     * 用户uid
     */
    private String uid;

    /**
     * 购买状态 0 未支付，1 已支付 2 取消支付 3 支付失败
     */
    private Short status;

    /**
     * 是否同步，比如wallet是否同步成功
     */
    private Boolean isSync;

    /**
     * 完成时间
     */
    private LocalDateTime completeAt;

    /**
     * 花费
     */
    private Integer cost;

    /**
     * 获得的虚拟货币数目
     */
    private Integer starNum;

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
    private Integer clientType;

    private Boolean isDelete;

    private String createBy;

    private String updateBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createAt;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateAt;
}
