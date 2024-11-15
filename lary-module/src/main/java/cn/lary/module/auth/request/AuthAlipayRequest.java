package cn.lary.module.auth.request;

import cn.lary.module.auth.core.*;
import cn.lary.module.auth.kit.GlobalAuthKit;
import com.alibaba.fastjson2.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipaySystemOauthTokenRequest;
import com.alipay.api.request.AlipayUserInfoShareRequest;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import com.alipay.api.response.AlipayUserInfoShareResponse;
import cn.lary.common.kit.StringKit;

/**
 * 支付宝登录
 *
 * @author yadong.zhang (yadong.zhang0415(a)gmail.com)
 * @since 1.0.1
 */
public class AuthAlipayRequest extends AuthDefaultRequest {

    /**
     * 支付宝公钥：当选择支付宝登录时，该值可用
     * 对应“RSA2(SHA256)密钥”中的“支付宝公钥”
     */
    private final String alipayPublicKey;

    private final AlipayClient alipayClient;

    private static final String GATEWAY = "https://openapi.alipay.com/gateway.do";


    /**
     * 构造方法，需要设置"alipayPublicKey"
     *
     * @param config          公共的OAuth配置
     * @param alipayPublicKey 支付宝公钥
     */
    public AuthAlipayRequest(AuthConfig config, String alipayPublicKey) {
        super(config, AuthDefaultSource.ALIPAY);
        this.alipayPublicKey = alipayPublicKey;
        check(config);
        this.alipayClient = new DefaultAlipayClient(GATEWAY, config.getClientId(),
                config.getClientSecret(), "json", "UTF-8", this.alipayPublicKey, "RSA2");
    }

    public AuthAlipayRequest(AuthConfig config, String alipayPublicKey, AuthStateCache authStateCache) {
        super(config, AuthDefaultSource.ALIPAY, authStateCache);
        this.alipayPublicKey = alipayPublicKey;
        check(config);
        this.alipayClient = new DefaultAlipayClient(GATEWAY, config.getClientId(), config.getClientSecret(),
                "json", "UTF-8", this.alipayPublicKey, "RSA2");
    }

    public AuthAlipayRequest(AuthConfig config, String alipayPublicKey, AuthStateCache authStateCache, String proxyHost, Integer proxyPort) {
        super(config, AuthDefaultSource.ALIPAY, authStateCache);
        this.alipayPublicKey = alipayPublicKey;
        check(config);
        this.alipayClient = new DefaultAlipayClient(GATEWAY, config.getClientId(), config.getClientSecret(),
            "json", "UTF-8", this.alipayPublicKey, "RSA2", proxyHost, proxyPort);
    }

    protected void check(AuthConfig config) {
        AuthChecker.checkConfig(config, AuthDefaultSource.ALIPAY);

        if (!StringKit.isNotEmpty(alipayPublicKey)) {
            throw new AuthException(AuthResponseStatus.PARAMETER_INCOMPLETE, AuthDefaultSource.ALIPAY);
        }
        // 支付宝在创建回调地址时，不允许使用localhost或者127.0.0.1
        if (GlobalAuthKit.isLocalHost(config.getRedirectUri())) {
            // The redirect uri of alipay is forbidden to use localhost or 127.0.0.1
            throw new AuthException(AuthResponseStatus.ILLEGAL_REDIRECT_URI, AuthDefaultSource.ALIPAY);
        }
    }

    @Override
    protected void checkCode(AuthCallback authCallback) {
        if (StringKit.isEmpty(authCallback.getAuth_code())) {
            throw new AuthException(AuthResponseStatus.ILLEGAL_CODE, source);
        }
    }

    @Override
    protected AuthToken getAccessToken(AuthCallback authCallback) {
        AlipaySystemOauthTokenRequest request = new AlipaySystemOauthTokenRequest();
        request.setGrantType("authorization_code");
        request.setCode(authCallback.getAuth_code());
        AlipaySystemOauthTokenResponse response;
        try {
            response = this.alipayClient.execute(request);
        } catch (Exception e) {
            throw new AuthException(e);
        }
        if (!response.isSuccess()) {
            throw new AuthException(response.getSubMsg());
        }
        return AuthToken.builder()
            .accessToken(response.getAccessToken())
            .uid(response.getUserId())
            .expireIn(Integer.parseInt(response.getExpiresIn()))
            .refreshToken(response.getRefreshToken())
            .build();
    }

    /**
     * 刷新access srsToken （续期）
     *
     * @param authToken 登录成功后返回的Token信息
     * @return AuthResponse
     */
    @Override
    public AuthResponse refresh(AuthToken authToken) {
        AlipaySystemOauthTokenRequest request = new AlipaySystemOauthTokenRequest();
        request.setGrantType("refresh_token");
        request.setRefreshToken(authToken.getRefreshToken());
        AlipaySystemOauthTokenResponse response = null;
        try {
            response = this.alipayClient.execute(request);
        } catch (Exception e) {
            throw new AuthException(e);
        }
        if (!response.isSuccess()) {
            throw new AuthException(response.getSubMsg());
        }
        return AuthResponse.builder()
            .code(AuthResponseStatus.SUCCESS.getCode())
            .data(AuthToken.builder()
                .accessToken(response.getAccessToken())
                .uid(response.getUserId())
                .expireIn(Integer.parseInt(response.getExpiresIn()))
                .refreshToken(response.getRefreshToken())
                .build())
            .build();
    }

    @Override
    protected AuthUser getUserInfo(AuthToken authToken) {
        String accessToken = authToken.getAccessToken();
        AlipayUserInfoShareRequest request = new AlipayUserInfoShareRequest();
        AlipayUserInfoShareResponse response = null;
        try {
            response = this.alipayClient.execute(request, accessToken);
        } catch (AlipayApiException e) {
            throw new AuthException(e.getErrMsg(), e);
        }
        if (!response.isSuccess()) {
            throw new AuthException(response.getSubMsg());
        }

        String province = response.getProvince(), city = response.getCity();
        String location = String.format("%s %s", StringKit.isEmpty(province) ? "" : province, StringKit.isEmpty(city) ? "" : city);

        return AuthUser.builder()
            .rawUserInfo(JSONObject.parseObject(JSONObject.toJSONString(response)))
            .uuid(response.getUserId())
            .username(StringKit.isEmpty(response.getUserName()) ? response.getNickName() : response.getUserName())
            .nickname(response.getNickName())
            .avatar(response.getAvatar())
            .location(location)
            .gender(AuthUserGender.getRealGender(response.getGender()))
            .token(authToken)
            .source(source.toString())
            .build();
    }

    /**
     * 返回带{@code state}参数的授权url，授权回调时会带上这个{@code state}
     * @param state state 验证授权流程的参数，可以防止csrf
     */
    @Override
    public String authorize(String state) {
        return UrlBuilder.fromBaseUrl(source.authorize())
            .param("app_id", config.getClientId())
            .param("scope", "auth_user")
            .param("redirect_uri", config.getRedirectUri())
            .param("state", getRealState(state))
            .build();
    }
}
