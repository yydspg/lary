package cn.lary.group.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
public class Group implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 群id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long gid;

    /**
     * 群名字
     */
    private String name;

    /**
     * 群主uid
     */
    private Long creator;

    /**
     * 群状态
     */
    private Integer status;

    /**
     * 群禁言
     */
    private Boolean isForbidden;

    /**
     * 群头像路径
     */
    private String groupAvatar;

    /**
     * 群人数
     */
    private Integer groupNum;

    /**
     * 群头像是否已经被上传
     */
    private Boolean isUploadAvatar;

    /**
     * 群类型 0. 普通群，1.超大群
     */
    private Integer groupType;

    /**
     * 群分类
     */
    private Integer category;

    /**
     * 群邀请开关
     */
    private Boolean isEnableInvite;

    /**
     * 是否禁止群内加好友
     */
    private Boolean isForbiddenAddFriend;

    /**
     * 是否允许新成员查看历史消息
     */
    private Boolean isAllowViewHistoryMsg;

    private Long createAt;


}
