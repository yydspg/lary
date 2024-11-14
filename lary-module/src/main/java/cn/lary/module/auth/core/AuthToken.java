package cn.lary.module.auth.core;

import lombok.*;

import java.io.Serializable;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthToken implements Serializable {

    private String accessToken;
    private int expireIn;
    private String refreshToken;
    private int refreshTokenExpireIn;
    private String uid;
    private String openId;
    private String accessCode;
    private String unionId;


    /**
     * 企业微信附带属性
     */
    private String code;
    /**
     * 微信公众号 - 网页授权的登录时可用
     * 微信针对网页授权登录，增加了一个快照页的逻辑，快照页获取到的微信用户的 uid oid 和头像昵称都是虚拟的信息
     */
    private boolean snapshotUser;


}
