package cn.lary.wallet.entity;

import cn.lary.wallet.dto.TransferDTO;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;

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
     * 收入id
     */
    private Long oid;
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
    private Integer category;

    /**
     * 交易类型
     */
    private Integer type;

    /**
     * 花费
     */
    private BigDecimal amount;

    /**
     * 同步状态
     */
    private Integer sync;

    public static WalletIncome of(TransferDTO dto) {
          WalletIncome income = new WalletIncome();
          income.setUid(dto.getUid());
          income.setToUid(dto.getToUid());
          income.setAmount(dto.getAmount());
          income.setChannel(dto.getChannel());
          income.setCategory(dto.getCategory());
          income.setType(dto.getType());
          return income;
      }
}
