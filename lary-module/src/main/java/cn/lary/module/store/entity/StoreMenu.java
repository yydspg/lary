package cn.lary.module.store.entity;

import com.baomidou.mybatisplus.annotation.*;

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
@TableName("store_menu")
public class StoreMenu implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    /**
     * 说明备注
     */
    private String description;

    /**
     * 前端路由
     */
    private String frontRoute;

    /**
     * 图标
     */
    private String icon;

    /**
     * 层级
     */
    private Integer level;

    /**
     * 菜单/权限名称
     */
    private String name;

    /**
     * 父id
     */
    private String parentId;

    /**
     * 赋权API地址,正则表达式
     */
    private String path;

    /**
     * 排序值
     */
    private String sortOrder;

    /**
     * 菜单标题
     */
    private String title;

    /**
     * 权限url
     */
    private String permission;

    private Boolean isDelete;

      @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createAt;

      @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateAt;
}
