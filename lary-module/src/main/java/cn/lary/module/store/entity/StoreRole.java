package cn.lary.module.store.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("store_role")
public class StoreRole implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 角色名称
     */
    private String name;

    /**
     * 店铺id
     */
    private Long storeId;

    /**
     * 是否为注册默认角色
     */
    private Boolean isDefaultRole;

    /**
     * 备注
     */
    private String description;

    private Boolean isDelete;

      @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createAt;

      @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateAt;
}
