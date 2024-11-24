package cn.lary.module.auth.core;

import com.alibaba.fastjson2.JSONObject;
import lombok.*;

import java.io.Serializable;



@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthUser implements Serializable {

    private String uuid;
    /**
     * 用户名
     */
    private String username;
    /**
     * 用户昵称
     */
    private String nickname;
    /**
     * 用户头像
     */
    private String avatar;
    /**
     * 用户网址
     */
    private String blog;
    /**
     * 所在公司
     */
    private String company;
    /**
     * 位置
     */
    private String location;
    /**
     * 用户邮箱
     */
    private String email;
    /**
     * 用户备注（各平台中的用户个人介绍）
     */
    private String remark;
    /**
     * 性别
     */
    private AuthUserGender gender;
    /**
     * 用户来源
     */
    private String source;
    /**
     * 用户授权的token信息
     */
    private AuthToken token;
    /**
     * 第三方平台返回的原始用户信息
     */
    private JSONObject rawUserInfo;

    /**
     * 微信公众号 - 网页授权的登录时可用
     *
     * 微信针对网页授权登录，增加了一个快照页的逻辑，快照页获取到的微信用户的 uid oid 和头像昵称都是虚拟的信息
     */
    private boolean snapshotUser;

}
