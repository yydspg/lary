package cn.lary.module.app.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppConfigRes {

    private String rsaPublicKey;

    private Integer version;

    private String superToken;

    private Short superTokenOn;


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

    public static AppConfigRes build(AppConfig appConfig) {
        AppConfigRes appConfigRes = new AppConfigRes();
        appConfigRes.setRsaPublicKey(appConfig.getRsaPublicKey());
        appConfigRes.setVersion(appConfig.getVersion());
        appConfigRes.setSuperToken(appConfig.getSuperToken());
        appConfigRes.setSuperTokenOn(appConfig.getSuperTokenOn());
        appConfigRes.setWelcomeMessage(appConfig.getWelcomeMessage());
        appConfigRes.setNewUserJoinSystemGroup(appConfig.getNewUserJoinSystemGroup());
        appConfigRes.setSearchByPhone(appConfig.getSearchByPhone());
        appConfigRes.setRegisterInviteOn(appConfig.getRegisterInviteOn());
        appConfigRes.setSendWelcomeMessageOn(appConfig.getSendWelcomeMessageOn());
        appConfigRes.setInviteSystemAccountJoinGroupOn(appConfig.getInviteSystemAccountJoinGroupOn());
        appConfigRes.setRegisterUserMustCompleteInfoOn(appConfig.getRegisterUserMustCompleteInfoOn());
        appConfigRes.setChannelPinnedMessageMaxCount(appConfig.getChannelPinnedMessageMaxCount());
        return appConfigRes;
    }
}
