package cn.lary.module.wallet.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("wallet_outcome")
public class WalletIncome implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    private Long uid;

    /**
     * id
     */
    private Long toUid;

    /**
     * 频道id
     */
    private Long channel;

    /**
     * 频道类型
     */
    private Short category;

    /**
     * 交易类型
     */
    private Short type;

    /**
     * 花费
     */
    private BigDecimal amount;

    /**
     * 同步状态
     */
    private Integer syncStatus;

    private Boolean isDelete;

      @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createAt;
}
