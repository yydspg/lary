package cn.lary.module.gift.entity;

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
 * @since 2024-08-16
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("gift_type")
public class GiftType implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 父类id
     */
    private Integer parentId;

    private String name;

    /**
     * current type gift num
     */
    private Integer count;

    /**
     * 类别图片
     */
    private String avatar;

    /**
     * 是否特殊
     */
    private Boolean isPrivilege;

    



}
