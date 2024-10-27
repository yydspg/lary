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
 * @since 2024-10-07
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("login_log")
public class LoginLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    private Long uid;

    /**
     * 最后一次登录ip
     */
    private String ip;

    /**
     * 设备名称
     */
    private String name;

    /**
     * 设备模型
     */
    private Integer flag;

    private Integer level;

    private Boolean isDelete;


    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createAt;

}
