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
 * @since 2024-10-16
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("group_member")
public class GroupMember implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 独立id
     */
    private Long mid;

    /**
     * 群聊id
     */
    private Long gid;

    /**
     * 用户id
     */
    private Long uid;


    /**
     * 群聊备注
     */
    private String remark;

    /**
     * 本群昵称
     */
    private String nickname;

    /**
     * 群聊角色
     */
    private Integer role;

    /**
     * 群聊状态
     */
    private Integer status;

    /**
     * 群成员头像
     */
    private String avatar;

    /**
     * 群成员用户名
     */
    private String name;

    /**
     * 群成员禁言时长
     */
    private Integer forbiddenExpireTime;

    /**
     * 验证码
     */
    private String verifyCode;

    /**
     * 是否是机器人
     */
    private Boolean isRobot;

    /**
     * 邀请人uid
     */
    private Long inviteUid;

    


}
