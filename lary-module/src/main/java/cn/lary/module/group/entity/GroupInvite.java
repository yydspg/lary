package cn.lary.module.group.entity;

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
 * @since 2024-07-29
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("group_invite")
public class GroupInvite implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 邀请唯一编号
     */
    private String inviteNo;

    /**
     * 群唯一编号
     */
    private String groupNo;

    /**
     * 邀请者uid
     */
    private String inviter;

    /**
     * 备注
     */
    private String remark;

    /**
     * 状态： 0.待确认 1.已确认
     */
    private Short status;

    /**
     * 允许此次操作的用户uid
     */
    private String allower;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createAt;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateAt;
}
