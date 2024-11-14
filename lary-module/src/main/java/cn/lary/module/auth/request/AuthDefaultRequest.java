package cn.lary.module.auth.request;



import cn.lary.common.kit.StringKit;
import cn.lary.common.kit.UUIDKit;
import cn.lary.module.auth.LaryHttpManager;
import cn.lary.module.auth.core.*;

import java.util.List;


public abstract class AuthDefaultRequest implements AuthRequest {
    protected AuthConfig config;
    protected AuthSource source;
    protected AuthStateCache authStateCache;

    public AuthDefaultRequest(AuthConfig config, AuthSource source) {
        this(config, source, AuthDefaultStateCache.INSTANCE);
    }

    public AuthDefaultRequest(AuthConfig config, AuthSource source, AuthStateCache authStateCache) {
        this.config = config;
        this.source = source;
        this.authStateCache = authStateCache;
        if (!AuthChecker.isSupportedAuth(config, source)) {
            throw new AuthException(AuthResponseStatus.PARAMETER_INCOMPLETE, source);
        }
        // 校验配置合法性
        AuthChecker.checkConfig(config, source);
    }

    /**
     * 获取access token
     *
     * @param authCallback 授权成功后的回调参数
     * @return token
     * @see AuthDefaultRequest#authorize(String)
     */
    protected abstract AuthToken getAccessToken(AuthCallback authCallback);

    /**
     * 使用token换取用户信息
     *
     * @param authToken token信息
     * @return 用户信息
     * @see AuthDefaultRequest#getAccessToken(AuthCallback)
     */
    protected abstract AuthUser getUserInfo(AuthToken authToken);

    /**
     * 统一的登录入口。当通过{@link AuthDefaultRequest#authorize(String)}授权成功后，会跳转到调用方的相关回调方法中
     * 方法的入参可以使用{@code AuthCallback}，{@code AuthCallback}类中封装好了OAuth2授权回调所需要的参数
     *
     * @param authCallback 用于接收回调参数的实体
     * @return AuthResponse
     */
    @Override
    public AuthResponse login(AuthCallback authCallback) {
        try {
            checkCode(authCallback);
            if (!config.isIgnoreCheckState()) {
                AuthChecker.checkState(authCallback.getState(), source, authStateCache);
            }

            AuthToken authToken = this.getAccessToken(authCallback);
            AuthUser user = this.getUserInfo(authToken);
            return AuthResponse.builder().code(AuthResponseStatus.SUCCESS.getCode()).data(user).build();
        } catch (Exception e) {
            return this.responseError(e);
        }
    }

    protected void checkCode(AuthCallback authCallback) {
        AuthChecker.checkCode(source, authCallback);
    }

    /**
     * 处理{@link AuthDefaultRequest#login(AuthCallback)} 发生异常的情况，统一响应参数
     *
     * @param e 具体的异常
     * @return AuthResponse
     */
    AuthResponse responseError(Exception e) {
        int errorCode = AuthResponseStatus.FAILURE.getCode();
        String errorMsg = e.getMessage();
        if (e instanceof AuthException) {
            AuthException authException = ((AuthException) e);
            errorCode = authException.getErrorCode();
            if (StringKit.isNotEmpty(authException.getErrorMsg())) {
                errorMsg = authException.getErrorMsg();
            }
        }
        return AuthResponse.builder().code(errorCode).msg(errorMsg).build();
    }



    /**
     * 返回带{@code state}参数的授权url，授权回调时会带上这个{@code state}
     *
     * @param state state 验证授权流程的参数，可以防止csrf
     * @return 返回授权地址
     * @since 1.9.3
     */
    @Override
    public String authorize(String state) {
        return UrlBuilder.fromBaseUrl(source.authorize())
                .param("response_type", "code")
                .param("client_id", config.getClientId())
                .param("redirect_uri", config.getRedirectUri())
                .param("state", getRealState(state))
                .build();
    }

    /**
     * 返回获取accessToken的url
     * @param code 授权码
     * @return 返回获取accessToken的url
     */
    protected String accessTokenUrl(String code) {
        return UrlBuilder.fromBaseUrl(source.accessToken())
                .param("code", code)
                .param("client_id", config.getClientId())
                .param("client_secret", config.getClientSecret())
                .param("grant_type", "authorization_code")
                .param("redirect_uri", config.getRedirectUri())
                .build();
    }

    /**
     * 返回获取accessToken的url
     *
     * @param refreshToken refreshToken
     * @return 返回获取accessToken的url
     */
    protected String refreshTokenUrl(String refreshToken) {
        return UrlBuilder.fromBaseUrl(source.refresh())
                .param("client_id", config.getClientId())
                .param("client_secret", config.getClientSecret())
                .param("refresh_token", refreshToken)
                .param("grant_type", "refresh_token")
                .param("redirect_uri", config.getRedirectUri())
                .build();
    }

    /**
     * 返回获取userInfo的url
     *
     * @param authToken token
     * @return 返回获取userInfo的url
     */
    protected String userInfoUrl(AuthToken authToken) {
        return UrlBuilder.fromBaseUrl(source.userInfo()).param("access_token", authToken.getAccessToken()).build();
    }

    /**
     * 返回获取revoke authorization的url
     *
     * @param authToken token
     * @return 返回获取revoke authorization的url
     */
    protected String revokeUrl(AuthToken authToken) {
        return UrlBuilder.fromBaseUrl(source.revoke()).param("access_token", authToken.getAccessToken()).build();
    }

    /**
     * 获取state，如果为空， 则默认取当前日期的时间戳
     *
     * @param state 原始的state
     * @return 返回不为null的state
     */
    protected String getRealState(String state) {
        if (StringKit.isEmpty(state)) {
            state = UUIDKit.getUUID();
        }
        // 缓存state
        authStateCache.cache(state, state);
        return state;
    }

    /**
     * authorizationCode
     */
    protected String doPostAuthorizationCode(String code) {
        return LaryHttpManager.post(accessTokenUrl(code));
    }

    /**
     *authorizationCode
     */
    protected String doGetAuthorizationCode(String code) {
        return LaryHttpManager.get(accessTokenUrl(code));
    }

    /**
     * user info
     */
    protected String doGetUserInfo(AuthToken authToken) {
        return LaryHttpManager.get(userInfoUrl(authToken));
    }


    /**
     * 取消授权
     */
    protected String doGetRevoke(AuthToken authToken) {
        return LaryHttpManager.get(revokeUrl(authToken));
    }

    /**
     * 获取以 {@code separator}分割过后的 scope 信息
     *
     * @param separator     多个 {@code scope} 间的分隔符
     * @param encode        是否 encode 编码
     * @param defaultScopes 默认的 scope， 当客户端没有配置 {@code scopes} 时启用
     * @return String
     * @since 1.16.7
     */
    protected String getScopes(String separator, boolean encode, List<String> defaultScopes) {
        List<String> scopes = config.getScopes();
        if (null == scopes || scopes.isEmpty()) {
            if (null == defaultScopes || defaultScopes.isEmpty()) {
                return "";
            }
            scopes = defaultScopes;
        }
        if (null == separator) {
            // 默认为空格
            separator = " ";
        }
        String scopeStr = String.join(separator, scopes);
//        return encode ? UrlUtil.urlEncode(scopeStr) : scopeStr;
        return null;
    }


}
