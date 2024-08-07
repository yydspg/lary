package cn.lary.module.user.dto.res;

import cn.lary.core.dto.SingleResponse;
import cn.lary.module.user.entity.User;

public class LoginUserDetailRes extends SingleResponse {
    // TODO  :  这里需要结合自己的业务处理 逻辑
    public static LoginUserDetailRes build(User user) {
        LoginUserDetailRes loginUserDetailRes = new LoginUserDetailRes();
        return loginUserDetailRes;
    }
}
