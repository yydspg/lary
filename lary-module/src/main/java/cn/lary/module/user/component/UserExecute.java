package cn.lary.module.user.component;

import cn.lary.common.dto.ResponsePair;
import cn.lary.module.user.dto.*;
import cn.lary.module.user.service.UserRedDotService;
import cn.lary.module.user.service.UserService;
import cn.lary.module.user.vo.UserRedDotVO;
import cn.lary.module.user.vo.UserVO;
import cn.lary.external.wk.vo.route.RouteVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserExecute {

    private final UserService userService;
    private final UserRedDotService userRedDotService;


    /**
     * 登陆接口<br>
     * 如果从设备登陆,且为新设备,需要请求{@link DeviceExecute} <br>
     * 获取code重新请求
     * @param dto {@link LoginDTO}
     * @return login token
     */
    public ResponsePair<String> login(LoginDTO dto) {
        return userService.login(dto);
    }

    /**
     * 注册接口<br>
     * 通过验证码校验后,直接执行登陆的逻辑
     * @param dto {@link RegisterDTO}
     * @return token
     */
    public ResponsePair<String> register(RegisterDTO dto) {
        return userService.register(dto);
    }
    /**
     * 注销接口<br>
     * 通过验证码校验后
     * @param dto {@link RegisterDTO}
     * @return token
     */
    public ResponsePair<Void> destroy(UserDestroyDTO dto) {
        return userService.destroy(dto);
    }
    /**
     * 用户注册时的手机验证
     * @param phone 手机号
     * @return ok
     */
    public ResponsePair<Void> registerCode(String phone) {
        return userService.registerCode(phone);
    }

    /**
     * 用户注销时的手机验证
     * @param phone 手机号
     * @return ok
     */
    public ResponsePair<Void> destroyCode(String phone) {
        return userService.destroyCode(phone);
    }

    /**
     * 刷新token
     * @return token
     */
    public ResponsePair<Void> refreshToken(HttpServletRequest request) {
       return userService.refresh(request);
    }

    /**
     * 退出登陆<br>
     * 清理 redis 缓存
     * @return ok
     */
    public ResponsePair<Void> logout(HttpServletRequest request) {
       return userService.logout(request);
    }

    /**
     * 获取用户 IM 连接地址<br>
     * 此版本为固定值,后续根据底层IM服务优化
     * @return {@link RouteVO}
     */
    public ResponsePair<RouteVO> getRoute() {
        return userService.getRoute();
    }

    /**
     * 获取用户红点<br>
     * 此表用户不同的类型就存在一条记录<br>
     * 所以后续增加策略要全表加
     * @return {@link UserRedDotVO}
     */
    public ResponsePair<List<UserRedDotVO>> getRedDot() {
        return userRedDotService.redDots();
    }

    /**
     * 获取我的信息
     * @return {@link UserVO}
     */
    public ResponsePair<UserVO> my(){
        return userService.my();
    }
}

