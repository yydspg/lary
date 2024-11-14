package cn.lary.module.auth.core;


import cn.lary.common.kit.StringKit;
import lombok.*;

import java.util.List;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthConfig {

    /**
     * 客户端id：对应各平台的appKey
     */
    private String clientId;

    /**
     * 客户端Secret：对应各平台的appSecret
     */
    private String clientSecret;

    /**
     * 登录成功后的回调地址
     */
    private String redirectUri;


    /**
     * 企业微信，授权方的网页应用ID
     */
    private String agentId;

    /**
     * 企业微信第三方授权用户类型，member|admin
     */
    private String usertype;


    /**
     * 忽略校验 {@code state} 参数，默认不开启。当 {@code ignoreCheckState} 为 {@code true} 时，
     * {@link me.zhyd.oauth.request.AuthDefaultRequest#login(AuthCallback)} 将不会校验 {@code state} 的合法性。
     * <p>
     * 使用场景：当且仅当使用自实现 {@code state} 校验逻辑时开启
     * <p>
     * 以下场景使用方案仅作参考：
     * 1. 授权、登录为同端，并且全部使用 JustAuth 实现时，该值建议设为 {@code false};
     * 2. 授权和登录为不同端实现时，比如前端页面拼装 {@code authorizeUrl}，并且前端自行对{@code state}进行校验，
     * 后端只负责使用{@code code}获取用户信息时，该值建议设为 {@code true};
     *
     * <strong>如非特殊需要，不建议开启这个配置</strong>
     * <p>
     * 该方案主要为了解决以下类似场景的问题：
     *
     * @see <a href="https://github.com/justauth/JustAuth/issues/83">https://github.com/justauth/JustAuth/issues/83</a>
     * @since 1.15.6
     */
    private boolean ignoreCheckState;

    /**
     * 支持自定义授权平台的 scope 内容
     */
    private List<String> scopes;

    /**
     * 设备ID, 设备唯一标识ID
     */
    private String deviceId;


    /**
     * 是否开启 PKCE 模式，该配置仅用于支持 PKCE 模式的平台，针对无服务应用，不推荐使用隐式授权，推荐使用 PKCE 模式
     */
    private boolean pkce;

    /**
     * Okta 授权服务器的 ID， 默认为 default。如果要使用自定义授权服务，此处传实际的授权服务器 ID（一个随机串）
     */
    private String authServerId;
    /**
     * 忽略校验 {@code redirectUri} 参数，默认不开启。当 {@code ignoreCheckRedirectUri} 为 {@code true} 时，
     */
    private boolean ignoreCheckRedirectUri;

    /**
     * 适配 builder 模式 set 值的情况
     */
    public String getAuthServerId() {
        return StringKit.isEmpty(authServerId) ? "default" : authServerId;
    }

}
