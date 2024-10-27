package cn.lary.module.wallet.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

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
public class Wallet implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    private Long uid;

    /**
     * vc 收入总数量
     */
    private Long vcIncome;

    /**
     * vc 使用总数量
     */
    private Long vcOutcome;

    /**
     * vc 剩余总数量
     */
    private Long vcFee;

    /**
     * 被锁定的vc
     */
    private Long vcLocked;

    /**
     * 是否为主播
     */
    private Boolean isAnchor;

    /**
     * 是否被锁定
     */
    private Boolean isBlock;

    /**
     * 锁定原因
     *
     */
    private Boolean blockReason;
    /**
     * 安全问题，用于解锁钱包
     */
    private String secQuestion;

    /**
     * 安全问题答案
     */
    private String secAnswer;

    private Boolean isDelete;

    

    

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createAt;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateAt;
}
