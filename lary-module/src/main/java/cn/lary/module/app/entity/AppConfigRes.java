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
    private boolean newUserJoinSystemGroup;

    /**
     * 是否可通过手机号搜索
     */
    private boolean searchByPhone;

    /**
     * 是否开启注册邀请
     */
    private boolean registerInviteOn;

    /**
     * 是否开启登录欢迎语
     */
    private boolean sendWelcomeMessageOn;

    /**
     * 是否开启系统账号进入群聊
     */
    private boolean inviteSystemAccountJoinGroupOn;

    /**
     * 注册用户是否必须完善信息
     */
    private boolean registerUserMustCompleteInfoOn;

    /**
     * 频道最多置顶消息数量
     */
    private Short channelPinnedMessageMaxCount;

    public static AppConfigRes build(AppConfig appConfig) {
        AppConfigRes appConfigRes = new AppConfigRes();
        if (appConfig == null) {
            return appConfigRes;
        }
        appConfigRes.setRsaPublicKey(appConfig.getRsaPublicKey());
        appConfigRes.setVersion(appConfig.getVersion());
        appConfigRes.setSuperToken(appConfig.getSuperToken());
        appConfigRes.setSuperTokenOn(appConfig.getSuperTokenOn());
        appConfigRes.setWelcomeMessage(appConfig.getWelcomeMessage());
        appConfigRes.setNewUserJoinSystemGroup(appConfig.isNewUserJoinSystemGroup());
        appConfigRes.setSearchByPhone(appConfig.isSearchByPhone());
        appConfigRes.setRegisterInviteOn(appConfig.isRegisterInviteOn());
        appConfigRes.setSendWelcomeMessageOn(appConfig.isSendWelcomeMessageOn());
        appConfigRes.setInviteSystemAccountJoinGroupOn(appConfig.isInviteSystemAccountJoinGroupOn());
        appConfigRes.setRegisterUserMustCompleteInfoOn(appConfig.isRegisterUserMustCompleteInfoOn());
        appConfigRes.setChannelPinnedMessageMaxCount(appConfig.getChannelPinnedMessageMaxCount());
        return appConfigRes;
    }
}
