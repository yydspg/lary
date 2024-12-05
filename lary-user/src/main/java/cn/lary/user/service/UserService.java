package cn.lary.user.service;

import cn.lary.common.dto.ResponsePair;
import cn.lary.api.user.dto.LoginDTO;
import cn.lary.api.user.dto.RegisterDTO;
import cn.lary.api.user.dto.UserDestroyDTO;
import cn.lary.api.user.dto.UserUpdateDTO;
import cn.lary.api.user.entity.User;
import cn.lary.api.user.vo.UserVO;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
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
     * 虚拟角色
     * @param user {@link User}
     * @return user
     */
    User virtual(User user);


    /**
     * 登陆接口<br>
     * 如果从设备登陆,且为新设备,需要请求<br>
     * 获取code重新请求
     * @param dto {@link LoginDTO}
     * @return login srsToken
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
     * @return OK
     */
     ResponsePair<Void> logout(HttpServletRequest request);

//    /**
//     * 获取用户 IM 连接地址<br>
//     * 此版本为固定值,后续根据底层IM服务优化
//     * @return {@link RouteVO}
//     */
//     ResponsePair<RouteVO> getRoute();

    /**
     * 用户注册时的手机验证
     * @param phone 手机号
     * @return OK
     */
     ResponsePair<Void> registerCode(String phone,String name,int flag);
    /**
     * 用户注销时的手机验证
     * @param phone 手机号
     * @return OK
     */
    ResponsePair<Void> destroyCode(String phone,String name,int flag);

    /**
     * 刷新login的token<br>
     * 客户端的token刷新必须大于过期时间
     * @return OK
     */
     ResponsePair<Void> refresh(HttpServletRequest request);

    /**
     * 注销用户
     * @return OK
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
