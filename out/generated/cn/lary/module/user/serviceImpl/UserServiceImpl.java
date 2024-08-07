package cn.lary.module.user.serviceImpl;

import cn.lary.module.user.entity.User;
import cn.lary.module.user.mapper.UserMapper;
import cn.lary.module.user.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author paul
 * @since 2024-08-07
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
