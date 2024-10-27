package cn.lary.module.user.entity;

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
@TableName("user_setting")
public class UserSetting implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long uid;

    /**
     * 粉丝列表是否展示
     */
    private Boolean fanList;

    /**
     * 勋章是否展示
     */
    private Boolean medal;

    /**
     * 动态是否展示
     */
    private Boolean dynamic;

    /**
     * 新消息提醒
     */
    private Boolean newMessageNotice;

    private Boolean isDelete;


    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createAt;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateAt;
}
