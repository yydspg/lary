package cn.lary.module.user.serviceImpl;

import cn.lary.module.user.entity.LoginLog;
import cn.lary.module.user.mapper.LoginLogMapper;
import cn.lary.module.user.service.LoginLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author paul
 * @since 2024-10-09
 */
@Service
public class LoginLogServiceImpl extends ServiceImpl<LoginLogMapper, LoginLog> implements LoginLogService {

}
