package cn.lary.module.user.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
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
 * @since 2024-07-29
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("user_home_setting")
public class UserHomeSetting implements Serializable {

    private static final long serialVersionUID = 1L;

    private String uid;

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

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createAt;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateAt;
}
