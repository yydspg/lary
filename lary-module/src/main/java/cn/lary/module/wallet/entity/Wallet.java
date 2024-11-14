package cn.lary.module.wallet.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.math.BigDecimal;
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
 * @since 2024-11-01
 */
@Getter
@Setter
@Accessors(chain = true)
public class Wallet implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 钱包id
     */
    private Long wid;

    /**
     * 用户id
     */
    private Long uid;

    /**
     * 收入流水总金额
     */
    private BigDecimal income;

    /**
     * 支出流水总金额
     */
    private BigDecimal outcome;
    /**
     * 零钱
     */
    private BigDecimal pocket;

    /**
     * 总金额
     */
    private BigDecimal amount;

    /**
     * 虚拟货币数量
     */
    private Long vcCount;

    /**
     * 用户角色
     */
    private Integer role;

    /**
     * 钱包状态
     */
    private Integer status;


    
}
