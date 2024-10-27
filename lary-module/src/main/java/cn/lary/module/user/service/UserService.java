package cn.lary.module.user.service;

import cn.lary.common.dto.ResponsePair;
import cn.lary.external.wk.vo.route.RouteVO;
import cn.lary.module.user.dto.LoginDTO;
import cn.lary.module.user.dto.RegisterDTO;
import cn.lary.module.user.dto.UserDestroyDTO;
import cn.lary.module.user.dto.UserUpdateDTO;
import cn.lary.module.user.entity.User;
import cn.lary.module.user.vo.UserVO;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author paul
 * @since 2024-07-29
 */
public interface UserService extends IService<User> {
    /**
     * 登陆接口<br>
     * 如果从设备登陆,且为新设备,需要请求<br>
     * 获取code重新请求
     * @param dto {@link LoginDTO}
     * @return login token
     */
    ResponsePair<String> login(LoginDTO dto);

    /**
     * 注册用户
     * @param dto {@link RegisterDTO}
     * @return 验证码
     */
    ResponsePair<String> register(RegisterDTO dto);
    /**
     * 退出登陆<br>
     * 清理redis缓存
     * @return ok
     */
     ResponsePair<Void> logout(HttpServletRequest request);

    /**
     * 获取用户 IM 连接地址<br>
     * 此版本为固定值,后续根据底层IM服务优化
     * @return {@link RouteVO}
     */
     ResponsePair<RouteVO> getRoute();

    /**
     * 用户注册时的手机验证
     * @param phone 手机号
     * @return ok
     */
     ResponsePair<Void> registerCode(String phone);
    /**
     * 用户注销时的手机验证
     * @param phone 手机号
     * @return ok
     */
    ResponsePair<Void> destroyCode(String phone);

    /**
     * 刷新login的token<br>
     * 客户端的token刷新必须大于过期时间
     * @return ok
     */
     ResponsePair<Void> refresh(HttpServletRequest request);

    /**
     * 注销用户
     * @return ok
     */
    ResponsePair<Void> destroy(UserDestroyDTO dto);

    /**
     * 更新用户信息
     * @param dto {@link UserUpdateDTO}
     * @return {@link UserVO}
     */
    ResponsePair<UserVO> update(UserUpdateDTO dto);

    /**
     * 查询我的信息
     * @return {@link UserVO}
     */
    ResponsePair<UserVO> my();
    /**
     * 筛选有效用户
     * @param members m
     * @return IDs
     */
    List<Long> getValidUsers(List<Long> members);
}
