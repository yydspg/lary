package cn.lary.module.stream.serviceImpl;

import cn.lary.module.stream.entity.FollowApply;
import cn.lary.module.stream.entity.Room;
import cn.lary.module.stream.mapper.FollowApplyMapper;
import cn.lary.module.stream.mapper.RoomMapper;
import cn.lary.module.stream.service.FollowApplyService;
import cn.lary.module.stream.service.RoomService;
import cn.lary.module.user.mapper.UserMapper;
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
public class FollowApplyServiceImpl extends ServiceImpl<FollowApplyMapper, FollowApply> implements FollowApplyService {
    private final RoomService roomService;
    private final FollowApplyMapper followApplyMapper;


    @Override
    public List<String> getFollows(String uid) {
        Room room = roomService.getOne(new LambdaQueryWrapper<Room>().eq(Room::getUid, uid));
        if (room == null || room.getIsDelete() || room.getIsBlock() || room.getIsHot()) {
            return null;
        }
        return followApplyMapper.selectObjs(new LambdaQueryWrapper<FollowApply>().select(FollowApply::getToUid).eq(FollowApply::getUid, uid)
                .eq(FollowApply::getIsDelete, false).eq(FollowApply::getIsBlock, false));
    }

    @Override
    public boolean isBlock(String uid, String toUid) {
        FollowApply followApply = followApplyMapper.selectOne(new LambdaQueryWrapper<FollowApply>().eq(FollowApply::getUid, uid).eq(FollowApply::getToUid, toUid));
        return followApply != null && !followApply.getIsDelete() && followApply.getIsBlock();
    }

    @Override
    public boolean isFan(String uid, String toUid) {
        FollowApply followApply = followApplyMapper.selectOne(new LambdaQueryWrapper<FollowApply>().eq(FollowApply::getUid, uid).eq(FollowApply::getToUid, toUid));
        return followApply != null && !followApply.getIsDelete() && followApply.getIsBlock();
    }
}
