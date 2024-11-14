package cn.lary.module.auth.core;

import cn.lary.common.kit.StringKit;
import cn.lary.module.auth.kit.GlobalAuthKit;


/**
 * 授权配置类的校验器

 */
public class AuthChecker {

    /**
     * 是否支持第三方登录
     */
    public static boolean isSupportedAuth(AuthConfig config, AuthSource source) {
        return StringKit.isNotEmpty(config.getClientId())
            && StringKit.isNotEmpty(config.getClientSecret());
    }

    /**
     * 检查配置合法性。针对部分平台， 对redirect uri有特定要求。一般来说redirect uri都是http://，而对于facebook平台， redirect uri 必须是https的链接
     */
    public static void checkConfig(AuthConfig config, AuthSource source) {
        String redirectUri = config.getRedirectUri();
        if (config.isIgnoreCheckRedirectUri()) {
            return;
        }
        if (StringKit.isEmpty(redirectUri)) {
            throw new AuthException(AuthResponseStatus.ILLEGAL_REDIRECT_URI, source);
        }
        if (!GlobalAuthKit.isHttpProtocol(redirectUri) && !GlobalAuthKit.isHttpsProtocol(redirectUri)) {
            throw new AuthException(AuthResponseStatus.ILLEGAL_REDIRECT_URI, source);
        }
    }

    /**
     * 校验回调传回的code
     */
    public static void checkCode(AuthSource source, AuthCallback callback) {
        String code = callback.getCode();

        if (StringKit.isEmpty(code)) {
            throw new AuthException(AuthResponseStatus.ILLEGAL_CODE, source);
        }
    }

    /**
     * 校验回调传回的{@code state}，为空或者不存在
     */
    public static void checkState(String state, AuthSource source, AuthStateCache authStateCache) {
        if (StringKit.isEmpty(state) || !authStateCache.containsKey(state)) {
            throw new AuthException(AuthResponseStatus.ILLEGAL_STATUS, source);
        }
    }
}
