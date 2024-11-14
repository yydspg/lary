package cn.lary.module.store.entity;

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
 * @since 2024-10-28
 */
@Getter
@Setter
@Accessors(chain = true)
public class Store implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 店铺名称
     */
    private String name;

    /**
     * 店铺头像
     */
    private String avatar;

    /**
     * 店铺简介
     */
    private String description;

    /**
     * 物流评分
     */
    private BigDecimal deliveryScore;

    /**
     * 描述评分
     */
    private BigDecimal descriptionScore;

    /**
     * 服务评分
     */
    private BigDecimal serviceScore;

    /**
     * 商品数量
     */
    private Integer goodsNum;

    /**
     * 收藏数量
     */
    private Integer collectionNum;

    

 

      
}
