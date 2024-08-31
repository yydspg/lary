package cn.lary.module.user.entity;

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
 * @since 2024-07-29
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("friend_apply_record")
public class FriendApplyRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String uid;

    private String toUid;

    private String remark;

    private Short status;

    private String token;

    private Boolean isDeleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createAt;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateAt;
}
