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
@TableName("user_fan_level")
public class UserFanLevel implements Serializable {

    private static final long serialVersionUID = 1L;

    private String uid;

    private String anchorId;

    /**
     * 粉丝等级
     */
    private Short fanLevel;

    /**
     * 总计消费金额
     */
    private Long cost;

    /**
     * 粉丝等级图片路径
     */
    private String fanLevelUrl;

    private Boolean isDelete;

    private String createBy;

    private String updateBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createAt;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateAt;
}
