package cn.lary.group.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

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
    private Long id;

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

    


}
