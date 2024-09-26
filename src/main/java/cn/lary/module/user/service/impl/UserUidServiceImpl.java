package cn.lary.module.user.service.impl;

import cn.lary.module.user.entity.UserUid;
import cn.lary.module.user.mapper.UserUidMapper;
import cn.lary.module.user.service.UserUidService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author paul
 * @since 2024-09-21
 */
@Service
public class UserUidServiceImpl extends ServiceImpl<UserUidMapper, UserUid> implements UserUidService {

    @Override
    public String getUid() {
        UserUid record = new UserUid();
        baseMapper.insert(record);
        return String.valueOf(record.getId());
    }


}
