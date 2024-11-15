package cn.lary.module.auth.core;


public interface AuthRequest {
    /**
     * 返回带{@code state}参数的授权url，授权回调时会带上这个{@code state}
     */
    default String authorize(String state) {
        throw new AuthException(AuthResponseStatus.NOT_IMPLEMENTED);
    }

    /**
     * 第三方登录
     */
    default AuthResponse login(AuthCallback authCallback) {
        throw new AuthException(AuthResponseStatus.NOT_IMPLEMENTED);
    }

    /**
     * 撤销授权
     */
    default AuthResponse revoke(AuthToken authToken) {
        throw new AuthException(AuthResponseStatus.NOT_IMPLEMENTED);
    }

    /**
     * 刷新access srsToken （续期）
     */
    default AuthResponse refresh(AuthToken authToken) {
        throw new AuthException(AuthResponseStatus.NOT_IMPLEMENTED);
    }
}
