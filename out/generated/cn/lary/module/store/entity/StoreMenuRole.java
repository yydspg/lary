package cn.lary.module.store.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
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
@TableName("store_menu_role")
public class StoreMenuRole implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
      private Long id;

    /**
     * 角色id
     */
    private Long roleId;

    /**
     * 菜单
     */
    private Long menuId;

    /**
     * 店铺id
     */
    private Long storeId;

    /**
     * 是否拥有操作数据权限，为否则只有查看权限
     */
    private Boolean isSuper;
}
