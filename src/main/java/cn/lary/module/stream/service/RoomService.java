package cn.lary.module.stream.service;

import cn.lary.module.stream.entity.Room;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author paul
 * @since 2024-08-16
 */
public interface RoomService extends IService<Room> {
    boolean isAnchor(String uid);
}
