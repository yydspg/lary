package cn.lary.api.user.entity;

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
 * @since 2024-08-07
 */
@Getter
@Setter
@Accessors(chain = true)
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * uid
     */
    private Long uid;
    /**
     * setting id
     */
    private Long sid;
    /**
     * 直播间id
     */
    private Long rid;
    /**
     * 钱包id
     */
    private Long wid;

    /**
     * 用户名
     */
    private String username;

    private String phone;

    /**
     * Oath 登陆
     */
    private String eid;

    private String password;

    /**
     * 1 is man,2 is woman
     */
    private Integer sex;


    private String birthday;

    private String zone;

    private String avatar;

    private String regin;

    private String bio;

    private String vercode;

    private String qrVercode;

    private Integer level;

    private String email;

    private Integer role;

    private Integer status;

    private String home;
    /**
     * 短编码
     */
    private String shortNo;

    private long createAt;
}
