package cn.lary.module.stream.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
 * @since 2024-09-04
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("follow")
public class Follow implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Integer uid;

    /**
     * 被关注id
     */
    private Integer toUid;

    /**
     * 被关注用户名
     */
    private String username;
    /**
     * 业务来源
     */
    private Integer source;

    /**
     * 用户简介
     */
    private String bio;

    /**
     * 备注
     */
    private String remark;
    /**
     * 头像地址
     */
    private String avatarUrl;

    /**
     * 是否是主播
     */
    private Boolean isAnchor;
    /**
     * 是否拉黑
     */
    private Boolean isBlock;
    /**
     * 是否取消关注
     */
    private Boolean isUnfollow;
    /**
     * 是否单向关注
     */
    private Boolean isOneWay;


    private Boolean isDelete;

    private Integer level;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createAt;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateAt;
}
