package cn.lary.module.stream.service.impl;

import cn.lary.module.stream.entity.Follow;
import cn.lary.module.stream.entity.Room;
import cn.lary.module.stream.mapper.FollowMapper;
import cn.lary.module.stream.service.FollowService;
import cn.lary.module.stream.service.RoomService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author paul
 * @since 2024-09-04
 */
@Service
@RequiredArgsConstructor
public class FollowServiceImpl extends ServiceImpl<FollowMapper, Follow> implements FollowService {
    private final RoomService roomService;
    private final FollowMapper followMapper;


    @Override
    public List<String> getFollows(String uid) {
        Room room = roomService.getOne(new LambdaQueryWrapper<Room>().eq(Room::getUid, uid));
        if (room == null || room.getIsDelete() || room.getIsBlock() || room.getIsHot()) {
            return null;
        }
        return followMapper.selectObjs(new LambdaQueryWrapper<Follow>().select(Follow::getToUid).eq(Follow::getUid, uid)
                .eq(Follow::getIsDelete, false).eq(Follow::getIsBlock, false));
    }

    @Override
    public boolean isBlock(String uid, String toUid) {
        Follow follow = followMapper.selectOne(new LambdaQueryWrapper<Follow>().eq(Follow::getUid, uid).eq(Follow::getToUid, toUid));
        return follow != null && !follow.getIsDelete() && follow.getIsBlock();
    }

    @Override
    public boolean isFan(String uid, String toUid) {
        Follow follow = followMapper.selectOne(new LambdaQueryWrapper<Follow>().eq(Follow::getUid, uid).eq(Follow::getToUid, toUid));
        return follow != null && !follow.getIsDelete() && follow.getIsBlock();
    }
}
