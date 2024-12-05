package cn.lary.api.user.entity;

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
     * login id
     */
    private Long lid;

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
    /**
     * 状态
     */
    private Integer status;
    /**
     * create time
     */
    private Long createAt;

}
