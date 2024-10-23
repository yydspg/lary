package cn.lary.module.user.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
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
 * @since 2024-08-07
 */
@Getter
@Setter
@Accessors(chain = true)
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer uid;

    private String name;

    /**
     * 短编码
     */
    private String shortNo;

    /**
     * 0 is man,1 is woman
     */
    private Integer sex;

//    private String username;

    private String password;


    private Boolean isUploadAvatar;

    private String birthday;

    private String zone;

    private String phone;

    private String regin;

    private Boolean isRobot;

    private String bio;

    private String vercode;

    private String qrVercode;

    private Integer level;

    private String email;

    private String avatar;

    private Integer role;

    private Integer status;

    private Boolean isDelete;

    private Boolean isAnchor;

    private String anchorAnnounce;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createAt;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateAt;
}
