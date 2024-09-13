package cn.lary.module.gift.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
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
 * @since 2024-08-16
 */
@Getter
@Setter
@Accessors(chain = true)
public class Gift implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * gift type
     */
    private Integer type;

    private String typeName;

    /**
     * virtual currency price
     */
    private Integer price;

    /**
     * real pay price CNY
     */
    private Integer realPrice;

    private String name;

    /**
     * 购买数量
     */
    private Long purchaseNum;

    /**
     * gift logo
     */
    private String pictureUrl;

    private Boolean isDelete;

    private String createBy;

    private String updateBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createAt;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateAt;
}
