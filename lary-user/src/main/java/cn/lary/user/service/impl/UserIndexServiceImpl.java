package cn.lary.user.service.impl;

import cn.lary.api.user.dto.UidQueryDTO;
import cn.lary.api.user.entity.UserIndex;
import cn.lary.user.mapper.UserIndexMapper;
import cn.lary.user.service.UserIndexService;
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
