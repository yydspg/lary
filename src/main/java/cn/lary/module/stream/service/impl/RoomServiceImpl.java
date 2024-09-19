package cn.lary.module.stream.service.impl;

import cn.lary.module.stream.entity.Room;
import cn.lary.module.stream.mapper.RoomMapper;
import cn.lary.module.stream.service.RoomService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author paul
 * @since 2024-08-16
 */
@Service
@RequiredArgsConstructor
public class RoomServiceImpl extends ServiceImpl<RoomMapper, Room> implements RoomService {

    private final RoomMapper roomMapper;


    @Override
    public boolean isAnchor(String uid) {
        LambdaQueryWrapper<Room> qw = new LambdaQueryWrapper<Room>().eq(Room::getUid, uid).eq(Room::getIsDelete, false).eq(Room::getIsBlock, false);
        return roomMapper.exists(qw);
    }
}
