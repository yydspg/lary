package cn.lary.module.order.entity;

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
 * @since 2024-10-28
 */
@Getter
@Setter
@Accessors(chain = true)
public class Receipt implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 订单id
     */
    private Long orderId;

    /**
     * uid
     */
    private Long uid;

    /**
     * 用户
     */
    private String username;

    /**
     * 发票内容
     */
    private String content;

    /**
     * 发票金额
     */
    private String amount;

    /**
     * 发票状态
     */
    private Integer status;

    /**
     * 发票抬头
     */
    private String title;

    /**
     * 店铺ID
     */
    private String storeId;

    /**
     * 店铺名称
     */
    private String storeName;

    /**
     * 纳税人识别号
     */
    private String taxpayerId;

 

      

    
}
