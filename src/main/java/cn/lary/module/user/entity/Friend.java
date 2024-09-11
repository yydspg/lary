package cn.lary.module.user.entity;

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
 * @since 2024-07-29
 */
@Getter
@Setter
@Accessors(chain = true)
public class Friend implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户UID
     */
    private String uid;

    /**
     * 好友uid
     */
    private String toUid;

    /**
     * 好友标示
     */
    private Short flag;

    /**
     * 版本号
     */
    private Long version;

    /**
     * 验证码 加好友来源
     */
    private String vercode;

    /**
     * 好友来源
     */
    private String sourceVercode;

    /**
     * 是否已删除
     */
    private Boolean isDeleted;

    /**
     * 单项好友
     */
    private Boolean isAlone;

    /**
     * 加好友发起方
     */
    private Boolean isInitiator;

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