package cn.lary.gift.entity;

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
     * gift type id
     */
    private Integer type;

    private String typeName;

    /**
     * virtual currency price
     */
    private Long vcAmount;

    /**
     * real pay price CNY
     */
    private BigDecimal amount;

    private String name;

    /**
     * gift logo
     */
    private String avatar;

    



}
