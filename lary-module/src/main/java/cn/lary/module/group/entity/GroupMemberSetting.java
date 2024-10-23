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
 * @since 2024-10-15
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("group_setting")
public class GroupMemberSetting implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 是否置顶
     */
    private Boolean isTop;

    /**
     * 是否免打扰
     */
    private Boolean isNoDisturb;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 群聊备注
     */
    private String remark;

    /**
     * 是否隐藏会话
     */
    private Boolean isHidden;

    /**
     * 是否开启进群提醒
     */
    private Boolean isJoinGroupRemind;

    /**
     * 是否显示群成员昵称
     */
    private Boolean isShowNickname;

    private Boolean isDelete;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createAt;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateAt;
}
