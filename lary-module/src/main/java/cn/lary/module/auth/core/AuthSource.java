package cn.lary.module.auth.core;

import cn.lary.module.auth.request.AuthDefaultRequest;

public interface AuthSource {
    /**
     * 授权的api
     */
    String authorize();

    /**
     * 获取accessToken的api
     */
    String accessToken();

    /**
     * 获取用户信息的api
     */
    String userInfo();

    /**
     * 取消授权的api
     */
    default String revoke() {
        throw new AuthException(AuthResponseStatus.UNSUPPORTED);
    }

    /**
     * 刷新授权的api
     */
    default String refresh() {
        throw new AuthException(AuthResponseStatus.UNSUPPORTED);
    }

    /**
     * 获取Source的字符串名字
     */
    default String getName() {
        if (this instanceof Enum) {
            return String.valueOf(this);
        }
        return this.getClass().getSimpleName();
    }


    Class<? extends AuthDefaultRequest> getTargetClass();
}