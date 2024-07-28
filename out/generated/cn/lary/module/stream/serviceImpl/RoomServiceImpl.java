package cn.lary.module.stream.serviceImpl;

import cn.lary.module.stream.entity.Room;
import cn.lary.module.stream.mapper.RoomMapper;
import cn.lary.module.stream.service.RoomService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author paul
 * @since 2024-07-29
 */
@Service
public class RoomServiceImpl extends ServiceImpl<RoomMapper, Room> implements RoomService {

}
