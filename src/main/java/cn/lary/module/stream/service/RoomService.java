package cn.lary.module.stream.service;

import cn.lary.core.dto.ResponsePair;
import cn.lary.module.stream.dto.GoLiveDTO;
import cn.lary.module.stream.entity.Room;
import cn.lary.module.stream.vo.DownLiveVO;
import cn.lary.module.stream.vo.GoLiveVO;
import cn.lary.module.stream.vo.JoinLiveVO;
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

    /**
     * 加入直播间
     * @param toUid 主播id
     * @param ip 用户ip
     * @return {@link JoinLiveVO}
     */
     ResponsePair<JoinLiveVO> join( int toUid, String ip);

    /**
     * 离开直播间
     * @return ok
     */
     ResponsePair<Void> leave();
    /**
     * 结束直播
     *
     * @return {@link DownLiveVO}
     */
    ResponsePair<DownLiveVO> end();

    /**
     * 开启直播
     *
     * @param ip  ip
     * @param dto {@link GoLiveDTO}
     * @return {@link GoLiveVO}
     */
    ResponsePair<GoLiveVO> go(String ip, GoLiveDTO dto);

    /**
     * 判断是否是热门主播
     * @param room {@link Room}
     * @return ok
     */
    boolean isHot(Room room) ;


}