package cn.lary.module.auth.core;


import cn.lary.module.auth.request.*;

public enum AuthDefaultSource implements AuthSource {

    /**
     * 支付宝
     */
    ALIPAY {
        @Override
        public String authorize() {
            return "https://openauth.alipay.com/oauth2/publicAppAuthorize.htm";
        }

        @Override
        public String accessToken() {
            return "https://openapi.alipay.com/gateway.do";
        }

        @Override
        public String userInfo() {
            return "https://openapi.alipay.com/gateway.do";
        }

        @Override
        public Class<? extends AuthDefaultRequest> getTargetClass() {
            return AuthAlipayRequest.class;
        }
    },

    /**
     * 微信开放平台
     */
    WECHAT_OPEN {
        @Override
        public String authorize() {
            return "https://open.weixin.qq.com/connect/qrconnect";
        }

        @Override
        public String accessToken() {
            return "https://api.weixin.qq.com/sns/oauth2/access_token";
        }

        @Override
        public String userInfo() {
            return "https://api.weixin.qq.com/sns/userinfo";
        }

        @Override
        public String refresh() {
            return "https://api.weixin.qq.com/sns/oauth2/refresh_token";
        }

        @Override
        public Class<? extends AuthDefaultRequest> getTargetClass() {
            return AuthWeChatOpenRequest.class;
        }
    },

    /**
     * 淘宝
     */
    TAOBAO {
        @Override
        public String authorize() {
            return "https://oauth.taobao.com/authorize";
        }

        @Override
        public String accessToken() {
            return "https://oauth.taobao.com/token";
        }

        @Override
        public String userInfo() {
            throw new AuthException(AuthResponseStatus.UNSUPPORTED);
        }

        @Override
        public Class<? extends AuthDefaultRequest> getTargetClass() {
            return AuthTaobaoRequest.class;
        }
    },

    /**
     * 美团
     */
    MEITUAN {
        @Override
        public String authorize() {
            return "https://openapi.waimai.meituan.com/oauth/authorize";
        }

        @Override
        public String accessToken() {
            return "https://openapi.waimai.meituan.com/oauth/access_token";
        }

        @Override
        public String userInfo() {
            return "https://openapi.waimai.meituan.com/oauth/userinfo";
        }

        @Override
        public String refresh() {
            return "https://openapi.waimai.meituan.com/oauth/refresh_token";
        }

        @Override
        public Class<? extends AuthDefaultRequest> getTargetClass() {
            return AuthMeituanRequest.class;
        }
    },




}
