package cn.lary.module.user.service.impl;

import cn.lary.module.user.dto.UidQueryDTO;
import cn.lary.module.user.entity.UserIndex;
import cn.lary.module.user.mapper.UserIndexMapper;
import cn.lary.module.user.service.UserIndexService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author paul
 * @since 2024-11-17
 */
@Service
@RequiredArgsConstructor
public class UserIndexServiceImpl extends ServiceImpl<UserIndexMapper, UserIndex> implements UserIndexService {

    private final UserIndexMapper userIndexMapper;

    @Override
    public long uid(UidQueryDTO dto) {
        return userIndexMapper.getUid(dto);
    }
}
