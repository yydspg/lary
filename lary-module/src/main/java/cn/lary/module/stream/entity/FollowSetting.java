package cn.lary.module.stream.entity;

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
 * @since 2024-10-10
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("follow_setting")
public class FollowSetting implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    private Long uid;

    /**
     * 被关注id
     */
    private Long toUid;

    /**
     * 是否静默
     */
    private Boolean isMute;

    /**
     * 是否置顶
     */
    private Boolean isTop;

    /**
     * 是否删除
     */
    private Boolean isDelete;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createAt;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateAt;
}
