package cn.lary.module.auth.request;

import cn.lary.common.kit.StringKit;
import cn.lary.module.auth.LaryHttpManager;
import cn.lary.module.auth.core.*;
import cn.lary.module.auth.kit.GlobalAuthKit;
import com.alibaba.fastjson2.JSONObject;


/**
 * 淘宝
 */
public class AuthTaobaoRequest extends AuthDefaultRequest {

    public AuthTaobaoRequest(AuthConfig config) {
        super(config, AuthDefaultSource.TAOBAO);
    }

    public AuthTaobaoRequest(AuthConfig config, AuthStateCache authStateCache) {
        super(config, AuthDefaultSource.TAOBAO, authStateCache);
    }

    @Override
    protected AuthToken getAccessToken(AuthCallback authCallback) {
        return AuthToken.builder().accessCode(authCallback.getCode()).build();
    }

    private AuthToken getAuthToken(JSONObject object) {
        this.checkResponse(object);

        return AuthToken.builder()
            .accessToken(object.getString("access_token"))
            .expireIn(object.getIntValue("expires_in"))
            .refreshToken(object.getString("refresh_token"))
            .uid(object.getString("taobao_user_id"))
            .openId(object.getString("taobao_open_uid"))
            .build();
    }

    private void checkResponse(JSONObject object) {
        if (object.containsKey("error")) {
            throw new AuthException(object.getString("error_description"));
        }
    }

    @Override
    protected AuthUser getUserInfo(AuthToken authToken) {
        String response = doPostAuthorizationCode(authToken.getAccessCode());
        JSONObject accessTokenObject = JSONObject.parseObject(response);
        if (accessTokenObject.containsKey("error")) {
            throw new AuthException(accessTokenObject.getString("error_description"));
        }
        authToken = this.getAuthToken(accessTokenObject);

        String nick = GlobalAuthKit.urlDecode(accessTokenObject.getString("taobao_user_nick"));
        return AuthUser.builder()
            .rawUserInfo(accessTokenObject)
            .uuid(StringKit.isEmpty(authToken.getUid()) ? authToken.getOpenId() : authToken.getUid())
            .username(nick)
            .nickname(nick)
            .gender(AuthUserGender.UNKNOWN)
            .token(authToken)
            .source(source.toString())
            .build();
    }

    @Override
    public AuthResponse refresh(AuthToken oldToken) {
        String tokenUrl = refreshTokenUrl(oldToken.getRefreshToken());
        String response = LaryHttpManager.get(tokenUrl);
        JSONObject accessTokenObject = JSONObject.parseObject(response);
        return AuthResponse.builder()
            .code(AuthResponseStatus.SUCCESS.getCode())
            .data(this.getAuthToken(accessTokenObject))
            .build();
    }

    @Override
    public String authorize(String state) {
        return UrlBuilder.fromBaseUrl(source.authorize())
            .param("response_type", "code")
            .param("client_id", config.getClientId())
            .param("redirect_uri", config.getRedirectUri())
            .param("view", "web")
            .param("state", getRealState(state))
            .build();
    }
}
