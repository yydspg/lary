package cn.lary.module.stream.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
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
 * @since 2024-10-10
 */
@Getter
@Setter
@Accessors(chain = true)
public class Follow implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    private Integer uid;

    /**
     * 被关注id
     */
    private Integer toUid;

    /**
     * 被关注姓名
     */
    private String username;

    /**
     * 业务来源
     */
    private Short source;

    /**
     * 被关注者简介
     */
    private String bio;

    /**
     * 被关注者头像
     */
    private String avatar;

    /**
     * 备注
     */
    private String remark;

    /**
     * 是否是主播
     */
    private Boolean isAnchor;

    /**
     * 是否被拉黑
     */
    private Boolean isBlock;

    /**
     * 是否不关注,取消关注后此字段为 true
     */
    private Boolean isUnfollow;

    /**
     * 是否是单向好友
     */
    private Boolean isOneWay;

    /**
     * 是否删除
     */
    private Boolean isDelete;

    /**
     * 粉丝等级
     */
    private Integer level;

    /**
     * 对主播的花费
     */
    private String cost;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createAt;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateAt;
}
