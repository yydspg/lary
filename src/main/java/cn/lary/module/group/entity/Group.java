package cn.lary.module.group.entity;

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
@TableName(value = "lary_groups")
public class Group implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String groupNo;

    private String name;

    private String creator;

    private Short status;

    /**
     * 群禁言
     */
    private boolean forbidden;

    /**
     * 群头像路径
     */
    private String groupAvatarUrl;

    /**
     * 群头像是否已经被上传
     */
    private Boolean isUploadAvatar;

    /**
     * 群类型 0. 普通群，1.超大群
     */
    private Short groupType;

    /**
     * 群分类
     */
    private String category;

    /**
     * 群邀请开关
     */
    private Boolean invite;

    private Boolean forbiddenAddFriend;

    private Boolean allowViewHistoryMsg;

    private Long version;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createAt;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateAt;

    private String notice;
}