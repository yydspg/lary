package cn.lary.module.app.entity;

import com.baomidou.mybatisplus.annotation.IdType;
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
 * @since 2024-08-03
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("app_config")
public class AppConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String rsaPrivateKey;

    private String rsaPublicKey;

    private Integer version;

    private String superToken;

    private Short superTokenOn;

    private LocalDateTime createAt;

    private LocalDateTime updateAt;

    /**
     * 消息可撤回时长
     */
    private Short revokeSecond;

    /**
     * 登录欢迎语
     */
    private String welcomeMessage;

    /**
     * 注册用户是否默认加入系统群
     */
    private Short newUserJoinSystemGroup;

    /**
     * 是否可通过手机号搜索
     */
    private Short searchByPhone;

    /**
     * 是否开启注册邀请
     */
    private Short registerInviteOn;

    /**
     * 是否开启登录欢迎语
     */
    private Short sendWelcomeMessageOn;

    /**
     * 是否开启系统账号进入群聊
     */
    private Short inviteSystemAccountJoinGroupOn;

    /**
     * 注册用户是否必须完善信息
     */
    private Short registerUserMustCompleteInfoOn;

    /**
     * 频道最多置顶消息数量
     */
    private Short channelPinnedMessageMaxCount;

    /**
     * 是否能修改服务器地址
     */
    private Short canModifyApiUrl;
}
