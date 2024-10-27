package cn.lary.module.wallet.entity;

import cn.lary.module.wallet.dto.TransferDTO;
import com.baomidou.mybatisplus.annotation.*;
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
 * @since 2024-10-02
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("wallet_outcome")
public class WalletOutcome implements Serializable {

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
    private Long channelId;

    /**
     * 频道类型
     */
    private Integer channelType;

    /**
     * 交易类型
     */
    private Integer type;

    /**
     * 花费
     */
    private Long cost;

    private Boolean isDelete;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createAt;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateAt;

    public static WalletOutcome of(TransferDTO dto) {
        WalletOutcome o = new WalletOutcome();
        o.setUid(dto.getUid());
        o.setChannelId(dto.getChannelId());
        o.setChannelType(dto.getChannelType());
        o.setType(dto.getType());
        o.setCost(dto.getAmount());
        o.setToUid(dto.getToUid());
        return o;
    }
}
