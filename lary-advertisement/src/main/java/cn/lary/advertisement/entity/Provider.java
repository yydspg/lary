package cn.lary.advertisement.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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
 * @since 2024-11-17
 */
@Getter
@Setter
@Accessors(chain = true)
public class Provider implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 服务商id
     */
    private Long pid;

    /**
     * 名称
     */
    private Long name;

    /**
     * 总投入
     */
    private BigDecimal amount;

    /**
     * 服务商等级
     */
    private Integer level;



    private Integer status;

    /**
     * 创建时间
     */
      @TableField(fill = FieldFill.INSERT)
    private Long createAt;
}
